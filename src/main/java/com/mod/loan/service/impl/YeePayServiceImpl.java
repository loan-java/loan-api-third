package com.mod.loan.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.PaymentTypeEnum;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.message.OrderRepayQueryMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.model.*;
import com.mod.loan.model.vo.UserBankInfoVO;
import com.mod.loan.service.OrderRepayService;
import com.mod.loan.service.UserBankService;
import com.mod.loan.service.UserService;
import com.mod.loan.service.YeePayService;
import com.mod.loan.util.StringUtil;
import com.mod.loan.util.yeepay.StringResultDTO;
import com.mod.loan.util.yeepay.YeePayApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liujianjian
 * @date 2019/6/15 20:55
 */
@Slf4j
@Service
public class YeePayServiceImpl implements YeePayService {

    @Resource
    private UserService userService;
    @Resource
    private UserBankService userBankService;
    @Resource
    private OrderRepayService orderRepayService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisMapper redisMapper;

    /**
     * 绑卡
     */
    @Override
    public ResultMessage requestBindCard(Long uid, String cardNo, String cardPhone, Bank bank) {
        try {
            User user = userService.selectByPrimaryKey(uid);
            if (user == null) {
                throw new BizException("用户(" + uid + ")不存在");
            }
            String timeStr = System.currentTimeMillis() + "";
            String orderNo = StringUtil.getOrderNumber("c");
            String identityid = ("dev".equals(Constant.ENVIROMENT) ? "YBTest" : "YB") + uid;
            String idcardno = user.getUserCertNo();
            String username = user.getUserName();

            StringResultDTO resultDTO = YeePayApiRequest.bindCardRequest(orderNo, identityid, cardNo, idcardno, username, cardPhone);
            if ("FAIL".equals(resultDTO.getStatus()) || "TIME_OUT".equals(resultDTO.getStatus())) {
                log.info("易宝绑卡请求异常，uid={}, errorcode={}, error={}", uid, resultDTO.getErrorcode(), resultDTO.getErrormsg());
                return new ResultMessage(ResponseEnum.M4000.getCode(), "易宝绑卡请求失败: " + resultDTO.getErrormsg());
            }

            UserBankInfoVO userBankInfoVO = new UserBankInfoVO();
            userBankInfoVO.setUid(uid);
            userBankInfoVO.setCardCode(bank.getCode());
            userBankInfoVO.setCardName(bank.getBankName());
            userBankInfoVO.setCardNo(cardNo);
            userBankInfoVO.setCardPhone(cardPhone);
            redisMapper.set(RedisConst.user_bank_bind + uid, userBankInfoVO, 600);
        } catch (Exception e) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "易宝绑卡请求失败: " + e.getMessage());
        }
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 确认绑卡
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultMessage confirmBindCard(String validateCode, Long uid, UserBankInfoVO userBankInfoVO) {
        try {
            String orderNo = redisMapper.get(RedisConst.user_bank_bind + uid);
            StringResultDTO result = YeePayApiRequest.bindCardConfirm(orderNo, validateCode);
            if (!"BIND_SUCCESS".equalsIgnoreCase(result.getStatus())) {
                log.error("易宝确认绑卡失败, uid={}, result={}", uid, JSON.toJSONString(result));
                throw new BizException(result.getErrorcode());
            }

            String protocolNo = result.getYborderid();
            UserBank userBank = new UserBank();
            userBank.setCardCode(userBankInfoVO.getCardCode());
            userBank.setCardName(userBankInfoVO.getCardName());
            userBank.setCardNo(userBankInfoVO.getCardNo());
            userBank.setCardPhone(userBankInfoVO.getCardPhone());
            userBank.setCardStatus(1);
            userBank.setCreateTime(new Date());
            userBank.setForeignId(protocolNo);
            userBank.setUid(uid);
            userBank.setBindType(PaymentTypeEnum.yeepay.getCode());
            userService.insertUserBank(uid, userBank);
        } catch (Exception e) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "易宝绑卡确认失败: " + e.getMessage());
        }
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 还款
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultMessage repay(Order order) {
        try {
            String timeStr = System.currentTimeMillis() + "";
            String requestno = order.getOrderNo() + timeStr.substring(timeStr.length() - 6);

            long uid = order.getUid();

            String identityid = ("dev".equals(Constant.ENVIROMENT) ? "YBTest" : "YB") + uid;

            UserBank userBank = userBankService.selectUserCurrentBankCard(order.getUid());
            if (userBank == null || StringUtils.isBlank(userBank.getCardNo())) {
                throw new BizException("用户未绑卡");
            }

            // 卡号前六位
            String cardtop = userBank.getCardNo().substring(0, 6);
            // 卡号后四位
            String cardlast = userBank.getCardNo().substring(userBank.getCardNo().length() - 4);
            // 还款金额
            String amount = order.getShouldRepay().toPlainString();
            if ("dev".equals(Constant.ENVIROMENT)) {
                amount = "0.01";
            }
            String productname = "还款";
            // 协议支付： SQKKSCENEKJ010 代扣： SQKKSCENE10 商户需开通对应协议支付/代扣权限
            String terminalno = "SQKKSCENEKJ010";

            StringResultDTO result = YeePayApiRequest.cardPayRequest(
                    requestno, identityid, cardtop, cardlast, amount, productname, terminalno, false);

            String status = result.getStatus();
            if ("PAY_FAIL".equalsIgnoreCase(status) || "FAIL".equalsIgnoreCase(status)) {
                return new ResultMessage(ResponseEnum.M4000.getCode(), "易宝还款失败: " + status);
            }

            OrderRepay orderRepay = new OrderRepay();
            orderRepay.setRepayNo(result.getYborderid());
            orderRepay.setUid(uid);
            orderRepay.setOrderId(order.getId());
            orderRepay.setRepayType(1);
            BigDecimal repayMoney = new BigDecimal(amount);
            orderRepay.setRepayMoney(repayMoney);
            orderRepay.setBank(userBank.getCardName());
            orderRepay.setBankNo(userBank.getCardNo());
            orderRepay.setCreateTime(new Date());
            orderRepay.setUpdateTime(new Date());
            orderRepay.setRepayStatus(0);
            orderRepayService.insertSelective(orderRepay);

            OrderRepayQueryMessage message = new OrderRepayQueryMessage();
            message.setMerchantAlias(RequestThread.getClientAlias());
            message.setRepayNo(result.getYborderid());
            message.setTimes(1);
            message.setRepayType(1);
            rabbitTemplate.convertAndSend(RabbitConst.yeepay_queue_repay_order_query, message);

            JSONObject object = new JSONObject();
            object.put("repayOrderNo", result.getYborderid());
            object.put("shouldRepayAmount", amount);
            return new ResultMessage(ResponseEnum.M2000, object);
        } catch (Exception e) {
            log.error("易宝还款异常: " + e.getMessage(), e);
            return new ResultMessage(ResponseEnum.M4000.getCode(), "易宝还款失败: " + e.getMessage());
        }
    }

}
