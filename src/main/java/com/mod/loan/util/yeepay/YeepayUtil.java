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


//    public static Map<String, String> yeepayYOP(Map<String, String> map, String Uri) throws IOException {}

    public static JSONObject yeepayYOP(Map<String, String> map, String Uri) throws Exception {

        YopRequest request = new YopRequest("SQKK" + merchantno, privatekey);
//        Map<String, String> result = new HashMap<String, String>();

        Set<Entry<String, String>> entry = map.entrySet();
        for (Entry<String, String> s : entry) {
            request.addParam(s.getKey(), s.getValue());
        }
        log.info("易宝请求: " + JSON.toJSONString(request));

        //向YOP发请求
        //YopResponse yopresponse = YopClient3.postRsa(Uri, yoprequest);   过时的方法
        YopResponse response = YopRsaClient.post(Uri, request);
//        System.out.println("请求YOP之后结果：" + yopresponse.toString());
//        System.out.println("请求YOP之后结果：" + yopresponse.getStringResult());
        log.info("易宝返回: " + JSON.toJSONString(response));

        checkFailResp(response);
        return parseResult(response.getStringResult());
//        	对结果进行处理
//        if ("FAILURE".equals(yopresponse.getState())) {
//            if (yopresponse.getError() != null)
//                result.put("errorcode", yopresponse.getError().getCode());
//            result.put("errormsg", yopresponse.getError().getMessage());
//            System.out.println("错误明细：" + yopresponse.getError());
//            System.out.println("系统处理异常结果：" + result);
//            return result;
//        }
        //成功则进行相关处理
//        if (yopresponse.getStringResult() != null) {
//            result = parseResponse(yopresponse.getStringResult());
//            System.out.println("yopresponse.getStringResult: " + result);
//        }
    }

    public static void checkFailResp(YopResponse resp) throws BizException {
        if ("FAILURE".equals(resp.getState())) {
            if (resp.getError() != null)
                throw new BizException(resp.getError().getCode(), resp.getError().getMessage());
        }
    }

    public static JSONObject parseResult(String result) {
        if (StringUtils.isBlank(result)) return null;

        return JSON.parseObject(result);
    }

    //将获取到的yopresponse转换成json格式
//    public static Map<String, String> parseResponse(String yopresponse) {
//        Map<String, String> jsonMap = new HashMap<>();
//        jsonMap = JSON.parseObject(yopresponse,
//                new TypeReference<TreeMap<String, String>>() {
//                });
//        System.out.println("将结果yopresponse转化为map格式之后: " + jsonMap);
//        return jsonMap;
//    }
}
        

