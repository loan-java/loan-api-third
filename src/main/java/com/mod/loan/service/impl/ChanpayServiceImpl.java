package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.message.OrderRepayQueryMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderRepay;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.ChanpayService;
import com.mod.loan.service.OrderRepayService;
import com.mod.loan.service.UserBankService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.chanpay.ChanpayApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

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

    @Override
    public ResultMessage bindCardRequest(String orderNo, long uid, String bankCardNo, String mobileNo) {
        try {
            User user = userService.selectByPrimaryKey(uid);
            if (user == null) throw new BizException("用户(" + uid + ")不存在");

            String userId = "" + uid;
            String idCardNo = user.getUserCertNo();
            String username = user.getUserName();
            chanpayApiRequest.bindCardRequest(orderNo, userId, bankCardNo, idCardNo, username, mobileNo);

        } catch (Exception e) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "畅捷绑卡请求失败: " + e.getMessage());
        }
        return new ResultMessage(ResponseEnum.M2000);
    }

    @Override
    public ResultMessage bindCardConfirm(String orderNo, String smsCode) {
        try {
            chanpayApiRequest.bindCardConfirm(orderNo, smsCode);
        } catch (Exception e) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "畅捷绑卡确认失败: " + e.getMessage());
        }
        return new ResultMessage(ResponseEnum.M2000);
    }

    @Override
    public ResultMessage repay(Order order) {
        try {
            long uid = order.getUid();
            String userId = "" + uid;

            UserBank userBank = userBankService.selectUserCurrentBankCard(order.getUid());
            if (userBank == null || StringUtils.isBlank(userBank.getCardNo())) throw new BizException("用户未绑卡");

            String cardPre = userBank.getCardNo().substring(0, 6); //卡号前六位
            String cardSuf = userBank.getCardNo().substring(userBank.getCardNo().length() - 4); //卡号后四位

            String amount = order.getShouldRepay().toPlainString();
            ChanpayApiRequest.ChanpayResponse response = chanpayApiRequest.cardPayRequest(order.getOrderNo(), userId, cardPre, cardSuf, amount);

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
            message.setRepayNo(tid);
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
