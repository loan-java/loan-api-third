package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 试算接口
 */
@Slf4j
@Component
public class WithDrawRequestHandler {


    @Resource
    private OrderMapper orderMapper;


    //查询审批结论
    ResponseBean<Map<String, Object>> withdrawTria(JSONObject param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject bizData = param.getJSONObject("biz_data");
        log.info("===============试算接口开始====================" + bizData.toJSONString());
        String orderNo = bizData.getString("order_no");
        String loan_amount = bizData.getString("loan_amount");
        String loan_term = bizData.getString("loan_term");
        String source = UserOriginEnum.RZ.getCode();

        Order order = orderMapper.findByOrderNoAndSource(orderNo,Integer.valueOf(source));
        if(order == null){
            throw new BizException("试算接口:订单不存在"+orderNo);
        }
        map.put("daily_rate","daily_rate");
        map.put("monthly_rate","monthly_rate");
        map.put("receive_amount","receive_amount");
        map.put("service_fee","service_fee");
        map.put("pay_amount","pay_amount");
        JSONObject trial_result_data = new JSONObject();
        trial_result_data.put("period_amount","period_amount");
        trial_result_data.put("principal","principal");
        trial_result_data.put("interest","interest");
        trial_result_data.put("otherfee","otherfee");
        trial_result_data.put("can_repay_time","can_repay_time");
        trial_result_data.put("period_no","period_no");
        map.put("trial_result_data",trial_result_data.toJSONString());
        log.info("===============试算接口结束====================");
        return  ResponseBean.success(map);
    }


}
