package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.util.rongze.BizDataUtil;
import com.mod.loan.util.rongze.SignUtil;
import lombok.extern.slf4j.Slf4j;
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
    private RongZeRequestHandler rongZeRequestHandler;

    @RequestMapping("/dispatcherRequest")
    public Object dispatcherRequest(@RequestBody JSONObject param) {

        log.info("收到融泽请求, param: " + JSON.toJSONString(param));

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

            switch (method) {
                case "fund.withdraw.req":
                    return rongZeRequestHandler.handleOrderSubmit(param);

                // TODO: 2019/5/15 其它 method
                default:
                    return ResponseBean.fail(ResponseEnum.M5000.getCodeInt(), "method not found");
            }
        } catch (Exception e) {
            return ResponseBean.fail("失败: " + e.getMessage());
        }
    }
}
