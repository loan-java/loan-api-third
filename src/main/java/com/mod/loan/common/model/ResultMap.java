package com.mod.loan.common.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 返回结果
 *
 * @author kk
 */
public class ResultMap {

    public static JSONObject success() {
        JSONObject object = new JSONObject();
        object.put("code", "9999");
        object.put("message", "处理成功");
        object.put("accountId", RequestThread.getUid());
        object.put("token", RequestThread.getToken());
        object.put("deviceCode", RequestThread.getDeviceCode());
        object.put("sign", RequestThread.getSign());
        object.put("version", RequestThread.getClientVersion());

        return object;
    }

    public static JSONObject fail(String code, String message) {
        JSONObject object = new JSONObject();
        object.put("code", code);
        object.put("message", message);
        object.put("accountId", RequestThread.getUid());
        object.put("token", RequestThread.getToken());
        object.put("deviceCode", RequestThread.getDeviceCode());
        object.put("sign", RequestThread.getSign());
        object.put("version", RequestThread.getClientVersion());

        return object;
    }
}
