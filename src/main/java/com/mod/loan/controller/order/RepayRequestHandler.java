package com.mod.loan.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.OrderSourceEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.controller.BaseRequestHandler;
import com.mod.loan.model.Order;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.OrderService;
import com.mod.loan.service.UserBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    /**
     * 获取还款计划
     */
    public ResponseBean<Map<String, Object>> getRepayPlan(JSONObject param) throws BizException {
        JSONObject data = parseAndCheckBizData(param);
        String orderNo = data.getString("order_no");
        Order order = orderService.findOrderByOrderNoAndSource(orderNo, OrderSourceEnum.RONGZE.getSoruce());

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
        repay.put("due_time", order.getRepayTime().getTime() / 1000);
        // 当期最早可以还款的时间 精确到毫秒（比如 1539073086805 ）
        repay.put("can_repay_time", System.currentTimeMillis() / 1000);
        // 还款方式：1=主动还款 2=跳转机构 H5 还款  4=银行代扣 5=主动还款+银行代扣
        repay.put("pay_type", 5);
        // 当前所需的还款金额，单位元，保留小数点后两位 （该金额应该是本金利息加上逾期金额减去已还款金额的结果，逾期金额、已还款金额可能为零）
        repay.put("amount", order.getShouldRepay());
        // 已还款金额，单位元，保留小数点后两位
        repay.put("paid_amount", order.getHadRepay().toPlainString());
        // 逾期费用，单位元，保留小数点后两位
        repay.put("overdue_fee", order.getOverdueFee().toPlainString());
        // 还款成功的时间
        repay.put("success_time", "");
        // 当期还款金额描述
        repay.put("remark", "");
        // 费用项集合
        List<Map<String, Object>> billItem = new ArrayList<>();
        if (order.getActualMoney() != null) {
            Map<String, Object> actualMoney = new HashMap<>(2);
            actualMoney.put("feetype", "1");
            actualMoney.put("dueamount", order.getActualMoney().toPlainString());
            billItem.add(actualMoney);
        }
        if (order.getTotalFee() != null) {
            Map<String, Object> totalFee = new HashMap<>(2);
            totalFee.put("feetype", "5");
            totalFee.put("dueamount", order.getTotalFee().toPlainString());
            billItem.add(totalFee);
        }
        if (order.getInterestFee() != null) {
            Map<String, Object> interestFee = new HashMap<>(2);
            interestFee.put("feetype", "2");
            interestFee.put("dueamount", order.getInterestFee().toPlainString());
            billItem.add(interestFee);
        }
        if (order.getOverdueFee() != null) {
            Map<String, Object> overdueFee = new HashMap<>(2);
            overdueFee.put("feetype", "2");
            overdueFee.put("dueamount", order.getOverdueFee().toPlainString());
            billItem.add(overdueFee);
        }
        repay.put("billitem", billItem);
        repayPlan.add(repay);

        Long userId = 1L;
        UserBank userBank = userBankService.selectUserCurrentBankCard(userId);
        Map<String, Object> map = new HashMap<>(4);
        //账单的订单编号
        map.put("order_no ", orderNo);
        //银行名称编码（并非汉字）
        map.put("open_bank ", userBank.getCardCode());
        //银行卡号
        map.put("ank_card ", userBank.getCardNo());
        //还款计划
        map.put("repayment_plan", repayPlan);
        return ResponseBean.success(map);
    }
}
