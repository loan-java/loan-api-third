package com.mod.loan.util.yeepay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.exception.BizException;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.sdk.yop.client.YopRsaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Slf4j
public class YeepayUtil {

    static String merchantno = Config.getInstance().getValue("merchantno");
    static String appKey = Config.getInstance().getValue("appKey");
    static String publickey = Config.getInstance().getValue("publickey");
    static String privatekey = Config.getInstance().getValue("privatekey");


    public static StringResultDTO yeepayYOP(Map<String, String> map, String Uri) throws Exception {
        YopRequest request = new YopRequest("SQKK" + merchantno, privatekey);

        Set<Entry<String, String>> entry = map.entrySet();
        for (Entry<String, String> s : entry) {
            request.addParam(s.getKey(), s.getValue());
        }
        log.info("易宝请求: " + JSON.toJSONString(request));

        //向YOP发请求
        YopResponse response = YopRsaClient.post(Uri, request);

        checkFailResp(response);
        //对结果进行处理
        return parseResult(response.getStringResult());
    }

    private static void checkFailResp(YopResponse resp) throws BizException {
        if ("FAILURE".equals(resp.getState())) {
            if (resp.getError() != null)
                throw new BizException(resp.getError().getCode(), resp.getError().getMessage());
        }
    }

    private static StringResultDTO parseResult(String result) {
        if (StringUtils.isBlank(result)) return null;
        return JSONObject.parseObject(result, StringResultDTO.class);
    }
}
        

