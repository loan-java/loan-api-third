package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.model.Order;
import com.mod.loan.model.User;
import com.mod.loan.service.OrderService;
import com.mod.loan.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ author liujianjian
 * @ date 2019/5/15 18:18
 */
@Slf4j
@Component
public class RongZeRequestHandler {

    @Resource
    private OrderService orderService;

    //推送用户确认收款信息
    ResponseBean<Map<String, Object>> handleOrderSubmit(JSONObject param) throws Exception {
        JSONObject data = JSONObject.parseObject(param.getString("biz_data"));

        String orderNo = data.getString("order_no");
        String loanAmount = data.getString("loan_amount");
        int loanTerm = data.getIntValue("loan_term");

        orderService.submitOrder(orderNo, loanAmount, loanTerm);

        Map<String, Object> map = new HashMap<>();
        map.put("deal_result", "1");
        map.put("need_confirm", "0");
        return ResponseBean.success(map);
    }

    //查询借款合同
    Object handleQueryContract(JSONObject param) {
        JSONObject data = JSONObject.parseObject(param.getString("biz_data"));

        String orderNo = data.getString("order_no");

        Map<String, Object> map = new HashMap<>();
        map.put("contract_url", "url");
        return ResponseBean.success(map);
    }

    //查询订单状态
    Object handleQueryOrderStatus(JSONObject param) {
        JSONObject data = JSONObject.parseObject(param.getString("biz_data"));

        String orderNo = data.getString("order_no");
        Order order = orderService.findOrderByOrderNo(orderNo);
        if (order == null) return ResponseBean.fail("订单不存在");

        /*11审核中10+：11-新建;12-等待复审;
        放款中20+；21-待放款;22-放款中(已受理);23-放款失败(可以重新放款);
        还款中30+；31-已放款/还款中;32-还款确认中;33-逾期;34-坏账；
        已结清中40+；41-正常还款;42-逾期还款;
        订单结束50+；51-自动审核失败 ;52-复审失败;53-取消*/

        int status;
        if (order.getStatus() == 23) status = 169; //放款失败
        else if (order.getStatus() == 31) status = 170; //放款成功
        else if (order.getStatus() == 21 || order.getStatus() == 22 || order.getStatus() == 11 || order.getStatus() == 12)
            status = 171; //放款处理中
        else if (order.getStatus() == 33) status = 180; //贷款逾期
        else if (order.getStatus() == 41 || order.getStatus() == 42) status = 200; //贷款结清
        else status = 169;

        long updateTime = order.getCreateTime().getTime();
        switch (status){
            case 170 : updateTime = order.getArriveTime().getTime();break;
            case 200 : updateTime = order.getRealRepayTime().getTime();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("order_no", orderNo);
        map.put("order_status", status);
        map.put("update_time", updateTime);
        map.put("remark", "");
        return ResponseBean.success(map);
    }
}
