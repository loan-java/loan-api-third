package com.mod.loan.util.baofoo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.util.baofoo.rsa.RsaCodingUtil;
import com.mod.loan.util.baofoo.util.HttpUtil;
import com.mod.loan.controller.check.Log;
import com.mod.loan.util.baofoo.util.SecurityUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class Bankcard {

    /**
     * 银行卡校验
     *
     * @param memberId      宝付提供给商户的唯一编号
     * @param terminalId    宝付提供给商户的终端编号
     * @param accNo         银行卡号
     * @param idCard        身份证号码
     * @param idHolder      持卡人姓名
     * @param mobile        手机号
     * @param verifyElement 123:三要素（银行卡号 + 姓名 + 身份证号）,	1234:四要素（银行卡号 + 姓名 + 身份证号 + 银行卡预留手机号）
     * @param pfxPath       私钥路径
     * @param pfxPwd        密码
     */
    public static ResultMessage bankCardV(String memberId,
                                          String terminalId,
                                          String accNo,
                                          String idCard,
                                          String idHolder,
                                          String mobile,
                                          String verifyElement,
                                          String pfxPath,
                                          String pfxPwd,
                                          String url) {
        Map<String, String> postHeadMap = new TreeMap<>();
        postHeadMap.put("member_id", memberId);
        //报文流水号
        postHeadMap.put("terminal_id", terminalId);
        postHeadMap.put("data_type", "json");

        Map<String, String> dataContent = new TreeMap<>();
        dataContent.put("member_id", memberId);
        dataContent.put("terminal_id", terminalId);
        //商户订单号（自定义订单号）
        dataContent.put("trans_id", "TID" + System.currentTimeMillis());
        dataContent.put("trade_date", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        dataContent.put("acc_no", accNo);
        dataContent.put("id_card", idCard);
        //0:身份证,1:军官证,2:护照
        dataContent.put("id_type", "0");
        dataContent.put("id_holder", idHolder);
        //借记卡:101，	信用卡:102
        dataContent.put("card_type", "101");
        dataContent.put("mobile", mobile);
        dataContent.put("industry_type", "A1");
        dataContent.put("verify_element", verifyElement);
        dataContent.put("product_type", "0");

        String dcStr = JSON.toJSONString(dataContent);
        Log.write("业务请求参数：" + dcStr);

        String base64str;
        try {
            base64str = SecurityUtil.Base64Encode(dcStr);
        } catch (UnsupportedEncodingException e) {
            return new ResultMessage(ResponseEnum.M4000);
        }
        String enDcStr = RsaCodingUtil.encryptByPriPfxFile(base64str, pfxPath, pfxPwd);

        //放入业务请求参数。
        postHeadMap.put("data_content", enDcStr);

        String reStr = HttpUtil.RequestForm(url, postHeadMap);

        Log.write("返回数据：" + reStr);

        JSONObject object = JSONObject.parseObject(reStr);
        if (object.getBooleanValue("success")) {
            if (object.get("data") != null) {
                JSONObject data = JSONObject.parseObject(object.getString("data"));
                if ("0".equals(data.getString("code"))) {
                    return new ResultMessage(ResponseEnum.M2000);
                } else {
                    return new ResultMessage(data.getString("org_code"), data.getString("org_desc"));
                }
            }
        }
        return new ResultMessage(object.getString("errorCode"), object.getString("errorMsg"));
    }
} 