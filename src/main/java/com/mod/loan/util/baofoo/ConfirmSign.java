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

public class ConfirmSign {
    /**
     * 确认绑卡（协议支付）
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //报文发送日期时间
        String send_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //商户私钥
        String pfxpath = "D:\\CER_EN_DECODE\\AgreementPay\\bfkey_100025773@@200001173.pfx";
        //宝付公钥
        String cerpath = "D:\\CER_EN_DECODE\\AgreementPay\\bfkey_100025773@@200001173.cer";

        //短信验证码，测试环境随机6位数;生产环境验证码预绑卡成功后发到用户手机。确认绑卡时回传。
        String SMSStr = "123456";

        //商户自定义（可随机生成  商户自定义(AES key长度为=16位)）
        String AesKey = "4f66405c4f66405c";
        //使用接收方的公钥加密后的对称密钥，并做Base64转码，明文01|对称密钥，01代表AES[密码商户自定义]
        String dgtl_envlp = "01|" + AesKey;

        Log.write("密码dgtl_envlp：" + dgtl_envlp);
        //公钥加密
        dgtl_envlp = RsaCodingUtil.encryptByPubCerFile(SecurityUtil.Base64Encode(dgtl_envlp), cerpath);
        //预签约唯一码(预绑卡返回的值)[格式：预签约唯一码|短信验证码]
        String UniqueCode = "201803221845403000550|" + SMSStr;
        Log.write("预签约唯一码：" + UniqueCode);
        //先BASE64后进行AES加密
        UniqueCode = SecurityUtil.AesEncrypt(SecurityUtil.Base64Encode(UniqueCode), AesKey);
        Log.write("AES结果:" + UniqueCode);

        Map<String, String> DateArry = new TreeMap<>();
        DateArry.put("send_time", send_time);
        //报文流水号
        DateArry.put("msg_id", "TISN" + System.currentTimeMillis());
        DateArry.put("version", "4.0.0.0");
        DateArry.put("terminal_id", "200001173");
        //交易类型
        DateArry.put("txn_type", "02");
        DateArry.put("member_id", "100025773");
        DateArry.put("dgtl_envlp", dgtl_envlp);
        //预签约唯一码
        DateArry.put("unique_code", UniqueCode);

        String SignVStr = FormatUtil.coverMap2String(DateArry);
        Log.write("SHA-1摘要字串：" + SignVStr);
        //签名
        String signature = SecurityUtil.sha1X16(SignVStr, "UTF-8");
        Log.write("SHA-1摘要结果：" + signature);
        String Sign = SignatureUtils.encryptByRSA(signature, pfxpath, "100025773_286941");
        Log.write("RSA签名结果：" + Sign);
        //签名域
        DateArry.put("signature", Sign);

        String PostString = HttpUtil.RequestForm("https://vgw.baofoo.com/cutpayment/protocol/backTransRequest", DateArry);
        Log.write("请求返回:" + PostString);

        Map<String, String> ReturnData = FormatUtil.getParm(PostString);

        if (!ReturnData.containsKey("signature")) {
            throw new Exception("缺少验签参数！");
        }

        String RSign = ReturnData.get("signature");
        Log.write("返回的验签值：" + RSign);
        //需要删除签名字段
        ReturnData.remove("signature");
        String RSignVStr = FormatUtil.coverMap2String(ReturnData);
        Log.write("返回SHA-1摘要字串：" + RSignVStr);
        //签名
        String RSignature = SecurityUtil.sha1X16(RSignVStr, "UTF-8");
        Log.write("返回SHA-1摘要结果：" + RSignature);

        if (SignatureUtils.verifySignature(cerpath, RSignature, RSign)) {
            //验签成功
            Log.write("Yes");
        }
        if (!ReturnData.containsKey("resp_code")) {
            throw new Exception("缺少resp_code参数！");
        }
        if ("S".equals(ReturnData.get("resp_code"))) {
            if (!ReturnData.containsKey("dgtl_envlp")) {
                throw new Exception("缺少dgtl_envlp参数！");
            }
            String RDgtlEnvlp = SecurityUtil.Base64Decode(RsaCodingUtil.decryptByPriPfxFile(ReturnData.get("dgtl_envlp"), pfxpath, "100025773_286941"));
            Log.write("返回数字信封：" + RDgtlEnvlp);
            //获取返回的AESkey
            String RAesKey = FormatUtil.getAesKey(RDgtlEnvlp);
            Log.write("返回的AESkey:" + RAesKey);
            Log.write("签约协议号:" + SecurityUtil.Base64Decode(SecurityUtil.AesDecrypt(ReturnData.get("protocol_no"), RAesKey)));
        } else if ("I".equals(ReturnData.get("resp_code"))) {
            Log.write("处理中！");
        } else if ("F".equals(ReturnData.get("resp_code"))) {
            Log.write("失败！");
        } else {
            //异常不得做为订单状态。
            throw new Exception("反回异常！");
        }
    }
}