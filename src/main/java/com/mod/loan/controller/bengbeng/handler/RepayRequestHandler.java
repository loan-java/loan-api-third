package com.mod.loan.controller.bengbeng.handler;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.OrderSourceEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderRepay;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.OrderRepayService;
import com.mod.loan.service.OrderService;
import com.mod.loan.service.UserBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 还款相关
 *
 * @author czl
 */
@Slf4j
@Component
public class RepayRequestHandler extends BaseRequestHandler {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserBankService userBankService;

    @Autowired
    private OrderRepayService orderRepayService;

    /**
     * 获取还款计划
     */
    public ResponseBean<Map<String, Object>> getRepayPlan(JSONObject param) throws BizException {
        JSONObject data = parseAndCheckBizData(param);
        log.info("===============获取还款计划开始====================" + data.toJSONString());


        String orderNo = data.getString("order_no");
        Order order = orderService.findOrderByOrderNoAndSource(orderNo, OrderSourceEnum.BENGBENG.getSoruce());
        log.info("获取订单详情:{}", JSONObject.toJSONString(order));
        if (order == null) {
            throw new BizException("===============订单编号不存在=" + orderNo);
        }
        List<Map<String, Object>> repayPlan = new ArrayList<>();
        Map<String, Object> repay = new HashMap<>(11);
        // 还款计划编号  期数
        repay.put("period_no", "1");
        // 账单状态：1=未到期待还款 2=已还款 3=逾期
        if (order.getRealRepayTime() != null) {
            repay.put("bill_status", "2");
        } else if (order.getOverdueDay() > 0) {
            repay.put("bill_status", "3");
        } else {
            repay.put("bill_status", "1");
        }
        // 账单到期时间 精确到毫秒（比如 1539073086805)
        repay.put("due_time", order.getRepayTime().getTime());
        // 当期最早可以还款的时间 精确到毫秒（比如 1539073086805 ）
        repay.put("can_repay_time", System.currentTimeMillis());
        // 还款方式：1=主动还款 2=跳转机构 H5 还款  4=银行代扣 5=主动还款+银行代扣
        repay.put("pay_type", 5);
        // 当前所需的还款金额，单位元，保留小数点后两位 （该金额应该是本金利息加上逾期金额减去已还款金额的结果，逾期金额、已还款金额可能为零）

        BigDecimal amount = order.getHadRepay() == null ? order.getShouldRepay() : order.getShouldRepay().subtract(order.getHadRepay());
        repay.put("amount", amount);
        // 已还款金额+减免金额，单位元，保留小数点后两位
        repay.put("paid_amount", (order.getHadRepay().add(order.getReduceMoney())).toPlainString());
        // 逾期费用，单位元，保留小数点后两位
        repay.put("overdue_fee", order.getOverdueFee().toPlainString());
        // 还款成功的时间
        repay.put("success_time", order.getRealRepayTime() == null ? "" : order.getRealRepayTime().getTime() / 1000);
        // 当期还款金额描述
        if (order.getRealRepayTime() != null) {
            StringBuilder remark = new StringBuilder("含本金 ");
            remark.append(order.getActualMoney().toPlainString());
            remark.append(" 元，利息&手续费 ").append(order.getTotalFee().add(order.getInterestFee()).toPlainString()).append(" 元");
            if (order.getOverdueFee() != null && order.getOverdueFee().compareTo(new BigDecimal("0")) > 0) {
                remark.append("，逾期 ").append(order.getOverdueFee().toPlainString()).append(" 元");
            }
            repay.put("remark", remark.toString());
        }
        // 费用项集合
        List<Map<String, Object>> billItem = new ArrayList<>();
        if (order.getActualMoney() != null) {
            // 实际金额
            Map<String, Object> actualMoney = new HashMap<>(2);
            actualMoney.put("feetype", "1");
            actualMoney.put("dueamount", order.getActualMoney().toPlainString());
            billItem.add(actualMoney);
        }
        if (order.getTotalFee() != null) {
            // 服务费
            Map<String, Object> totalFee = new HashMap<>(2);
            totalFee.put("feetype", "5");
            totalFee.put("dueamount", order.getTotalFee().toPlainString());
            billItem.add(totalFee);
        }
        if (order.getInterestFee() != null && order.getInterestFee().compareTo(new BigDecimal("0")) > 0) {
            // 利息
            Map<String, Object> interestFee = new HashMap<>(2);
            interestFee.put("feetype", "2");
            interestFee.put("dueamount", order.getInterestFee().toPlainString());
            billItem.add(interestFee);
        }
        if (order.getOverdueFee() != null && order.getOverdueFee().compareTo(new BigDecimal("0")) > 0) {
            // 逾期费
            Map<String, Object> overdueFee = new HashMap<>(2);
            overdueFee.put("feetype", "3");
            overdueFee.put("dueamount", order.getOverdueFee().toPlainString());
            billItem.add(overdueFee);
        }
        repay.put("billitem", billItem);
        repayPlan.add(repay);

        UserBank userBank = userBankService.selectUserCurrentBankCard(RequestThread.getUid());
        if (userBank == null) {
            throw new BizException("===============用户银行卡不存在uid=" + RequestThread.getUid());
        }
        Map<String, Object> map = new HashMap<>(4);
        //账单的订单编号
        map.put("order_no", orderNo);
        //银行名称编码（并非汉字）
        map.put("open_bank", userBank.getCardCode());
        //银行卡号
        map.put("bank_card", userBank.getCardNo());
        //还款计划
        map.put("repayment_plan", repayPlan);

        log.info("===============获取还款计划结束====================");

        return ResponseBean.success(map);
    }

    /**
     * 获取还款状态
     */
    public ResponseBean<Map<String, Object>> getRepayStatus(JSONObject param) throws BizException {
        JSONObject data = parseAndCheckBizData(param);
        log.info("===============获取还款状态开始====================" + data.toJSONString());

        String orderNo = data.getString("order_no");
        Order order = orderService.findOrderByOrderNoAndSource(orderNo, OrderSourceEnum.BENGBENG.getSoruce());
        if (order == null) {
            throw new BizException("订单不存在");
        }
        OrderRepay orderRepay = orderRepayService.selectByOrderId(order.getId());
        if (orderRepay == null) {
            throw new BizException("无还款信息");
        }
        Map<String, Object> map = new HashMap<>();
        // 订单编号
        map.put("order_no", orderNo);
        // 还款期数
        map.put("period_nos", "1");
        // 本次还款金额，单位 元
        map.put("repay_amount", orderRepay.getRepayMoney() == null ? "" : orderRepay.getRepayMoney().toPlainString());
        // 还款状态 1=还款成功 2=还款失败 0=还款中状态
        map.put("repay_status", orderRepay.getRepayStatus() == 3 ? 1 : orderRepay.getRepayStatus() == 4 ? 2 : 0);
        // 还款触发方式  1=主动还款 2=自动代扣
        map.put("repay_place", orderRepay.getRepayType() == 7 ? 2 : 1);
        // 执行还款的时间
        map.put("success_time", orderRepay.getCreateTime().getTime() / 1000);
        // 备注说明
        String remark = "";
        if (orderRepay.getRepayStatus() == 3) {
            remark = "本金 " + order.getBorrowMoney().toPlainString() + "，利息 " + order.getInterestFee().toPlainString();
        } else if (orderRepay.getRepayStatus() == 4) {
            remark = orderRepay.getRemark();
        }
        map.put("remark", remark);
        log.info("===============获取还款状态结束====================");
        return ResponseBean.success(map);
    }
}
