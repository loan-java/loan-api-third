package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.PaymentTypeEnum;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.message.OrderRepayQueryMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.model.*;
import com.mod.loan.model.vo.UserBankInfoVO;
import com.mod.loan.service.ChanpayService;
import com.mod.loan.service.OrderRepayService;
import com.mod.loan.service.UserBankService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.StringUtil;
import com.mod.loan.util.chanpay.ChanpayApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ author liujianjian
 * @ date 2019/6/18 22:02
 */
@Slf4j
@Service
public class ChanpayServiceImpl implements ChanpayService {

    @Resource
    private UserService userService;
    @Resource
    private ChanpayApiRequest chanpayApiRequest;
    @Resource
    private UserBankService userBankService;
    @Resource
    private OrderRepayService orderRepayService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisMapper redisMapper;

    @Override
    public ResultMessage bindCardRequest(Long uid, String cardNo, String cardPhone, Bank bank) {
        try {
            User user = userService.selectByPrimaryKey(uid);
            if (user == null) throw new BizException("用户(" + uid + ")不存在");

            String userId = "" + uid;
            String idCardNo = user.getUserCertNo();
            String username = user.getUserName();

            String timeStr = System.currentTimeMillis() + "";
            String orderNo = StringUtil.getOrderNumber("c");

            chanpayApiRequest.bindCardRequest(orderNo, userId, cardNo, idCardNo, username, cardPhone);

            UserBankInfoVO userBankInfoVO = new UserBankInfoVO();
            userBankInfoVO.setUid(uid);
            userBankInfoVO.setCardCode(bank.getCode());
            userBankInfoVO.setCardName(bank.getBankName());
            userBankInfoVO.setCardNo(cardNo);
            userBankInfoVO.setCardPhone(cardPhone);
            redisMapper.set(RedisConst.user_bank_bind + uid, userBankInfoVO, 600);

        } catch (Exception e) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "畅捷绑卡请求失败: " + e.getMessage());
        }
        return new ResultMessage(ResponseEnum.M2000);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultMessage bindCardConfirm(String validateCode, Long uid, UserBankInfoVO userBankInfoVO) {
        try {
            String orderNo = redisMapper.get(RedisConst.user_bank_bind + uid);
            if (StringUtils.isBlank(orderNo)) {
                return new ResultMessage(ResponseEnum.M4000.getCode(), "请重新绑卡");
            }

            ChanpayApiRequest.BindCardResponse response = chanpayApiRequest.bindCardConfirm(orderNo, validateCode);
            //签约协议号
            String protocolNo = response.getOrderTrxid();
            UserBank userBank = new UserBank();
            userBank.setCardCode(userBankInfoVO.getCardCode());
            userBank.setCardName(userBankInfoVO.getCardName());
            userBank.setCardNo(userBankInfoVO.getCardNo());
            userBank.setCardPhone(userBankInfoVO.getCardPhone());
            userBank.setCardStatus(1);
            userBank.setCreateTime(new Date());
            userBank.setForeignId(protocolNo);
            userBank.setUid(uid);
            userBank.setBindType(PaymentTypeEnum.chanpay.getCode());
            userService.insertUserBank(uid, userBank);
        } catch (Exception e) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "畅捷绑卡确认失败: " + e.getMessage());
        }
        return new ResultMessage(ResponseEnum.M2000);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultMessage repay(Order order) {
        try {
            long uid = order.getUid();
            String userId = "" + uid;

            UserBank userBank = userBankService.selectUserCurrentBankCard(order.getUid());
            if (userBank == null || StringUtils.isBlank(userBank.getCardNo())) throw new BizException("用户未绑卡");

            String cardPre = userBank.getCardNo().substring(0, 6); //卡号前六位
            String cardSuf = userBank.getCardNo().substring(userBank.getCardNo().length() - 4); //卡号后四位

            String amount = order.getShouldRepay().toPlainString();
            ChanpayApiRequest.CardPayResponse response = chanpayApiRequest.cardPayRequest(order.getOrderNo(), userId, cardPre, cardSuf, amount);

            String tid = response.getOrderTrxid();

            OrderRepay orderRepay = new OrderRepay();
            orderRepay.setRepayNo(tid);
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
            message.setRepayNo(order.getOrderNo());
            message.setTimes(1);
            message.setRepayType(1);
            rabbitTemplate.convertAndSend(RabbitConst.chanpay_queue_repay_order_query, message);

            JSONObject object = new JSONObject();
            object.put("repayOrderNo", tid);
            object.put("shouldRepayAmount", amount);
            return new ResultMessage(ResponseEnum.M2000, object);
        } catch (Exception e) {
            log.error("畅捷还款异常: " + e.getMessage(), e);
            return new ResultMessage(ResponseEnum.M4000.getCode(), "畅捷还款失败: " + e.getMessage());
        }
    }
}
