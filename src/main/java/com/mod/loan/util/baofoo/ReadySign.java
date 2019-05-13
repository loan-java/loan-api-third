package com.mod.loan.util.baofoo;


import com.mod.loan.util.baofoo.rsa.RsaCodingUtil;
import com.mod.loan.util.baofoo.rsa.SignatureUtils;
import com.mod.loan.util.baofoo.util.FormatUtil;
import com.mod.loan.util.baofoo.util.HttpUtil;
import com.mod.loan.controller.check.Log;
import com.mod.loan.util.baofoo.util.SecurityUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class ReadySign {
    /**
     * 预绑卡（协议支付）
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //报文发送日期时间
        String sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //商户私钥
        String pfxpath = "/Users/yutian/Desktop/pay/pay/Demo/JAVA/AgreementPay/bfkey_100025773@@200001173.pfx";
        //宝付公钥
        String cerpath = "/Users/yutian/Desktop/pay/pay/Demo/JAVA/AgreementPay/bfkey_100025773@@200001173.cer";
        //商户自定义（可随机生成  商户自定义(AES key长度为=16位)）
        String aesKey = "4f66405c4f77405c";
        //使用接收方的公钥加密后的对称密钥，并做Base64转码，明文01|对称密钥，01代表AES[密码商户自定义]
        String dgtlEnvlp = "01|" + aesKey;
        Log.write("密码dgtl_envlp：" + dgtlEnvlp);
        //公钥加密
        dgtlEnvlp = RsaCodingUtil.encryptByPubCerFile(SecurityUtil.Base64Encode(dgtlEnvlp), cerpath);
        //账户信息[银行卡号|持卡人姓名|证件号|手机号|银行卡安全码|银行卡有效期]
        String cardInfo = "6222021202041701419|帅立杰|330124199212274817|15868417851||";

        //先BASE64后进行AES加密
        cardInfo = SecurityUtil.AesEncrypt(SecurityUtil.Base64Encode(cardInfo), aesKey);
        Map<String, String> dateArray = new TreeMap<>();
        dateArray.put("send_time", sendTime);
        //报文流水号
        dateArray.put("msg_id", "TISN" + System.currentTimeMillis());
        dateArray.put("version", "4.0.0.0");
        dateArray.put("terminal_id", "200001173");
        //交易类型
        dateArray.put("txn_type", "01");
        dateArray.put("member_id", "100025773");
        dateArray.put("dgtl_envlp", dgtlEnvlp);
        //用户在商户平台唯一ID
        dateArray.put("user_id", "181290");
        //卡类型  101	借记卡，102 信用卡
        dateArray.put("card_type", "101");
        //证件类型
        dateArray.put("id_card_type", "01");
        dateArray.put("acc_info", cardInfo);

        String signVStr = FormatUtil.coverMap2String(dateArray);
        //签名
        String signature = SecurityUtil.sha1X16(signVStr, "UTF-8");
        String sign = SignatureUtils.encryptByRSA(signature, pfxpath, "100025773_286941");
        //签名域
        dateArray.put("signature", sign);

        String postString = HttpUtil.RequestForm("https://vgw.baofoo.com/cutpayment/protocol/backTransRequest", dateArray);
        Map<String, String> returnData = FormatUtil.getParm(postString);

        if (!returnData.containsKey("signature")) {
            throw new Exception("缺少验签参数！");
        }

        String rSign = returnData.get("signature");
        //需要删除签名字段
        returnData.remove("signature");
        String rSignVStr = FormatUtil.coverMap2String(returnData);
        //签名
        String rSignature = SecurityUtil.sha1X16(rSignVStr, "UTF-8");

        if (SignatureUtils.verifySignature(cerpath, rSignature, rSign)) {
            //验签成功
            Log.write("Yes");
        }

        if (!returnData.containsKey("resp_code")) {
            throw new Exception("缺少resp_code参数！");
        }

        if ("S".equals(returnData.get("resp_code"))) {
            if (!returnData.containsKey("dgtl_envlp")) {
                throw new Exception("缺少dgtl_envlp参数！");
            }
            String rDgtlEnvLp = SecurityUtil.Base64Decode(RsaCodingUtil.decryptByPriPfxFile(returnData.get("dgtl_envlp"), pfxpath, "100025773_286941"));
            //获取返回的AESKey
            String rAesKey = FormatUtil.getAesKey(rDgtlEnvLp);
            Log.write("返回的AESkey:" + rAesKey);
            Log.write("唯一码:" + SecurityUtil.Base64Decode(SecurityUtil.AesDecrypt(returnData.get("unique_code"), rAesKey)));
        }
    }

}
