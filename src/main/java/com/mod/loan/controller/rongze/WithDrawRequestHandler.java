package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.mapper.MerchantRateMapper;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.model.MerchantRate;
import com.mod.loan.model.Order;
import com.mod.loan.service.MerchantRateService;
import com.mod.loan.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
public class WithDrawRequestHandler {

    @Autowired
    private MerchantRateMapper merchantRateMapper;

    @Autowired
    private MerchantRateService merchantRateService;

    //查询审批结论
    ResponseBean<Map<String, Object>> withdrawTria(JSONObject param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject bizData = param.getJSONObject("biz_data");
        log.info("===============试算接口开始====================" + bizData.toJSONString());
        String loanAmount = bizData.getString("loan_amount");
        Integer loanTerm = bizData.getInteger("loan_term");
        //====================================================
        MerchantRate record = merchantRateMapper.findByMerchant(RequestThread.getClientAlias());
        if (record == null) throw new BizException("试算接口:借款周期基本信息失败");
        Long productId = record.getId();
        MerchantRate merchantRate = merchantRateService.selectByPrimaryKey(productId);
        if (null == merchantRate) {
            throw new BizException("试算接口:未查到规则");
        }
        //====================================================
        //按6天算
        Integer borrowDay = loanTerm;
        //借款金额
        BigDecimal borrowMoney = new BigDecimal(loanAmount);
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
        map.put("receive_amount", actualMoney.toString());//用户卡中收到款的金额，单位元，保留小数点后 2 位
        map.put("service_fee", totalFee.toString());//放款时预扣除手续费，单位元，保留小数点后 2 位
        map.put("pay_amount", shouldRepay.toString());//用户的总还款额，单位元，保留小数点后 2 位（总还款额包括本金利息管理费手续费等一切费用之和）
        //====================================================
        JSONObject trial_result_data = new JSONObject();
        trial_result_data.put("period_amount", shouldRepay.toString());//用户每期应还总金额，单位元，保留小数点后 2 位
        trial_result_data.put("principal", borrowMoney.toString());//本金，单位元，保留小数点后 2 位
//        trial_result_data.put("interest", order.getInterestRate().toString());//利息，单位元，保留小数点后 2 位
        trial_result_data.put("otherfee", totalFee);//除去本金+利息的其他费用，保留小数点后 2 位
        trial_result_data.put("can_repay_time", new Timestamp(repayTime.getTime()));//应还款日期，精确到毫秒（比如 153907308680
        trial_result_data.put("period_no", 1);//还款计划编号,期数
        map.put("trial_result_data", trial_result_data.toJSONString());
        log.info("===============试算接口结束====================");
        return ResponseBean.success(map);
    }


}
