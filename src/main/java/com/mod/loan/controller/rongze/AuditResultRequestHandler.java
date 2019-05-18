package com.mod.loan.controller.rongze;


import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.model.Order;
import com.mod.loan.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询审批结论
 */
@Slf4j
@Component
public class AuditResultRequestHandler {


    @Autowired
    private OrderMapper orderMapper;


    //查询审批结论
    ResponseBean<Map<String, Object>> auditResult(JSONObject param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String message="成功";
        JSONObject bizData = param.getJSONObject("biz_data");
        log.info("===============查询审批结论开始====================" + bizData.toJSONString());

        String orderNo = bizData.getString("order_no");
        String source = UserOriginEnum.RZ.getCode();

        Order order = orderMapper.findByOrderNoAndSource(orderNo,Integer.valueOf(source));
        if(order == null){
            log.info("订单不存在"+orderNo);
            throw new BizException("查询订单不存在");
        }
        int conclusion = 30; //10=审批通过 40=审批拒绝30=审批处理中
        String reapply = "0"; //是否可再申请 1-是，0-不可以
        Timestamp approvalTime = new Timestamp(order.getAuditTime() == null?System.currentTimeMillis():order.getAuditTime().getTime()); //是否可再申请 1-是，0-不可以
        String reapplyTime = ""; //可再申请的时间，yyyy- MM-dd，比如（2020-10- 10）
        String remark = "";
        //order_status=100（审批通过），order_status=110（审批拒绝）
        if(order.getStatus() == 100){
            conclusion = 10;
            int proType = 1; //单期产品
            int amountType = 0; //审批金额是否固定，0 - 固定
            int termType = 0; //审批期限是否固定，0 - 固定
            int approvalAmount = 1500; //审批金额
            int approvalTerm = 6; //审批期限
            int termUnit = 1; //期限单位，1 - 天
            String creditDeadline = DateUtil.getStringDateShort(); //审批结果有效期，当前时间
            map.put("reapplytime", approvalTime);
            map.put("pro_type", proType);
            map.put("term_unit", termUnit);
            map.put("amount_type", amountType);
            map.put("term_type", termType);
            map.put("approval_amount", approvalAmount);
            map.put("approval_term", approvalTerm);
            map.put("credit_deadline", creditDeadline);
        }else if(order.getStatus() == 110) {
            conclusion = 40;
            reapply = "1";
            reapplyTime = DateFormatUtils.format(new Date().getTime() + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
            remark="";
            map.put("order_no", orderNo);
            map.put("conclusion", conclusion);
            map.put("reapplytime", reapplyTime);
            map.put("remark", remark);
            map.put("refuse_time", approvalTime);
        }
        map.put("reapply", reapply);
        map.put("order_no", orderNo);
        map.put("conclusion", conclusion);
        log.info("===============查询审批结论结束====================");
        return  ResponseBean.success(map);
    }


}
