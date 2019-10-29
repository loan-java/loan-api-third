package com.mod.loan.controller.whole.order;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.OrderSourceEnum;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.model.Merchant;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderRepay;
import com.mod.loan.model.User;
import com.mod.loan.service.*;
import com.mod.loan.util.MD5;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("order")
public class RepayController {

    private static Logger logger = LoggerFactory.getLogger(RepayController.class);
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepayService orderRepayService;
    @Autowired
    UserService userService;
    @Autowired
    UserBankService userBankService;
    @Autowired
    MerchantService merchantService;

    /**
     * 查询商户支持的支付通道
     */
    @LoginRequired(check = true)
    @RequestMapping(value = "repay_channel")
    public ResultMessage repay_channel() {
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        if (null == merchant) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
        }
        JSONObject object = JSONObject.parseObject(merchant.getMerchantChannel());
        return new ResultMessage(ResponseEnum.M2000, object);
    }

    /**
     * 支付还款
     */
    @LoginRequired(check = true)
    @RequestMapping(value = "order_repay")
    public ResultMessage order_repay(@RequestParam(required = true) Long orderId, @RequestParam(required = true) String cardNo,
                                           @RequestParam(required = true) String cardName) throws IOException {
        ResultMessage message = null;
        Long uid = RequestThread.getUid();
        if (StringUtils.isBlank(cardNo) || StringUtils.isBlank(cardName)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请输入正确的卡号");
        }
        if (orderId == null) {
            logger.info("订单异常，uid={},订单号={}", uid, orderId);
            return new ResultMessage(ResponseEnum.M4000.getCode(), "订单不存在");
        }
        Order order = orderService.selectByPrimaryKey(orderId);
        if (!order.getUid().equals(uid)) {
            logger.info("订单异常，订单号为：{}", order.getId());
            return new ResultMessage(ResponseEnum.M4000.getCode(), "订单异常");
        }
        if (order.getStatus() != 31 && order.getStatus() != 33 && order.getStatus() != 34) { // 已放款，逾期，坏账状态
            logger.info("订单非还款状态，订单号为：{}", order.getId());
            return new ResultMessage(ResponseEnum.M4000.getCode(), "订单状态异常");
        }
        User user = userService.selectByPrimaryKey(uid);
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        //以分为单位，取整
        Long amount = new BigDecimal(100).multiply(order.getShouldRepay()).longValue();
        if ("dev".equals(Constant.ENVIROMENT)) {
            amount = 200L;//以分为单位
        }
        Map<String, String> param = new HashMap<String, String>();
        try {
            message = orderService.repayOrder(order.getOrderNo(), OrderSourceEnum.WHOLE.getSoruce());
        } catch (Exception e) {
            logger.info("支付异常。订单号为{}，卡号为{}，银行名称为{}", orderId, cardNo, cardName);
            logger.error("支付异常", e);
            message = new ResultMessage(ResponseEnum.M4000);
        }
        return message;
    }

//    /**
//     * 异步通知
//     * 回调的结果以http返回码是否是200来判断，
//     * 返回不是200，多次回调。最多10次，前3次每2分钟发，后几次每整点/半点发。
//     */
//    @LoginRequired(check = false)
//    @RequestMapping(value = "order_fuyou_callback")
//    public void order_fuyou_callback(HttpServletRequest req) throws IOException {
//        String version = req.getParameter("VERSION");
//        String type = req.getParameter("TYPE");
//        String responseCode = req.getParameter("RESPONSECODE");
//        String responseMsg = req.getParameter("RESPONSEMSG");
//        String mchntCd = req.getParameter("MCHNTCD");
//        String mchntOrderId = req.getParameter("MCHNTORDERID");
//        String orderId = req.getParameter("ORDERID");////富友订单号
//        String bankCard = req.getParameter("BANKCARD");
//        String amt = req.getParameter("AMT");
//        String sign = req.getParameter("SIGN");
//        OrderRepay orderRepay = orderRepayService.selectByPrimaryKey(mchntOrderId);
//        if (null == orderRepay || 3 == orderRepay.getRepayStatus()) {
//            logger.info("富友异步通知：流水不存在或已支付成功，还款订单流水为：{}，对应富友订单号为：{}", mchntOrderId, orderId);
//            return;
//        }
//        Order order = orderService.selectByPrimaryKey(orderRepay.getOrderId());
//        if (null == order || 41 == order.getStatus() || 42 == order.getStatus()) {
//            logger.info("富友异步通知：订单不存在或已还，还款订单流水为：{}，对应富友订单号为：{}", mchntOrderId, orderId);
//            return;
//        }
//        Merchant merchant = merchantService.findMerchantByAlias(order.getMerchant());
//        // 校验签名
//        String signPain = new StringBuffer().append(type).append("|").append(version).append("|").append(responseCode)
//                .append("|").append(mchntCd).append("|").append(mchntOrderId).append("|").append(orderId).append("|")
//                .append(amt).append("|").append(bankCard).append("|").append(merchant.getFuyou_h5key()).toString();
//        if (!MD5.toMD5(signPain).equals(sign)) {
//            logger.info("富友异步通知验签失败，订单流水为：{}，对应富友订单号为：{}", mchntOrderId, orderId);
//            return;
//        }
//        if (!"0000".equals(responseCode)) {
//            logger.info("富友异步通知支付失败，订单流水为：{}，对应富友订单号为：{}，失败信息为：{}", mchntOrderId, orderId, responseMsg);
//            OrderRepay orderRepay1 = new OrderRepay();
//            orderRepay1.setRepayNo(mchntOrderId);
//            orderRepay1.setRepayStatus(2);
//            if (StringUtils.isNotBlank(responseMsg) && responseMsg.length() > 30) {
//                responseMsg = responseMsg.substring(0, 30);
//            }
//            orderRepay1.setRemark(responseMsg);
//            orderRepayService.updateByPrimaryKeySelective(orderRepay1);
//            return;
//        }
//        Order order1 = new Order();
//        order1.setId(orderRepay.getOrderId());
//        order1.setRealRepayTime(new Date());
//        order1.setHadRepay(order.getShouldRepay());
//        if (33 == order.getStatus() || 34 == order.getStatus()) {
//            order1.setStatus(42);
//        } else {
//            order1.setStatus(41);
//        }
//        OrderRepay orderRepay1 = new OrderRepay();
//        orderRepay1.setRepayNo(mchntOrderId);
//        orderRepay1.setUpdateTime(new Date());
//        orderRepay1.setRepayStatus(3);
//        orderRepayService.updateOrderRepayInfo(orderRepay1, order1);
//        return;
//    }

}
