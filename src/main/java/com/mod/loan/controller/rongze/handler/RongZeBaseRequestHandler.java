package com.mod.loan.controller.rongze.handler;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import org.apache.commons.lang.StringUtils;

public class RongZeBaseRequestHandler {

    protected JSONObject parseAndCheckBizData(JSONObject param) throws BizException {
        JSONObject data = parseBizData(param);
        if (data == null) throw new BizException(ResponseEnum.M5000);
        return data;
    }

    private JSONObject parseBizData(JSONObject param) {
        String bizData = param.getString("biz_data");
        return StringUtils.isNotBlank(bizData) ? JSONObject.parseObject(bizData) : null;
    }
}
