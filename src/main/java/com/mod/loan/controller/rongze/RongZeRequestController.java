package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.model.User;
import com.mod.loan.service.UserService;
import com.mod.loan.util.rongze.BizDataUtil;
import com.mod.loan.util.rongze.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ author liujianjian
 * @ date 2019/5/15 18:01
 */
@Slf4j
@RestController
@RequestMapping("/rongze")
public class RongZeRequestController {

    @Resource
    private UserService userService;
    @Resource
    private RongZeRequestHandler rongZeRequestHandler;

    @RequestMapping("/dispatcherRequest")
    public Object dispatcherRequest(@RequestBody JSONObject param) {

        log.info("收到融泽请求, param: " + JSON.toJSONString(param));
        if (param == null) return ResponseBean.fail(ResponseEnum.M5000);

        try {//校验 sig
            String sign = param.getString("sign");
            boolean check = SignUtil.checkSign(param.toJSONString(), sign);
            if (!check) return ResponseBean.fail(ResponseEnum.M4006);

            //解密 bizData
            if ("1".equals(param.getString("biz_enc"))) {
                String bizDataStr = param.getString("biz_data");
                String bizData = BizDataUtil.decryptBizData(bizDataStr, param.getString("des_key"));
                param.put("biz_data", bizData);
            }

            String method = param.getString("method");
            if (StringUtils.isBlank(method)) return ResponseBean.fail(ResponseEnum.M5000);

            switch (method) {
                case "fund.withdraw.req": //推送用户确认收款信息
                    return rongZeRequestHandler.handleOrderSubmit(param);
                case "fund.deal.contract": //查询借款合同
                    return rongZeRequestHandler.handleQueryContract(param);
                case "fund.order.status": //查询订单状态
                    return rongZeRequestHandler.handleQueryOrderStatus(param);

                // TODO: 2019/5/15 其它 method
                default:
                    return ResponseBean.fail(ResponseEnum.M5000.getCodeInt(), "method not found");
            }
        } catch (Exception e) {
            logFail(e);
            return ResponseBean.fail(e.getMessage());
        }
    }

    private void logFail(Exception e) {
        if (e instanceof BizException)
            log.info(getPreLog() + e.getMessage());
        else
            log.error(getPreLog() + e.getMessage(), e);
    }

    private String getPreLog() {
        Long uid = RequestThread.getUid();
        if (uid == null) return "";

        String pre = "userId: %s, username: %s, phone: %s, ", username = "", phone = "";
        User user = userService.selectByPrimaryKey(uid);
        if (user != null) {
            username = user.getUserName();
            phone = user.getUserPhone();
        }
        return String.format(pre, uid, username, phone);
    }
}
