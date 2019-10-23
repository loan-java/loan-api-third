package com.mod.loan.controller.juhe.bank;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.controller.check.LoginCheck;
import com.mod.loan.mapper.BankMapper;
import com.mod.loan.model.*;
import com.mod.loan.model.vo.UserBankInfoVO;
import com.mod.loan.service.*;
import com.mod.loan.util.Base64Util;
import com.mod.loan.util.CheckUtils;
import com.mod.loan.util.GetBankUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户银行卡绑定
 *
 * @author wugy 2018年5月3日 下午9:31:27
 */
@CrossOrigin("*")
@RestController
public class JuHeBankController {
    private static Logger log = LoggerFactory.getLogger(JuHeBankController.class);
    private static String bank_url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=%s&cardBinCheck=true";
    private static String alipay_cookie_spanner = "";

    @Value("${environment:}")
    private String environment;

    @Autowired
    private UserBankService userBankService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginCheck loginCheck;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private KuaiQianService kuaiQianService;

    @Autowired
    private BaofooService baofooService;

    @Autowired
    private YeePayService yeePayService;
    @Autowired
    private ChanpayService chanpayService;

    @Autowired
    private BankMapper bankMapper;

    /**
     * 银行卡鉴权发送验证码
     *
     * @param param cardNo     银行卡号 rsa加密传输
     *              bankMobile 银行预留手机号 rsa加密传输
     */
    @RequestMapping(value = "/bankCardCode")
    @LoginRequired()
    @Api
    public JSONObject bankCardCode(HttpServletRequest request, @RequestBody JSONObject param) {
        log.info("=====银行卡鉴权发送验证码=====");
        log.info("请求参数：" + JSON.toJSONString(param));

        ResultMessage m = loginCheck.check(request, param, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }

        String cardNo = Base64Util.juHeRsaDecode(param.getString("cardNo"));
        String bankMobile = Base64Util.juHeRsaDecode(param.getString("bankMobile"));
        //todo 确定cardcode字段
        String bankCode = Base64Util.juHeRsaDecode(param.getString("cardCode"));

        log.info("cardNo =" + cardNo + ", bankMobile = " + bankMobile + "，bankCode =" + bankCode);

        if (!GetBankUtil.checkBankCard(cardNo)) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "银行卡号不正确");
        }

        String url = String.format(bank_url, cardNo);
        JSONObject json;
        try {
            json = JSONObject.parseObject(
                    Jsoup.connect(url).cookie("spanner", alipay_cookie_spanner).ignoreContentType(true).execute().body());
        } catch (IOException e) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "校验银行卡号异常");
        }
        if (!json.getBooleanValue("validated")) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "银行卡号不正确");
        }
        if (!"DC".equals(json.getString("cardType"))) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "只支持储蓄卡");
        }

        User user = userService.selectByPrimaryKey(RequestThread.getUid());
        if (StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getUserCertNo())) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "实名认证未完成");
        }


        if (!CheckUtils.isMobiPhoneNum(bankMobile)) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "手机号不合法");
        }
        UserBank userBank = userBankService.selectUserCurrentBankCard(RequestThread.getUid());
        if (userBank != null && userBank.getCardNo().equals(cardNo)) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "当前银行卡已绑定");
        }

        Bank bank = bankMapper.selectByPrimaryKey(bankCode);
        if (bank == null || bank.getBankStatus() == 0) {
            return ResultMap.fail(ResponseEnum.M4005.getCode(), "不支持该银行");
        }

        Long uid = RequestThread.getUid();
        if (!redisMapper.lock(RedisConst.lock_user_bind_card_code + uid, 2)) {
            return ResultMap.fail(ResponseEnum.M4005.getCode(), "操作过于频繁");
        }


        ResultMessage message;
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        Order order = orderService.findUserLatestOrder(RequestThread.getUid());
        switch (merchant.getBindType()) {
            case 4:
                message = baofooService.sendBaoFooSms(RequestThread.getUid(), cardNo, bankMobile, bank);
                break;
            case 5:
                message = kuaiQianService.sendKuaiQianSms(RequestThread.getUid(), cardNo, bankMobile, bank);
                break;
            case 6:
                message = chanpayService.bindCardRequest(RequestThread.getUid(), cardNo, bankMobile, bank);
                break;
            case 7:
                message = yeePayService.requestBindCard(RequestThread.getUid(), cardNo, bankMobile, bank);
                break;
            default:
                return ResultMap.fail(ResponseEnum.M4000.getCode(), "支付渠道异常");
        }

        if (ResponseEnum.M2000.getCode().equals(message.getStatus())) {
            return ResultMap.success();
        }

        return ResultMap.fail(message.getStatus(), message.getMsg());
    }

    /**
     * 绑定银行卡
     */
    @RequestMapping(value = "/bankBind")
    @LoginRequired()
    public JSONObject bankBind(HttpServletRequest request, @RequestBody JSONObject param) {
        log.info("=====绑定银行卡=====");
        log.info("请求参数：" + JSON.toJSONString(param));

        ResultMessage m = loginCheck.check(request, param, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }

        String cardNo = Base64Util.juHeRsaDecode(param.getString("cardNo"));
        String bankCode = param.getString("bankCode");
        String bankName = param.getString("bankName");
        String bankMobile = Base64Util.juHeRsaDecode(param.getString("bankMobile"));
        String bankAuthOrderNo = param.getString("bankAuthOrderNo");
        String smsCode = param.getString("smsCode");

        System.out.println("cardNo =" + cardNo + ", bankMobile = " + bankMobile);

        if (StringUtils.isBlank(smsCode)) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "验证码不能为空");
        }
        if (smsCode.length() > 6) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "验证码长度过长");
        }
        Order order = orderService.findUserLatestOrder(RequestThread.getUid());
        if (order != null && order.getStatus() < 40) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "当前无法绑定银行卡");
        }
        Long uid = RequestThread.getUid();
        String bindInfo = redisMapper.get(RedisConst.user_bank_bind + uid);
        if (StringUtils.isBlank(bindInfo)) {
            return ResultMap.fail(ResponseEnum.M5000.getCode(), "验证码失效,请重新获取");
        }
        if (!redisMapper.lock(RedisConst.lock_user_bind_card_code + uid, 2)) {
            return ResultMap.fail(ResponseEnum.M4005.getCode(), "操作过于频繁");
        }
        UserBankInfoVO userBankInfoVO = JSON.parseObject(bindInfo, UserBankInfoVO.class);
        ResultMessage message;
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        switch (merchant.getBindType()) {
            case 4:
                message = baofooService.bindBaoFooSms(smsCode, uid, userBankInfoVO);
                break;
            case 5:
                message = kuaiQianService.bindKuaiQianSms(smsCode, uid, userBankInfoVO);
                break;
            case 6:
                message = chanpayService.bindCardConfirm(smsCode, uid, userBankInfoVO);
                break;
            case 7:
                message = yeePayService.confirmBindCard(smsCode, uid, userBankInfoVO);
                break;
            default:
                return ResultMap.fail(ResponseEnum.M4000.getCode(), "支付渠道异常");
        }

        if (ResponseEnum.M2000.getCode().equals(message.getStatus())) {
            Map<String, Object> map = new HashMap<>(1);
            map.put("cardId", message.getData());

            JSONObject object = ResultMap.success();
            object.put("data", map);
            return object;
        }

        return ResultMap.fail(message.getStatus(), message.getMsg());
    }
}