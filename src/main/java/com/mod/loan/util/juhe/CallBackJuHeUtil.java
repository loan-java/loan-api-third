package com.mod.loan.util.juhe;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.service.impl.CallBackJuHeServiceImpl;
import com.mod.loan.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kk
 */
public class CallBackJuHeUtil {

    private static Logger log = LoggerFactory.getLogger(CallBackJuHeServiceImpl.class);

    public static boolean callBack(String host, JSONObject object) {
        object.put("timeStamp", System.currentTimeMillis() / 1000);
        String sign = SignUtil.getSign(object);
        object.put("sign", sign);
        boolean successFlag = false;
        String url = host + "/apiext/orderCallback/orderInfo";
        String response = HttpUtils.restRequest(url, object.toJSONString(), "POST");
        JSONObject res = JSON.parseObject(response);
        if ("200".equals(res.getString("code"))) {
            successFlag = true;
        }
        log.info("回调聚合，改变订单状态，请求参数：{}，返回结果：{}，message：{}", object.toJSONString(), response, res.getString("message"));
        return successFlag;
    }

//    /**
//     * unicode转字符串
//     *
//     * @param unicode unicode串
//     * @return 字符串
//     */
//    public static String unicodeToString(String unicode) {
//        StringBuffer sb = new StringBuffer();
//        String[] hex = unicode.split("\\\\u");
//        for (int i = 1; i < hex.length; i++) {
//            int index = Integer.parseInt(hex[i], 16);
//            sb.append((char) index);
//        }
//        return sb.toString();
//    }
}
