package com.mod.loan.controller.bengbeng.handler;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.OrderSourceEnum;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.config.Constant;
import com.mod.loan.mapper.OrderUserMapper;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderRepay;
import com.mod.loan.service.OrderRepayService;
import com.mod.loan.service.OrderService;
import com.mod.loan.util.bengbeng.BizDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
    @Resource
    private OrderRepayService orderRepayService;
    @Resource
    private OrderUserMapper orderUserMapper;

    //推送用户确认收款信息
    public ResponseBean<Map<String, Object>> handleOrderSubmit(JSONObject param) throws BizException {
        JSONObject data = parseAndCheckBizData(param);
        String orderNo = data.getString("order_no");

        String loanAmount = data.getString("loan_amount");
        if (loanAmount == null || "".equals(loanAmount)) {
            throw new BizException("推送用户确认收款信息:申请借贷金额不能为空");
        }
        Integer loanTerm = data.getInteger("loan_term");
        if (loanTerm == null) {
            throw new BizException("推送用户确认收款信息:申请借贷期限不能为空");
        }
        checkAndGetUserId(orderNo);
        orderService.submitOrder(orderNo, loanAmount, loanTerm, OrderSourceEnum.BENGBENG.getSoruce());

        Map<String, Object> map = new HashMap<>();
        map.put("deal_result", "1");
        map.put("need_confirm", "0");
        return ResponseBean.success(map);
    }

    //查询借款合同
    public ResponseBean<Map<String, Object>> handleQueryContract(JSONObject param) throws BizException {

        String orderNo = getOrderNo(param);

        Map<String, Object> map = new HashMap<>();
        map.put("contract_url", Constant.sysDomainHost + "/static/loan-contract.html?uid=" + checkAndGetUserId(orderNo) + "&orderNo=" + orderNo + "&source=" + OrderSourceEnum.BENGBENG.getSoruce());
        return ResponseBean.success(map);
    }

    //查询订单状态
    public ResponseBean<Map<String, Object>> handleQueryOrderStatus(JSONObject param) throws BizException {

        String orderNo = getOrderNo(param);
        checkAndGetUserId(orderNo);

        Order order = orderService.findOrderByOrderNoAndSource(orderNo, OrderSourceEnum.BENGBENG.getSoruce());
        if (order == null) return ResponseBean.fail("订单不存在");

        /*11审核中10+：11-新建;12-等待复审;
        放款中20+；21-待放款;22-放款中(已受理);23-放款失败(可以重新放款);
        还款中30+；31-已放款/还款中;32-还款确认中;33-逾期;34-坏账；
        已结清中40+；41-正常还款;42-逾期还款;
        订单结束50+；51-自动审核失败 ;52-复审失败;53-取消*/

        int status;
        String remark = "";
        if (order.getStatus() == 23) {
            status = 169; //放款失败
            remark = "放款失败";
        } else if (order.getStatus() == 31) {
            status = 170; //放款成功
            remark = "放款成功";
        } else if (order.getStatus() == 21 || order.getStatus() == 22 || order.getStatus() == 11 || order.getStatus() == 12) {
            status = 171; //放款处理中
            remark = "放款处理中";
        } else if (order.getStatus() == 33) {
            status = 180; //贷款逾期
            remark = "贷款逾期";
        } else if (order.getStatus() == 41 || order.getStatus() == 42) {
            status = 200; //贷款结清
            remark = "贷款结清";
        } else {
            status = 169;
            remark = "审核失败";
        }

        long updateTime = order.getCreateTime().getTime();
        switch (status) {
            case 170:
                updateTime = order.getArriveTime().getTime();
                break;
            case 200:
                updateTime = order.getRealRepayTime().getTime();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("order_no", orderNo);
        map.put("order_status", status);
        map.put("update_time", updateTime);
        map.put("remark", remark);
        return ResponseBean.success(map);
    }

    //用户还款
    public ResponseBean<Map<String, Object>> handleRepayment(JSONObject param) throws BizException {
        String orderNo = getOrderNo(param);
        checkAndGetUserId(orderNo);

        Order order = orderService.repayOrder(orderNo, OrderSourceEnum.BENGBENG.getSoruce());

        OrderRepay orderRepay = orderRepayService.selectByOrderId(order.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("need_confirm", "0");
        map.put("deal_result", "1");
        map.put("transactionid", orderRepay.getRepayNo());
        map.put("reason", "");
        return ResponseBean.success(map);
    }

    private String getOrderNo(JSONObject param) throws BizException {
        JSONObject data = parseAndCheckBizData(param);
        String orderNo = data.getString("order_no");
        if (StringUtils.isBlank(orderNo)) throw new BizException("order_no 未传");
        return BizDataUtil.bindRZOrderNo(orderNo);
    }

    private JSONObject parseAndCheckBizData(JSONObject param) throws BizException {
        JSONObject data = parseBizData(param);
        if (data == null) throw new BizException(ResponseEnum.M5000);
        return data;
    }

    private JSONObject parseBizData(JSONObject param) {
        String bizData = param.getString("biz_data");
        return StringUtils.isNotBlank(bizData) ? JSONObject.parseObject(bizData) : null;
    }

    private long checkAndGetUserId(String orderNo) throws BizException {
        Long uid = RequestThread.getUid();
        if (uid == null || uid <= 0) {
            uid = orderUserMapper.getUidByOrderNoAndSource(orderNo, Integer.parseInt(UserOriginEnum.BB.getCode()));
        }
        if (uid == null) throw new BizException("根据订单号未获取到用户id");
        RequestThread.setUid(uid);
        return uid;
    }
}
