package com.mod.loan.controller.juhe.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.OrderSourceEnum;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.controller.check.LoginCheck;
import com.mod.loan.mapper.MerchantRateMapper;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.model.MerchantRate;
import com.mod.loan.model.Order;
import com.mod.loan.model.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenanle
 */
@RestController
@RequestMapping(value = "order")
public class JuHeOrderController {

    private static Logger logger = LoggerFactory.getLogger(JuHeOrderController.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private LoginCheck loginCheck;
    @Autowired
    private MerchantRateMapper merchantRateMapper;

    /**
     * 查询订单信息
     *
     * @param map
     * @return
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "orderSearch")
    public JSONObject orderSearch(HttpServletRequest request, @RequestBody JSONObject map) {
        logger.info("=====查询订单信息=====");
        logger.info("请求参数：" + JSON.toJSONString(map));

        ResultMessage m = loginCheck.check(request, map, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }

        if (map.size() < 1) {
            return ResultMap.fail("4000", "参数不能为空");
        }
        OrderDTO orderDTO = new OrderDTO();
        Order order = orderMapper.findByOrderNoAndUid(map.getString("orderNo"), RequestThread.getUid(), OrderSourceEnum.JUHE.getSoruce());
        if (order == null) {
            ResultMap.fail("4000", "订单不存在");
        }
        if (11 == order.getStatus() || 12 == order.getStatus()) {
            orderDTO.setOrderStatus("APPROVE");
        } else if (51 == order.getStatus() || 52 == order.getStatus()) {
            orderDTO.setOrderStatus("AUDIT_REFUSE");
        } else if (53 == order.getStatus()) {
            orderDTO.setOrderStatus("PAY_FAILED");
        } else if (21 == order.getStatus() || 22 == order.getStatus() || 23 == order.getStatus()) {
            orderDTO.setOrderStatus("WAIT_PAY");
        } else if (31 == order.getStatus() || 32 == order.getStatus()) {
            orderDTO.setOrderStatus("REPAYING");
        } else if (41 == order.getStatus() || 42 == order.getStatus()) {
            orderDTO.setOrderStatus("REPAYED");
        } else if (33 == order.getStatus() || 34 == order.getStatus()) {
            orderDTO.setOrderStatus("OVERDUE");
        } else {
            //其它
            orderDTO.setOrderStatus("");
        }
        orderDTO.setOrderNo(map.get("orderNo").toString());
        orderDTO.setPayMoney(order.getActualMoney());
        orderDTO.setOrderApplyDate(order.getCreateTime());
        orderDTO.setPayTime(order.getRepayTime());
        JSONObject object = ResultMap.success();
        object.put("data", orderDTO);
        return object;
    }

    /**
     * 借款配置
     *
     * @return
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "loanConfig")
    public JSONObject loanConfig(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        logger.info("=====借款配置=====");
        logger.info("请求参数：" + JSON.toJSONString(jsonObject));

        ResultMessage m = loginCheck.check(request, jsonObject, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }
        Map<String, Object> data = new HashMap<>();
        ArrayList loanAmountInfo = new ArrayList();
        ArrayList loanTermInfo = new ArrayList();
        ArrayList collectTermInfo = new ArrayList();
        MerchantRate merchantRate = merchantRateMapper.findByMerchant(RequestThread.getClientAlias());
        loanAmountInfo.add(500);
        loanAmountInfo.add(1000);
        loanAmountInfo.add(1500);
        loanAmountInfo.add(2000);
        loanAmountInfo.add(2500);
        loanAmountInfo.add(3000);
        loanAmountInfo.add(3500);
        loanAmountInfo.add(4000);
        loanAmountInfo.add(4500);
        loanAmountInfo.add(5000);
        loanTermInfo.add(merchantRate.getProductDay());
        //展期期限集合[7, 14]
        collectTermInfo.add(7);

        //服务费费率 %
        String feeRate = merchantRate.getTotalRate().toString();
        //日利率 %
        String dailyRate = merchantRate.getProductRate().toString();
        //罚息率 %
        String delayRate = merchantRate.getOverdueRate().toString();
        //展期费率 %
        String collectRate = "0";

        data.put("loanAmountInfo", loanAmountInfo);
        data.put("loanTermInfo", loanTermInfo);
        data.put("collectTermInfo", collectTermInfo);
        data.put("feeRate", feeRate);
        data.put("dailyRate", dailyRate);
        data.put("delayRate", delayRate);
        data.put("collectRate", collectRate);
        JSONObject object = ResultMap.success();
        object.put("data", data);
        logger.info("返回前端数据：" + object.toJSONString());
        return object;
    }
}
