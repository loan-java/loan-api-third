package com.mod.loan.controller.bengbeng.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.mapper.MerchantRateMapper;
import com.mod.loan.mapper.OrderUserMapper;
import com.mod.loan.model.MerchantRate;
import com.mod.loan.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 试算接口
 */
@Slf4j
@Component
public class BengBengWithDrawRequestHandler {

    @Autowired
    private MerchantRateMapper merchantRateMapper;
    @Autowired
    private OrderUserMapper orderUserMapper;

    //试算接口
    public ResponseBean<Map<String, Object>> withdrawTria(JSONObject param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject bizData =  JSONObject.parseObject(param.getString("biz_data"));
        String orderNo = bizData.getString("order_no");

        log.info("===============试算接口开始====================");
        String loanAmount = bizData.getString("loan_amount");
        if(loanAmount == null || "".equals(loanAmount)){
            throw new BizException("试算接口:申请借贷金额不能为空");
        }
        Integer loanTerm = bizData.getInteger("loan_term");
        if(loanTerm == null){
            throw new BizException("试算接口:申请借贷期限不能为空");
        }
        //====================================================
        //是否存在关联的借贷信息
        Long  merchantRateId = orderUserMapper.getMerchantRateByOrderNoAndSource(orderNo, Integer.parseInt(UserOriginEnum.BB.getCode()));
        if(merchantRateId == null){
            throw new BizException("试算接口:商户不存在默认借贷信息");
        }
        MerchantRate merchantRate = merchantRateMapper.selectByPrimaryKey(merchantRateId);
        if(merchantRate == null){
            throw new BizException("试算接口:商户不存在默认借贷信息");
        }
        BigDecimal approvalAmount = merchantRate.getProductMoney(); //审批金额
        if(approvalAmount == null) {
            throw new BizException("试算接口:商户不存在默认借贷金额");
        }
        Integer approvalTerm = merchantRate.getProductDay(); //审批期限
        if(approvalAmount == null) {
            throw new BizException("试算接口:商户不存在默认借贷期限");
        }
        //====================================================
        //按6天算
        Integer borrowDay = loanTerm;
        if(borrowDay.intValue() != approvalTerm.intValue()) {
            throw new BizException("试算接口:商户实际借贷期限：" + approvalTerm.intValue() + ",现在借款期限：" + borrowDay.intValue());
        }
        //借款金额
        BigDecimal borrowMoney = new BigDecimal(loanAmount);
        if(borrowMoney.intValue() != approvalAmount.intValue()) {
            throw new BizException("试算接口:商户实际借贷金额：" + approvalAmount.intValue() + ",现在借款金额：" + borrowMoney.intValue());
        }
        //综合费率
        BigDecimal totalRate = merchantRate.getTotalRate();
        //日利率，整数
        BigDecimal interestRate = merchantRate.getProductRate();
        // 综合费用
        BigDecimal totalFee = MoneyUtil.totalFee(borrowMoney, totalRate);
        // 利息
        BigDecimal interestFee = MoneyUtil.interestFee(borrowMoney, borrowDay, interestRate );
        // 实际到账
        BigDecimal actualMoney = MoneyUtil.actualMoney(borrowMoney, totalFee);
        // 应还金额
        BigDecimal shouldRepay = MoneyUtil.shouldrepay(borrowMoney, interestFee, BigDecimal.ZERO, BigDecimal.ZERO);
        //还款时间
        Date repayTime = new DateTime().plusDays(borrowDay - 1).toDate();
        //====================================================
        map.put("daily_rate", interestRate.divide(new BigDecimal(1000), 4, BigDecimal.ROUND_HALF_UP).toString());//日利率，如果贷款周期为天，需要传，例如 0.0002
//        map.put("monthly_rate","monthly_rate");//月利率，如果贷款周期为月，需要传，例如 0.0002
        map.put("receive_amount", actualMoney.setScale(2).toString());//用户卡中收到款的金额，单位元，保留小数点后 2 位
        map.put("service_fee", totalFee.setScale(2).toString());//放款时预扣除手续费，单位元，保留小数点后 2 位
        map.put("pay_amount", shouldRepay.setScale(2).toString());//用户的总还款额，单位元，保留小数点后 2 位（总还款额包括本金利息管理费手续费等一切费用之和）
        //====================================================
        JSONArray trial_result_data = new JSONArray();
        JSONObject one = new JSONObject();
        one.put("period_amount", shouldRepay.setScale(2).toString());//用户每期应还总金额，单位元，保留小数点后 2 位
        one.put("principal", borrowMoney.setScale(2).toString());//本金，单位元，保留小数点后 2 位
//        trial_result_data.put("interest", order.getInterestRate().toString());//利息，单位元，保留小数点后 2 位
        one.put("otherfee", totalFee.setScale(2).toString());//除去本金+利息的其他费用，保留小数点后 2 位
        one.put("can_repay_time", new Timestamp(repayTime.getTime()));//应还款日期，精确到毫秒（比如 153907308680
        one.put("period_no", 1);//还款计划编号,期数
        trial_result_data.add(one);
        map.put("trial_result_data", trial_result_data);
        log.info("===============试算接口结束====================");
        return ResponseBean.success(map);
    }


}
