package com.mod.loan.util.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.config.Constant;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ author liujianjian
 * @ date 2019/5/15 9:03
 */
public class RongZeRequestUtil {

    public static String doPost(String url, String reqParamsStr) {
        //大王贷款回调服务API接口URL
//        String kingplatformUrl = "http://king-platform-callback.king-test.svc.cluster.local:9090/platform/callback";

//        String reqParamsStr = "{\"app_id\":\"appid1000000\",\"biz_data\":\"pGBul5nSR6ufWBMs/VRBPmduZhorhdfG8JEvIeikTgm4pvPdo/+qeB28lHw/G+NT2ZqXRMfn51LhsMutNWCzrSC06C35jFiOkQb3b4in1U3DS0jOxeN3AGbuF/7EzwA6ThlQTm1HkMw=\",\"biz_enc\":\"1\",\"des_key\":\"MDuA8iX/SIIxJRJRn8IAJ2+OZDZbAyZCGhhMU/oG5bOK51HypaLTIo9LKyKCRwbtqh0mJOP2ZS19o3lZNFCCexxOu4lFwnRWfOes+crDF8ppb4Z90W826GiNhtN3zk8UlXI0d3epCMIXLcHg3niXJu9O+ikAo9ycgM0u2EnXyPs=\",\"format\":\"json\",\"method\":\"api.bank.result\",\"return_url\":\"\",\"sign\":\"nhab1zczyG5t2PEhK0ztmttTm+5wcHFjekreSSRBOs8c19WVkl4ajO1nCXojPqxI34AlhdTQOkb/YMnOxHX3qX9n4jZ3W2qEdCnz2QcDBhvKrAqUbCD1Wty+iPltN+u+D41h0FmPvKuculPLGKDdINMkIPQjERVs9T49L5semF4=\",\"sign_type\":\"RSA\",\"timestamp\":\"1543396830799\",\"version\":\"1.0\"}";

        return HttpClientUtils.sendPost(url, reqParamsStr.getBytes());
    }

    /**
     * 构建请求参数
     */
    public static String buildRequestParams(String method, String bizData, String returnUrl) throws Exception {
        //说明：秘钥格式PKCS8，秘钥长度1024，签名方式RSA，字符集UTF-8

        //机构RSA私钥
        String privatekey = Constant.orgPrivateKey;
//        String privatekey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKEsWzW9BCMya+fTZ9ikE5PLglVG3eMhIG6ajF4NsmfjL/jBgWylbmPxvWnuzO1nnI+UVDDmJrxHuB39NIu2H1bJcjZ5o8WF7tEpnMYQ8GFQANCyOSiEQuV7A79vexGKDfyT7D+VjJLVlMhsS9Zv2bdHLi9n1UsNdgeimBO/NvQzAgMBAAECgYBD0wGzFI64LRDBpvItdaaTbHG1ZzQaz6bxRHJLZiIsm6tlWDEZwmg5ANK/0HFGenKk7TuctE2ar+eoHxTMsmBvGzctn8KSU+c5MqLiwMko4tkzoSJkaRAIO2f9Uv2rqLMHaOehzxHVgB39wIBv1HL5I1BmRjETHMR4cxmmb9w56QJBAPv2z246LDHe6ndCujcubkxYcNusGIWttSocwS3uCCGDLnrqEQ72zCmUevuSrYZFfnAT0LbA1fveP06J6Oa5pbUCQQCjwUEW3X08P2E8O2uCUWclianmQbZELaP68evNgumyOD8E3YJxDZtcTDcjrWoanE+UW31rm2ShihIEJrujMWNHAkBLIptSnGhHatjyPWS4RdFAVPM6noQlgNpQN4jnwF6OV8cJgjkaBEB3eb5+vIugSaLdmxsXFEP7OpgYPInGG8AtAkAni90/O2AqM5g05pixER2a2CMaw1XUIz2NtezfZbUwYBr//sqoqMOTR6itSgzsvkENsAaa/R0RUfnF3ODFqYCzAkEA7hH2HCE47Jy6q8tNGptbKy3Rj18KhcrLyQ4aq5KgX2wX2hNY0A5ZWJ+csYkWNgmPfbeOHj6gDwawg+ce0lZMng==";
        //机构RSA公钥
//        String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChLFs1vQQjMmvn02fYpBOTy4JVRt3jISBumoxeDbJn4y/4wYFspW5j8b1p7sztZ5yPlFQw5ia8R7gd/TSLth9WyXI2eaPFhe7RKZzGEPBhUADQsjkohELlewO/b3sRig38k+w/lYyS1ZTIbEvWb9m3Ry4vZ9VLDXYHopgTvzb0MwIDAQAB";

        String despwd = StandardDesUtils.generateDesKey();

        RequestRongZeBean vo = new RequestRongZeBean();
        vo.setMethod(method);
        vo.setSign_type("RSA");
        vo.setBiz_data(encryptBizData(bizData, despwd));
        vo.setBiz_enc("1");
        vo.setDes_key(encryptDescKey(despwd));
        vo.setApp_id(Constant.rongZeRequestAppId);
        vo.setVersion("1.0");
        vo.setFormat("json");
        vo.setTimestamp(System.currentTimeMillis() + "");
        vo.setReturn_url(returnUrl);

        String reqContent = JSONObject.toJSONString(vo);
        Map<String, String> paramMap = JSONObject.parseObject(reqContent, Map.class);

        StringBuffer sbfStr = new StringBuffer();
        List<String> list = new ArrayList<String>(paramMap.keySet());
        Collections.sort(list); //参数名ASCII码从小到大排序（字典序）
        for (String key : list) {
            if (StringUtils.isNotBlank(paramMap.get(key))) {
                sbfStr.append(key + "=" + paramMap.get(key) + "&");
            }
        }
        String pendVertContent = sbfStr.toString().substring(0, sbfStr.length() - 1);
        System.out.println("待生成签名的字符串：" + pendVertContent);
        String sign = RSAUtils.sign(pendVertContent, privatekey);
        System.out.println("签名sign:" + sign);

        //设置签名
        vo.setSign(sign);
        return JSONObject.toJSONString(vo);
    }

    public static String buildRequestParams(String method, String bizData) throws Exception {
        return buildRequestParams(method, bizData, "");
    }

    //加密业务数据
    private static String encryptBizData(String bizData, String despwd) throws Exception {
        //业务参数
//        String biz_data = "{'order_no':'1584827442684559360','bank_card':'6228480550690077718','bind_status':1,'reason':''}";
        //生成des秘钥
//        String despwd = StandardDesUtils.generateDesKey();
        String encrypt = StandardDesUtils.encrypt(bizData, despwd);

        System.out.println("decrypt bizData:" + decryptBizData(encrypt, despwd));

        return encrypt;
        //biz_data加解密
//        System.out.println("despwd:"+despwd);
//        System.out.println("encrypt bizdata作为biz_data值:"+encryptStr);
//        System.out.println("decrypt bizdata:"+StandardDesUtils.decrypt(encryptStr, despwd));
//        System.out.println();

        //大王贷款平台RSA公钥
//        String financePublicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIcI7USujX9g5J1ZxVMFl+Yq/u/i2JbGt8SM/Ds7Z4nU/895C+dL9MGnSqTAMXCsp5eFhoIhyHPUxiO0GaGLXBAvoq1TpTFGVA0mYty3yeDwNA/Aia5tnQe2mFamdRBQqrU/xMleR6rtEWxs/cze+ZU5eO441b/fcKCgel1kjPBQIDAQAB";
        //des_key加解密
        //对despwd进行RSA加密并base64转成String
//        String content = RSAUtils.encrypt(despwd, financePublicKey);
//        System.out.println("RSA加密并base64作为des_key值:"+content);

        //大王贷款平台RSA私钥
//        String financePrivateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIhwjtRK6Nf2DknVnFUwWX5ir+7+LYlsa3xIz8OztnidT/z3kL50v0wadKpMAxcKynl4WGgiHIc9TGI7QZoYtcEC+irVOlMUZUDSZi3LfJ4PA0D8CJrm2dB7aYVqZ1EFCqtT/EyV5Hqu0RbGz9zN75lTl47jjVv99woKB6XWSM8FAgMBAAECgYBtc0XQnUsOO2+Y4UfousF/9nDF2pby/8t1xv8MMfU16pRJDNvLPOJcjXh7SEDOUjS8nsLkvkB+aLkGsBxfHmVmACc5yj9+L/wHhzsI9Lvqg0q9AjdrC2a35OZexggWikKjH8HT/oerNCyKQ7RUs4k7P3ECz9WWhY28DR90ABtvHQJBAOCwXZHcspykVGqQH/S4a59TnP0rDJuSvG6e1q+fpGknDvriA1QYTLxriLvgvI5kY2Ry+ct5LbKzZ5JZUgPtSK8CQQCbc/TnHPpxTjFMATI6rsF5dnFkjSzQY6aQG+Q1X4d5GeIndYzPCWdr4Bd3Xkh5ouRHy1bdFrdQXHm4jIX4GSiLAkEApl/x6WASogrMt1uhTgSBLKktRgnqfAhbn03eio0boQFbBkr1S//yUlMOHJB9DrMnJeo9LX29aOWPe77IDEBX4QJAUIH0QsEyPv4E79zqu5OH5bTesvmeTOpe9+FKBg5MZf5urorles/e/PJYlNyCYmRnH3uCqAu8smTCMT6tnzjAUwJAYEjnoy8pBJmwcgSEZ4hgXjSCtAZHmSnnxQW9gJnmiyM/nJzGq9B3ZrBbY/zQ54Ws1HuX7dDlOQYRAnznFfZOQw==";
        //对des_key进行解密
//        System.out.println("解密的despwd:"+RSAUtils.decrypt(content , financePrivateKey));
    }

    //生成 RSA 加密后的密钥
    private static String encryptDescKey(String despwd) throws Exception {
        String encrypt = RSAUtils.encrypt(despwd, Constant.rongZePublicKey);
//        System.out.println("decrypt descKey:" + RSAUtils.decrypt(encrypt, Constant.rongZePublicKey));
        return encrypt;
    }

    //解密业务数据
    public static String decryptBizData(String encryptStr, String despwd) throws Exception {
        return StandardDesUtils.decrypt(encryptStr, despwd);
    }

    static class RequestRongZeBean {
        /**
         * 要请求的 API 方法名称
         */
        private String method;
        /**
         * API 请求的签名
         */
        private String sign;
        /**
         * 签名方式，默认RSA
         */
        private String sign_type;
        /**
         * 请求的业务数据，json 格式。注意是 json 格式的字符串
         */
        private String biz_data;
        /**
         * biz_data 加密方式
         * （0 不加密，1 加密:采用 DES 加密算法）
         */
        private String biz_enc;
        /**
         * RSA 加密后的密钥（biz_enc 为 1 时为必传）
         */
        private String des_key;
        /**
         * 分配给应用的唯一标识
         */
        private String app_id;
        /**
         * API 协议版本，默认值：1.0
         */
        private String version;
        /**
         * 响应格式，仅支持 json
         */
        private String format;
        /**
         * 时间戳，精确到毫秒（比如1539073086805 ）
         */
        private String timestamp;
        /**
         * 回调 url
         */
        private String return_url;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSign_type() {
            return sign_type;
        }

        public void setSign_type(String sign_type) {
            this.sign_type = sign_type;
        }

        public String getBiz_data() {
            return biz_data;
        }

        public void setBiz_data(String biz_data) {
            this.biz_data = biz_data;
        }

        public String getBiz_enc() {
            return biz_enc;
        }

        public void setBiz_enc(String biz_enc) {
            this.biz_enc = biz_enc;
        }

        public String getDes_key() {
            return des_key;
        }

        public void setDes_key(String des_key) {
            this.des_key = des_key;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getReturn_url() {
            return return_url;
        }

        public void setReturn_url(String return_url) {
            this.return_url = return_url;
        }
    }
}
