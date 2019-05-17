package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.model.User;
import com.mod.loan.service.UserService;
import com.mod.loan.util.HttpUtils;
import com.mod.loan.util.rongze.BizDataUtil;
import com.mod.loan.util.rongze.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    public Object dispatcherRequest(HttpServletRequest request, @RequestBody JSONObject param) {

        log.info("融泽请求收到, param: " + param.toJSONString());

        Object result;

        try {//校验 sig
            String sign = param.getString("sign");
            boolean check = SignUtil.checkSign(param.toJSONString(), sign);
            if (!check) throw new BizException(ResponseEnum.M4006);

            //绑定线程变量
            binRequestThread(request, param);

            //解密 bizData
            if ("1".equals(param.getString("biz_enc"))) {
                String bizDataStr = param.getString("biz_data");
                String bizData = BizDataUtil.decryptBizData(bizDataStr, param.getString("des_key"));
                param.put("biz_data", bizData);
            }

            String method = param.getString("method");
            if (StringUtils.isBlank(method)) throw new BizException(ResponseEnum.M5000);

            switch (method) {
                case "fund.withdraw.req": //推送用户确认收款信息
                    result = rongZeRequestHandler.handleOrderSubmit(param);
                    break;
                case "fund.deal.contract": //查询借款合同
                    result = rongZeRequestHandler.handleQueryContract(param);
                    break;
                case "fund.order.status": //查询订单状态
                    result = rongZeRequestHandler.handleQueryOrderStatus(param);
                    break;
                case "fund.payment.req": //推送用户还款信息
                    result = rongZeRequestHandler.handleRepayment(param);
                    break;

                // TODO: 2019/5/15 其它 method
                default:
                    throw new BizException(ResponseEnum.M5000.getCode(), "method not found");
            }
        } catch (Exception e) {
            logFail(e);
            result = e instanceof BizException ? ResponseBean.fail(((BizException) e)) : ResponseBean.fail(e.getMessage());
        }

        log.info("融泽请求返回, result: " + JSON.toJSONString(result));
        return result;
    }

    private void binRequestThread(HttpServletRequest request, JSONObject param) {
        RequestThread.remove();// 移除本地线程变量
        String sourceId = param.getString("source_id"); //标志用户来源的app
        String merchantId = param.getString("merchant_id"); //给融泽分配的merchant_id

        String ip = HttpUtils.getIpAddr(request, ".");
//        String clientVersion = obj.getString("version");
//        String clientType = obj.getString("terminalId");
        String clientAlias = Constant.merchant;
        String sign = param.getString("sign");
        String deviceCode = param.getString("deviceCode");
        String token = param.getString("token");

//        RequestThread.setClientVersion(clientVersion);
//        RequestThread.setClientType(clientType);
        RequestThread.setClientAlias(clientAlias);
        RequestThread.setIp(ip);
        RequestThread.setRequestTime(System.currentTimeMillis());
        RequestThread.setDeviceCode(deviceCode);
        RequestThread.setToken(token);
        RequestThread.setSign(sign);
        RequestThread.setSourceId(sourceId);
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
