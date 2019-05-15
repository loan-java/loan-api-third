package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.service.OrderService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ author liujianjian
 * @ date 2019/5/15 18:18
 */
@Component
public class RongZeRequestHandler {

    @Resource
    private OrderService orderService;

    //推送用户确认收款信息
    Object handleOrderSubmit(JSONObject param) {

        JSONObject data = JSONObject.parseObject(param.getString("biz_data"));

        String orderNo = data.getString("order_no");
        String loanAmount = data.getString("loan_amount");
        int loanTerm = data.getIntValue("loan_term");

        return orderService.submitOrder(orderNo, loanAmount, loanTerm);
    }

    //查询借款合同
    Object handleQueryContract(JSONObject param) {
        return null;
    }
}
