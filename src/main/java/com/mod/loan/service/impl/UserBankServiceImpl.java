package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.MerchantEnum;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.controller.check.Log;
import com.mod.loan.mapper.UserBankMapper;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.UserBankService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.baofoo.model.ReadySignVO;
import com.mod.loan.util.baofoo.rsa.RsaCodingUtil;
import com.mod.loan.util.baofoo.rsa.SignatureUtils;
import com.mod.loan.util.baofoo.util.FormatUtil;
import com.mod.loan.util.baofoo.util.HttpUtil;
import com.mod.loan.util.baofoo.util.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Service
public class UserBankServiceImpl extends BaseServiceImpl<UserBank, Long> implements UserBankService {

    private static Logger log = LoggerFactory.getLogger(UserBankServiceImpl.class);

    @Autowired
    private UserBankMapper userBankMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisMapper redisMapper;

    @Override
    public UserBank selectUserCurrentBankCard(Long uid) {
        return userBankMapper.selectUserCurrentBankCard(uid);
    }

    @Override
    public ResultMessage sendBaoFooSms(Long uid, String cardNo, String cardPhone) {
        ResultMessage message = null;
        String response = "";

        //报文发送日期时间
        String sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //商户私钥
        String pfxpath = Constant.baoFooKeyStorePath;
        //宝付公钥
        String cerpath = Constant.baoFooPubKeyPath;
        try {
            User user = userService.selectByPrimaryKey(uid);

            //商户自定义（可随机生成  商户自定义(AES key长度为=16位)）
            String aesKey = "4f66405c4f77405c";
            //使用接收方的公钥加密后的对称密钥，并做Base64转码，明文01|对称密钥，01代表AES[密码商户自定义]
            String dgtlEnvlp = "01|" + aesKey;
            //公钥加密
            dgtlEnvlp = RsaCodingUtil.encryptByPubCerFile(SecurityUtil.Base64Encode(dgtlEnvlp), cerpath);
            //账户信息[银行卡号|持卡人姓名|证件号|手机号|银行卡安全码|银行卡有效期]
            String cardInfo = cardNo + "|" + user.getUserName() + "|" + user.getUserCertNo() + "|" + cardPhone + "||";
            //先BASE64后进行AES加密
            cardInfo = SecurityUtil.AesEncrypt(SecurityUtil.Base64Encode(cardInfo), aesKey);

            Map<String, String> dateArray = new TreeMap<>();
            dateArray.put("send_time", sendTime);
            //报文流水号
            dateArray.put("msg_id", "TISN" + System.currentTimeMillis());
            dateArray.put("version", Constant.baoFooVersion);
            dateArray.put("terminal_id", Constant.baoFooTerminalId);
            //交易类型
            dateArray.put("txn_type", "01");
            dateArray.put("member_id", Constant.baoFooMemberId);
            dateArray.put("dgtl_envlp", dgtlEnvlp);
            //用户在商户平台唯一ID
            dateArray.put("user_id", uid + "");
            //卡类型  101	借记卡，102 信用卡
            dateArray.put("card_type", "101");
            //证件类型
            dateArray.put("id_card_type", "01");
            dateArray.put("acc_info", cardInfo);

            String signVStr = FormatUtil.coverMap2String(dateArray);
            log.info("绑卡请求参数:" + signVStr);
            //签名
            String signature = SecurityUtil.sha1X16(signVStr, "UTF-8");
            String sign = SignatureUtils.encryptByRSA(signature, pfxpath, Constant.baoFooKeyStorePassword);
            //签名域
            dateArray.put("signature", sign);

            String postString = HttpUtil.RequestForm(Constant.baoFooSendSmsUrl, dateArray);
            log.info("绑卡返回参数:" + postString);
            Map<String, String> returnData = FormatUtil.getParm(postString);
            ReadySignVO readySignVO = JSONObject.parseObject(JSONObject.toJSONString(returnData), ReadySignVO.class);

            if (StringUtils.isBlank(readySignVO.getSignature())) {
                //异常不得做为订单状态。
                log.error("缺少验签参数，[宝付]绑卡发送验证码返回参数：" + postString);
                throw new Exception("缺少验签参数！");
            }

            String rSign = readySignVO.getSignature();
            //需要删除签名字段
            returnData.remove("signature");
            String rSignVStr = FormatUtil.coverMap2String(returnData);
            //签名
            String rSignature = SecurityUtil.sha1X16(rSignVStr, "UTF-8");

            if (!SignatureUtils.verifySignature(cerpath, rSignature, rSign)) {
                //验签失败
                log.error("验签失败，[宝付]绑卡发送验证码返回参数：" + postString);
                return new ResultMessage(ResponseEnum.M4000, "验签失败");
            }

            if (StringUtils.isBlank(readySignVO.getResp_code())) {
                log.error("缺少resp_code参数，[宝付]绑卡发送验证码返回参数：" + postString);
                return new ResultMessage(ResponseEnum.M4000, "缺少resp_code参数！");
            }

            if ("S".equals(readySignVO.getResp_code())) {
                if (StringUtils.isBlank(readySignVO.getDgtl_envlp())) {
                    log.error("缺少dgtl_envlp参数，[宝付]绑卡发送验证码返回参数：" + postString);
                    return new ResultMessage(ResponseEnum.M4000, "缺少dgtl_envlp参数！");
                }

                String rDgtlEnvLp = SecurityUtil.Base64Decode(RsaCodingUtil.decryptByPriPfxFile(
                        readySignVO.getDgtl_envlp(), pfxpath, Constant.baoFooKeyStorePassword));
                //获取返回的AESKey
                String rAesKey = FormatUtil.getAesKey(rDgtlEnvLp);
                log.info("返回的AESkey:" + rAesKey);
                log.info("唯一码:" + SecurityUtil.Base64Decode(SecurityUtil.AesDecrypt(readySignVO.getUnique_code(), rAesKey)));

                readySignVO.setUnique_code(SecurityUtil.Base64Decode(
                        SecurityUtil.AesDecrypt(readySignVO.getUnique_code(), rAesKey)));
                redisMapper.set(RedisConst.user_bank_bind + uid, readySignVO, 600);
                return new ResultMessage(ResponseEnum.M2000);
            } else {
                log.error("宝付鉴权绑卡短信发送失败，请求参数为={},响应参数为={}",
                        JSON.toJSONString(dateArray), JSON.toJSONString(readySignVO));
                return new ResultMessage(ResponseEnum.M4000.getCode(), readySignVO.getBiz_resp_msg());
            }
        } catch (Exception e) {
            log.error("宝付鉴权绑卡短信发送异常", e);
            message = new ResultMessage(ResponseEnum.M4000);
        }

        return message;
    }

    @Override
    public ResultMessage bindBaoFooSms(String validateCode, Long uid, String bindInfo, String cardNo,
                                       String cardPhone, String bankCode, String bankName) {
        ResultMessage message = null;
        ReadySignVO readySignVO = JSONObject.parseObject(bindInfo, ReadySignVO.class);
        try {
            //报文发送日期时间
            String sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //商户私钥
            String pfxpath = Constant.baoFooKeyStorePath;
            //宝付公钥
            String cerpath = Constant.baoFooPubKeyPath;

            //商户自定义（可随机生成  商户自定义(AES key长度为=16位)）
            String aesKey = "4f66405c4f66405c";
            //使用接收方的公钥加密后的对称密钥，并做Base64转码，明文01|对称密钥，01代表AES[密码商户自定义]
            String dgtlEnvlp = "01|" + aesKey;
            //公钥加密
            dgtlEnvlp = RsaCodingUtil.encryptByPubCerFile(SecurityUtil.Base64Encode(dgtlEnvlp), cerpath);
            //预签约唯一码(预绑卡返回的值)[格式：预签约唯一码|短信验证码]
            String uniqueCode = readySignVO.getUnique_code() + "|" + validateCode;
            //先BASE64后进行AES加密
            uniqueCode = SecurityUtil.AesEncrypt(SecurityUtil.Base64Encode(uniqueCode), aesKey);

            Map<String, String> dateArray = new TreeMap<>();
            dateArray.put("send_time", sendTime);
            //报文流水号
            dateArray.put("msg_id", "TISN" + System.currentTimeMillis());
            dateArray.put("version", Constant.baoFooVersion);
            dateArray.put("terminal_id", Constant.baoFooTerminalId);
            //交易类型
            dateArray.put("txn_type", "02");
            dateArray.put("member_id", Constant.baoFooMemberId);
            dateArray.put("dgtl_envlp", dgtlEnvlp);
            //预签约唯一码
            dateArray.put("unique_code", uniqueCode);

            String signVStr = FormatUtil.coverMap2String(dateArray);
            //签名
            String signature = SecurityUtil.sha1X16(signVStr, "UTF-8");
            String sign = SignatureUtils.encryptByRSA(signature, pfxpath, Constant.baoFooKeyStorePassword);
            //签名域
            dateArray.put("signature", sign);

            String postString = HttpUtil.RequestForm(Constant.baoFooBindSmsUrl, dateArray);
            log.info("[宝付]绑定银行卡返回参数：" + postString);

            Map<String, String> returnData = FormatUtil.getParm(postString);

            if (!returnData.containsKey("signature")) {
                log.error("缺少验签参数，[宝付]绑定银行卡返回参数：" + postString);
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
                log.info("Yes");
            }
            if (!returnData.containsKey("resp_code")) {
                log.error("缺少resp_code参数，[宝付]绑定银行卡返回参数：" + postString);
                throw new Exception("缺少resp_code参数！");
            }
            if ("S".equals(returnData.get("resp_code"))) {
                if (!returnData.containsKey("dgtl_envlp")) {
                    log.error("缺少dgtl_envlp参数，[宝付]绑定银行卡返回参数：" + postString);
                    throw new Exception("缺少dgtl_envlp参数！");
                }
                String rDgtlEnvlp = SecurityUtil.Base64Decode(RsaCodingUtil.decryptByPriPfxFile(returnData.get("dgtl_envlp"), pfxpath, Constant.baoFooKeyStorePassword));
                //获取返回的AESKey
                String rAesKey = FormatUtil.getAesKey(rDgtlEnvlp);
                //签约协议号
                String protocolNo = SecurityUtil.Base64Decode(SecurityUtil.AesDecrypt(returnData.get("protocol_no"), rAesKey));
                UserBank userBank = new UserBank();
                userBank.setCardCode(bankCode);
                userBank.setCardName(bankName);
                userBank.setCardNo(cardNo);
                userBank.setCardPhone(cardPhone);
                userBank.setCardStatus(1);
                userBank.setCreateTime(new Date());
                userBank.setForeignId(protocolNo);
                userBank.setUid(uid);
                userBank.setBindType(MerchantEnum.baofoo.getCode());
                userService.insertUserBank(uid, userBank);
                redisMapper.remove(RedisConst.user_bank_bind + uid);
                return new ResultMessage(ResponseEnum.M2000, userBank.getId());
            } else {
                //异常不得做为订单状态。
                log.error("宝付鉴权绑卡失败，请求参数为={},响应参数为={}",
                        JSON.toJSONString(dateArray), JSON.toJSONString(returnData));
                return new ResultMessage(ResponseEnum.M4000.getCode(), returnData.get("biz_resp_msg"));
            }
        } catch (Exception e) {
            log.error("宝付绑卡异常", e);
            message = new ResultMessage(ResponseEnum.M4000);
        }

        return message;
    }
}
