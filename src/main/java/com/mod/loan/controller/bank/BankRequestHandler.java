package com.mod.loan.controller.bank;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.controller.BaseRequestHandler;
import com.mod.loan.mapper.BankMapper;
import com.mod.loan.model.Bank;
import com.mod.loan.model.Merchant;
import com.mod.loan.model.Order;
import com.mod.loan.model.User;
import com.mod.loan.service.*;
import com.mod.loan.util.GetBankUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 银行相关处理
 *
 * @author czl
 */
@Slf4j
@Component
public class BankRequestHandler extends BaseRequestHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private UserBankService userBankService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private KuaiQianService kuaiQianService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisMapper redisMapper;

    @Autowired
    private BankMapper bankMapper;

    /**
     * 银行卡鉴权发送验证码
     */
    public ResponseBean<Map<String, Object>> bankCardCode(JSONObject param) throws BizException {
        JSONObject data = parseAndCheckBizData(param);
        //订单编号
        String orderNo = data.getString("order_no");
        //绑卡卡号
        String bankCard = data.getString("bank_card");
        //预留手机号 可能为空
        String userMobile = data.getString("user_mobile");
        //开户行地址（省市） 可能为空
        String bankAddres = data.getString("bank_addres");

        if (!GetBankUtil.checkBankCard(bankCard)) {
            throw new BizException("银行卡号不正确");
        }

        String bank_url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=%s&cardBinCheck=true";
        String url = String.format(bank_url, bankCard);
        JSONObject json;
        try {
            json = JSONObject.parseObject(
                    Jsoup.connect(url).cookie("spanner", "").ignoreContentType(true).execute().body());
        } catch (IOException e) {
            throw new BizException("校验银行卡号异常");
        }
        if (!json.getBooleanValue("validated")) {
            throw new BizException("银行卡号不正确");
        }
        if (!"DC".equals(json.getString("cardType"))) {
            throw new BizException("只支持储蓄卡");
        }

        User user = userService.selectByPrimaryKey(RequestThread.getUid());
        if (StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getUserCertNo())) {
            throw new BizException("实名认证未完成");
        }

        ResultMessage message;
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        switch (merchant.getBindType()) {
            case 4:
                message = userBankService.sendBaoFooSms(RequestThread.getUid(), bankCard, userMobile);
                break;
            case 5:
                message = kuaiQianService.sendKuaiQianSms(RequestThread.getUid(), bankCard, userMobile);
                break;
            default:
                throw new BizException("支付渠道异常");
        }

        if (ResponseEnum.M2000.getCode().equals(message.getStatus())) {
            Map<String, Object> map = new HashMap<>();
            map.put("need_confirm ", "1");
            return ResponseBean.success(map);
        }

        throw new BizException(message.getStatus(), message.getMessage());
    }

    /**
     * 绑定银行卡
     */
    public ResponseBean<Map<String, Object>> bankBind(JSONObject param) throws BizException {
        JSONObject data = parseAndCheckBizData(param);
        //订单编号
        String orderNo = data.getString("order_no");
        //绑卡卡号
        String bankCard = data.getString("bank_card");
        //绑卡开户行编码
        String openBank = data.getString("open_bank");
        //预留手机号 可能为空
        String userMobile = data.getString("user_mobile");
        //用户填写的验证码
        String verifyCode = data.getString("verify_code");
        if (StringUtils.isBlank(verifyCode)) {
            throw new BizException("验证码不能为空");
        }
        if (verifyCode.length() > 6) {
            throw new BizException("验证码长度过长");
        }
        User user = userService.selectByPrimaryKey(RequestThread.getUid());
        Order order = orderService.findUserLatestOrder(user.getId());
        if (order != null && order.getStatus() < 40) {
            throw new BizException("当前无法绑定银行卡");
        }
        Long uid = RequestThread.getUid();
        String bindInfo = redisMapper.get(RedisConst.user_bank_bind + uid);
        if (StringUtils.isBlank(bindInfo)) {
            throw new BizException("验证码失效,请重新获取");
        }
        if (!redisMapper.lock(RedisConst.lock_user_bind_card_code + uid, 2)) {
            throw new BizException("操作过于频繁");
        }

        Bank bank = bankMapper.selectByPrimaryKey(openBank);
        String bankName = bank == null ? "" : bank.getBankName();
        if (StringUtils.isBlank(userMobile)) {
            userMobile = user.getUserPhone();
        }

        ResultMessage message;
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        switch (merchant.getBindType()) {
            case 4:
                message = userBankService.bindBaoFooSms(verifyCode, uid, bindInfo, bankCard, userMobile, openBank, bankName);
                break;
            case 5:
                message = kuaiQianService.bindKuaiQianSms(verifyCode, uid, bindInfo, bankCard, userMobile, openBank, bankName);
                break;
            default:
                throw new BizException("支付渠道异常");
        }

        if (ResponseEnum.M2000.getCode().equals(message.getStatus())) {
            Map<String, Object> map = new HashMap<>();
            map.put("deal_result", "1");
            return ResponseBean.success(map);
        }

        throw new BizException(message.getStatus(), message.getMessage());
    }
}
