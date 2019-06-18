package com.mod.loan.util.chanpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.config.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ author liujianjian
 * @ date 2019/6/18 20:41
 */
@Slf4j
@Component
public class ChanpayApiRequest {

    private static ChanpayGateway chanpay = new ChanpayGateway();

    //鉴权绑卡请求
    public ChanpayResponse bindCardRequest(String orderNo, String userId, String bankCardNo, String idCardNo, String username, String mobileNo) throws Exception {
        Map<String, String> origMap = new HashMap<String, String>();
        origMap = chanpay.setCommonMap(origMap);
        // 2.1 鉴权绑卡 api 业务参数
        origMap.put("Service", "nmg_biz_api_auth_req");// 鉴权绑卡的接口名(商户采集方式)
        origMap.put("TrxId", orderNo);// 订单号
        origMap.put("ExpiredTime", "90m");// 订单有效期
        origMap.put("MerUserId", userId);// 用户标识（测试时需要替换一个新的meruserid）
        origMap.put("MerchantNo", Constant.chanpayMerchantNo);// 商户编号
        origMap.put("BkAcctTp", "01");// 卡类型（00 – 银行贷记卡;01 – 银行借记卡;）
        origMap.put("BkAcctNo", chanpay.encrypt(bankCardNo));// 卡号
        //System.out.println(this.encrypt("621483011*******", MERCHANT_PUBLIC_KEY, charset));
        origMap.put("IDTp", "01");// 证件类型 （目前只支持身份证 01：身份证）
        origMap.put("IDNo", chanpay.encrypt(idCardNo));// 证件号
        //System.out.println(this.encrypt("13010*********", MERCHANT_PUBLIC_KEY, charset));
        origMap.put("CstmrNm", chanpay.encrypt(username));// 持卡人姓名
        origMap.put("MobNo", chanpay.encrypt(mobileNo));// 银行预留手机号
        //信用卡
//		origMap.put("CardCvn2", "004");// cvv2码
//		origMap.put("CardExprDt", "09/21");// 有效期

//		origMap.put("NotifyUrl", "http://dev.chanpay.com/receive.php");// 异步通知url
        origMap.put("SmsFlag", "1");
//        origMap.put("Extension", "");
        return doPost(origMap);
    }

    //鉴权绑卡确认
    public ChanpayResponse bindCardConfirm(String orderNo, String smsCode) throws Exception {

        Map<String, String> origMap = new HashMap<String, String>();
        // 2.1 基本参数
        origMap = chanpay.setCommonMap(origMap);
        origMap.put("Service", "nmg_api_auth_sms");// 鉴权绑卡确认的接口名
        // 2.1 鉴权绑卡  业务参数
        origMap.put("TrxId", orderNo);// 订单号
        origMap.put("OriAuthTrxId", orderNo);// 原鉴权绑卡订单号
        origMap.put("SmsCode", smsCode);// 鉴权短信验证码
//        origMap.put("NotifyUrl", "http://dev.chanpay.com/receive.php");// 异步通知地址
        return doPost(origMap);
    }

    //绑卡支付请求
    public ChanpayResponse cardPayRequest(String orderNo, String userId, String cardPre, String cardSuf, String amount) throws Exception {

        Map<String, String> origMap = new HashMap<String, String>();
        // 2.1 基本参数
        origMap = chanpay.setCommonMap(origMap);
        origMap.put("Service", "nmg_biz_api_quick_payment");// 支付的接口名
        // 2.2 业务参数
        origMap.put("TrxId", orderNo);// 订单号
        origMap.put("OrdrName", "还款");// 商品名称
        origMap.put("MerUserId", userId);// 用户标识（测试时需要替换一个新的meruserid）
        origMap.put("SellerId", Constant.chanpayMerchantNo);// 子账户号
//        origMap.put("SubMerchantNo", "");// 子商户号
        origMap.put("ExpiredTime", "40m");// 订单有效期
        origMap.put("CardBegin", cardPre);// 卡号前6位
        origMap.put("CardEnd", cardSuf);// 卡号后4位
        origMap.put("TrxAmt", amount);// 交易金额，元
        origMap.put("TradeType", "11");// 交易类型，11-即时，12-担保
        origMap.put("SmsFlag", "0");
        return doPost(origMap);
    }

    private ChanpayResponse doPost(Map<String, String> origMap) throws Exception {
        log.info("畅捷 api 请求开始, params: " + JSON.toJSONString(origMap));
        String result = chanpay.gatewayPost(origMap);
        log.info("畅捷 api 请求结束, result: " + result);

        JSONObject json = JSON.parseObject(result);
        ChanpayResponse r = new ChanpayResponse(json.getString("AcceptStatus"), json.getString("AppRetMsg"),
                json.getString("AppRetcode"), json.getString("Status"), json.getString("RetCode"), json.getString("RetMsg"));
        if ("F".equals(r.getAcceptStatus()) || "F".equals(r.getStatus())) {
            throw new BizException(StringUtils.isNotBlank(r.getRetCode()) ? r.getRetCode() : r.getAppRetcode(), StringUtils.isNotBlank(r.getRetMsg()) ? r.getRetMsg() : r.getAppRetMsg());
        }
        r.setOrderTrxid(json.getString("OrderTrxid"));
        return r;
    }

    public static class ChanpayResponse {
        private String acceptStatus;
        private String appRetMsg;
        private String appRetcode;

        private String status;
        private String retCode;
        private String retMsg;

        private String orderTrxid; //支付时畅捷订单号

        public ChanpayResponse() {
        }

        public ChanpayResponse(String acceptStatus, String appRetMsg, String appRetcode, String status, String retCode, String retMsg) {
            this.acceptStatus = acceptStatus;
            this.appRetMsg = appRetMsg;
            this.appRetcode = appRetcode;
            this.status = status;
            this.retCode = retCode;
            this.retMsg = retMsg;
        }

        public String getAcceptStatus() {
            return acceptStatus;
        }

        public void setAcceptStatus(String acceptStatus) {
            this.acceptStatus = acceptStatus;
        }

        public String getAppRetMsg() {
            return appRetMsg;
        }

        public void setAppRetMsg(String appRetMsg) {
            this.appRetMsg = appRetMsg;
        }

        public String getAppRetcode() {
            return appRetcode;
        }

        public void setAppRetcode(String appRetcode) {
            this.appRetcode = appRetcode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRetCode() {
            return retCode;
        }

        public void setRetCode(String retCode) {
            this.retCode = retCode;
        }

        public String getRetMsg() {
            return retMsg;
        }

        public void setRetMsg(String retMsg) {
            this.retMsg = retMsg;
        }

        public String getOrderTrxid() {
            return orderTrxid;
        }

        public void setOrderTrxid(String orderTrxid) {
            this.orderTrxid = orderTrxid;
        }
    }
}
