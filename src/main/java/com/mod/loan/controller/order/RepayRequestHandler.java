package com.mod.loan.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.controller.BaseRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public ResponseBean<Map<String, Object>> getRepayPlan(JSONObject param) throws BizException {


        List<Map<String, Object>> repayPlan = new ArrayList<>();
        Map<String, Object> repay = new HashMap<>();
        // 还款计划编号  期数
        repay.put("period_no","");
        // 账单状态：1=未到期待还款 2=已还款 3=逾期
        repay.put("bill_status","");
        // 账单到期时间 精确到毫秒（比如 1539073086805)
        repay.put("due_time","");
        // 当期最早可以还款的时间 精确到毫秒（比如 1539073086805 ）
        repay.put("can_repay_time","");
        // 还款方式：1=主动还款 2=跳转机构 H5 还款  4=银行代扣 5=主动还款+银行代扣
        repay.put("pay_type","");
        // 当前所需的还款金额，单位元，保留小数点后两位 （该金额应该是本金利息加上逾期金额减去已还款金额的结果，逾期金额、已还款金额可能为零）
        repay.put("amount","");
        // 已还款金额，单位元，保留小数点后两位
        repay.put("paid_amount","");
        // 逾期费用，单位元，保留小数点后两位
        repay.put("overdue_fee","");
        // 还款成功的时间
        repay.put("success_time","");
        // 当期还款金额描述
        repay.put("remark","");
        // 费用项集合
        List<Map<String, Object>> billitem = new ArrayList<>();
        Map<String, Object> bill = new HashMap<>();
        bill.put("feetype", "");
        bill.put("dueamount", "");

        billitem.add(bill);
        repay.put("billitem",billitem);
        repayPlan.add(repay);

        Map<String, Object> map = new HashMap<>();
        //账单的订单编号
        map.put("order_no ", "1");
        //银行名称编码（并非汉字）
        map.put("open_bank ", "1");
        //银行卡号
        map.put("ank_card ", "1");
        //还款计划
        map.put("repayment_plan", repayPlan);
        return ResponseBean.success(map);
    }
}
