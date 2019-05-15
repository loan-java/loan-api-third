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
    private UserService userService;
    @Resource
    private OrderService orderService;

    //推送用户确认收款信息
    ResponseBean<Map<String, Object>> handleOrderSubmit(JSONObject param) {
        try {
            JSONObject data = JSONObject.parseObject(param.getString("biz_data"));

            String orderNo = data.getString("order_no");
            String loanAmount = data.getString("loan_amount");
            int loanTerm = data.getIntValue("loan_term");

            orderService.submitOrder(orderNo, loanAmount, loanTerm);

            Map<String, Object> map = new HashMap<>();
            map.put("deal_result", "1");
            map.put("need_confirm", "0");
            return ResponseBean.success(map);
        } catch (BizException e) {
            logFail(e, "");
            return ResponseBean.fail(e);
        } catch (Exception e) {
            logFail(e, "提交贷款申请订单失败");
            return ResponseBean.fail(e.getMessage());
        }
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

        int status = -1;
        Map<String, Object> map = new HashMap<>();
        map.put("order_no", orderNo);
        map.put("order_status", status);
        map.put("update_time", "");
        map.put("remark", "");
        return ResponseBean.success(map);
    }


    private void logFail(Exception e, String pre) {
        if (e instanceof BizException)
            log.info(getPreLog() + e.getMessage());
        else
            log.error(getPreLog() + pre + ": " + e.getMessage(), e);
    }

    private String getPreLog() {
        String pre = "userId: %s, username: %s, phone: %s, ", username = "", phone = "";
        Long uid = RequestThread.getUid();
        if (uid != null) {
            User user = userService.selectByPrimaryKey(uid);

            if (user != null) {
                username = user.getUserName();
                phone = user.getUserPhone();
            }
        }
        return String.format(pre, uid, username, phone);
    }
}
