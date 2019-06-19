package com.mod.loan.util.chanpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.config.Constant;
import com.mod.loan.util.chanpay.dsf.BaseParameter;
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
public class ChanpayApiRequest extends BaseParameter {

    private static ChanpayGateway chanpay = new ChanpayGateway();

    //鉴权绑卡请求
    public JSONObject bindCardRequest(String orderNo, String userId, String bankCardNo, String idCardNo, String username, String mobileNo) throws Exception {
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
    public JSONObject bindCardConfirm(String orderNo, String smsCode) throws Exception {

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

    //协议支付请求
    public CardPayResponse cardPayRequest(String orderNo, String userId, String cardPre, String cardSuf, String amount) throws Exception {
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
        JSONObject json = doPost(origMap);
        return new CardPayResponse(json.getString("OrderTrxid"));
    }

    private JSONObject doPost(Map<String, String> origMap) throws Exception {
        log.info("畅捷 api 请求开始, params: " + JSON.toJSONString(origMap));
        String result = chanpay.gatewayPost(origMap);
        log.info("畅捷 api 请求结束, result: " + result);

        JSONObject json = JSON.parseObject(result);
        String acceptStatus = json.getString("AcceptStatus");
        String appRetMsg = json.getString("AppRetMsg");
        String appRetCode = json.getString("AppRetcode");
        String status = json.getString("Status");
        String retCode = json.getString("RetCode");
        String retMsg = json.getString("RetMsg");

        if ("F".equals(acceptStatus) || "F".equals(status)) {
            throw new BizException(StringUtils.isNotBlank(retCode) ? retCode : appRetCode, StringUtils.isNotBlank(retMsg) ? retMsg : appRetMsg);
        }
        return json;
    }

    public static class CardPayResponse {
        private String orderTrxid; //支付时畅捷订单号

        public CardPayResponse(String orderTrxid) {
            this.orderTrxid = orderTrxid;
        }

        public String getOrderTrxid() {
            return orderTrxid;
        }

        public void setOrderTrxid(String orderTrxid) {
            this.orderTrxid = orderTrxid;
        }
    }

}
