package com.mod.loan.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.message.OrderRepayQueryMessage;
import com.mod.loan.common.message.RiskAuditMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.controller.check.Log;
import com.mod.loan.mapper.MerchantRateMapper;
import com.mod.loan.model.*;
import com.mod.loan.service.*;
import com.mod.loan.util.MoneyUtil;
import com.mod.loan.util.StringUtil;
import com.mod.loan.util.TimeUtils;
import com.mod.loan.util.baofoo.rsa.RsaCodingUtil;
import com.mod.loan.util.baofoo.rsa.SignatureUtils;
import com.mod.loan.util.baofoo.util.FormatUtil;
import com.mod.loan.util.baofoo.util.HttpUtil;
import com.mod.loan.util.baofoo.util.SecurityUtil;
import com.mod.loan.util.rongze.BizDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.mapper.OrderPayMapper;
import com.mod.loan.mapper.OrderPhoneMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Slf4j
@Service
public class OrderServiceImpl extends BaseServiceImpl<Order, Long> implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderPhoneMapper orderPhoneMapper;
    @Resource
    private OrderPayMapper orderPayMapper;

    @Resource
    private UserService userService;
    @Resource
    private MerchantService merchantService;
    @Resource
    private RedisMapper redisMapper;
    @Resource
    private UserIdentService userIdentService;
    @Resource
    private BlacklistService blacklistService;
    @Resource
    private MerchantRateService merchantRateService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private KuaiQianService kuaiQianService;
    @Resource
    private UserBankService userBankService;
    @Resource
    private OrderRepayService orderRepayService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Order repayOrder(String orderNo) throws BizException {

        Order order = findOrderByOrderNo(orderNo);
        if (order == null) throw new BizException("订单不存在");

        ResultMessage message;
        Merchant merchant = merchantService.findMerchantByAlias(Constant.merchant);
        log.info("还款Merchant(" + Constant.merchant + "): " + JSONObject.toJSONString(merchant));

        if (merchant != null) {
            switch (merchant.getBindType()) {
                case 4:
                    message = repayBaofoo(order);
                    break;
                case 5:
                    message = kuaiQianService.repay(order);
                    break;
                default:
                    throw new BizException("支付渠道异常");
            }
        } else {
            message = kuaiQianService.repay(orderNo);
        }

        if (ResponseEnum.M2000.getCode().equals(message.getStatus())) {
            JSONObject res = JSONObject.parseObject(JSON.toJSONString(message.getData()));
            order.setRepayOrderNo(res.getString("repayOrderNo"));
            return order;
        }
        throw new BizException(message.getStatus(), message.getMessage());
    }

    @Override
    public Order findOrderByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Order submitOrder(String orderNo, String loanAmount, int loanTerm) throws BizException {
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        if (merchant == null) {
            throw new BizException("商户【" + RequestThread.getClientAlias() + "】不存在，未配置");
        }

        MerchantRate record = merchantRateService.findByMerchant(RequestThread.getClientAlias());
        Long productId = record.getId();

        Long uid = RequestThread.getUid();
        if (!redisMapper.lock(RedisConst.lock_user_order + uid, 5)) {
            throw new BizException("操作过于频繁");
        }
        UserIdent userIdent = userIdentService.selectByPrimaryKey(uid);
        if (2 != userIdent.getRealName() || 2 != userIdent.getUserDetails() || 2 != userIdent.getBindbank()
                || 2 != userIdent.getMobile() || 2 != userIdent.getLiveness()) {
            // 提示认证未完成
            throw new BizException("认证未完成");
        }
        Blacklist blacklist = blacklistService.getByUid(uid);
        if (null != blacklist) {
            // 校验灰名单锁定天数
            if (1 == blacklist.getType()) {
                DateTime d1 = new DateTime(new Date());
                DateTime d2 = new DateTime(blacklist.getInvalidTime());
                Integer remainDays = Days.daysBetween(d1, d2).getDays() + 1;
                throw new BizException("暂时无法下单，请于" + remainDays + "天后再尝试");
            }
            // 黑名单
            if (2 == blacklist.getType()) {
                throw new BizException("您不符合下单条件");
            }
        }
        // 是否有正在借款中的订单
        Order orderIng = findUserLatestOrder(uid);
        if (null != orderIng) {
            if (orderIng.getStatus() < 40) {
                throw new BizException("已有订单进行中，无法提单");
            }
            // 审核拒绝的订单7天内无法再下单
            if (orderIng.getStatus() == 51 || orderIng.getStatus() == 52) {
                DateTime applyTime = new DateTime(orderIng.getCreateTime()).plusDays(7);
                DateTime nowTime = new DateTime();
                Integer remainDays = Days.daysBetween(nowTime.withMillisOfDay(0), applyTime.withMillisOfDay(0))
                        .getDays();
                if (0 < remainDays && remainDays <= 7) {
                    throw new BizException("请" + remainDays + "天后重试提单");
                }
            }
        }
        Order order = new Order();
        MerchantRate merchantRate = merchantRateService.selectByPrimaryKey(productId);
        if (null == merchantRate) {
            throw new BizException("未查到规则");
        }
        if (merchantRate.getProductMoney().compareTo(new BigDecimal(loanAmount)) != 0
                || merchantRate.getProductDay() != loanTerm) {
            throw new BizException("规则不匹配");
        }
        // 综合费用
        BigDecimal totalFee = MoneyUtil.totalFee(merchantRate.getProductMoney(), merchantRate.getTotalRate());
        // 利息
        BigDecimal interestFee = MoneyUtil.interestFee(merchantRate.getProductMoney(), 1,
                merchantRate.getProductRate());
        // 实际到账
        BigDecimal actualMoney = MoneyUtil.actualMoney(merchantRate.getProductMoney(), totalFee);
        // 应还金额
        BigDecimal shouldRepay = MoneyUtil.shouldrepay(merchantRate.getProductMoney(), interestFee, new BigDecimal(0),
                new BigDecimal(0));
        // 判断客群
        Integer userType = judgeUserTypeByUid(uid);

        order.setOrderNo(BizDataUtil.getRZOrderNo(orderNo));
        order.setUid(uid);
        order.setBorrowDay(merchantRate.getProductDay());
        order.setBorrowMoney(merchantRate.getProductMoney());
        order.setActualMoney(actualMoney);
        order.setTotalRate(merchantRate.getTotalRate());
        order.setTotalFee(totalFee);
        order.setInterestRate(merchantRate.getProductRate());
        order.setOverdueDay(0);
        order.setOverdueFee(new BigDecimal(0));
        order.setOverdueRate(merchantRate.getOverdueRate());
        order.setInterestFee(interestFee);
        order.setShouldRepay(shouldRepay);
        order.setHadRepay(new BigDecimal(0));
        order.setReduceMoney(new BigDecimal(0));
        order.setStatus(11);
        order.setCreateTime(new Date());
        order.setMerchant(RequestThread.getClientAlias());
        order.setProductId(productId);
        order.setUserType(userType);
        order.setPaymentType(merchant.getPaymentType());
        OrderPhone orderPhone = new OrderPhone();
        orderPhone.setParamValue("");
        orderPhone.setPhoneModel("third|7");
        orderPhone.setPhoneType("H5");
        addOrder(order, orderPhone);

        // 通知风控
        RiskAuditMessage message = new RiskAuditMessage();
        message.setOrderId(order.getId());
        message.setStatus(1);
        message.setMerchant(RequestThread.getClientAlias());
        message.setUid(uid);
        message.setUserPhone(userService.selectByPrimaryKey(uid).getUserPhone());
        try {
            rabbitTemplate.convertAndSend(RabbitConst.queue_risk_order_notify, message);
        } catch (Exception e) {
            log.error("通知风控消息发送异常：" + e.getMessage(), e);
        }

        return order;
    }

    @Override
    public Order findUserLatestOrder(Long uid) {
        // TODO Auto-generated method stub
        return orderMapper.findUserLatestOrder(uid);
    }

    @Override
    public List<Order> getByUid(Long uid) {
        return orderMapper.getByUid(uid);
    }

    @Override
    public int addOrder(Order order, OrderPhone orderPhone) {
        // TODO Auto-generated method stub
        orderMapper.insertSelective(order);
        orderPhone.setOrderId(order.getId());
        return orderPhoneMapper.insertSelective(orderPhone);
    }

    @Override
    public OrderPhone findOrderPhoneByOrderId(Long orderId) {
        // TODO Auto-generated method stub
        return orderPhoneMapper.selectByPrimaryKey(orderId);
    }

    @Override
    public OrderPay findOrderPaySuccessRecord(Long orderId) {
        return orderPayMapper.selectByOrderIdAndStatus(orderId, 3);
    }

    @Override
    public Integer judgeUserTypeByUid(Long uid) {
        return orderMapper.judgeUserTypeByUid(uid);
    }

    @Override
    public Integer countByUid(Long uid) {
        Order order = new Order();
        order.setUid(uid);
        return orderMapper.selectCount(order);
    }

    @Override
    public Integer countPaySuccessByUid(Long uid) {
        return orderMapper.countPaySuccessByUid(uid);
    }

    /**
     * 宝付还款
     */
    private ResultMessage repayBaofoo(Order order) {
        //报文发送日期时间
        String sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //商户私钥
        String pfxpath = Constant.baoFooKeyStorePath;
        //宝付公钥
        String cerpath = Constant.baoFooPubKeyPath;

        long uid = order.getUid();

        UserBank userBank = userBankService.selectUserCurrentBankCard(uid);
        User user = userService.selectByPrimaryKey(uid);

        // 支付流水号
        String orderSeriesId = StringUtil.getOrderNumber("bf");
        try {
            //商户自定义(可随机生成  AES key长度为=16位)
            String aesKey = "4f66405c4f77405c";
            //使用接收方的公钥加密后的对称密钥，并做Base64转码，明文01|对称密钥，01代表AES[密码商户自定义]
            String dgtlEnvlp = "01|" + aesKey;
            //公钥加密
            dgtlEnvlp = RsaCodingUtil.encryptByPubCerFile(SecurityUtil.Base64Encode(dgtlEnvlp), cerpath);
            //签约协议号（确认支付返回）
            String protocolNo = userBank.getForeignId();
            //先BASE64后进行AES加密
            protocolNo = SecurityUtil.AesEncrypt(SecurityUtil.Base64Encode(protocolNo), aesKey);
            //信用卡：信用卡有效期|安全码,借记卡：传空
            String cardInfo = "";
            //结果回调
            String returnUrl = "";

            Map<String, String> dateArray = new TreeMap<>();
            dateArray.put("send_time", sendTime);
            //报文流水号
            dateArray.put("msg_id", "TISN" + System.currentTimeMillis());
            dateArray.put("version", Constant.baoFooVersion);
            dateArray.put("terminal_id", Constant.baoFooTerminalId);
            //交易类型(参看：文档中《交易类型枚举》)
            dateArray.put("txn_type", "08");
            dateArray.put("member_id", Constant.baoFooMemberId);

            dateArray.put("trans_id", orderSeriesId);
            dateArray.put("dgtl_envlp", dgtlEnvlp);
            //用户在商户平台唯一ID (和绑卡时要一致)
            dateArray.put("user_id", uid + "");
            //签约协议号（密文）
            dateArray.put("protocol_no", protocolNo);
            //交易金额 [单位：分  例：1元则提交100]，此处注意数据类型的转转，建议使用BigDecimal类弄进行转换为字串
            //以分为单位，取整
            long amount;
            if ("dev".equals(Constant.ENVIROMENT)) {
                amount = 1L;
            } else {
                amount = new BigDecimal(100).multiply(order.getShouldRepay()).longValue();
            }
            dateArray.put("txn_amt", amount + "");
            //卡信息
            dateArray.put("card_info", cardInfo);

            Map<String, String> riskItem = new HashMap<>();
            //--------风控基础参数-------------
            // 说明风控参数必须，按商户开通行业、真实交易信息传，不可传固定值。

            //行业类目 详见附录《行业类目》
            riskItem.put("goodsCategory", "02");
            //用户在商户系统中的登陆名（手机号、邮箱等标识）
            riskItem.put("userLoginId", user.getUserPhone());
            riskItem.put("userEmail", "");
            //用户手机号
            riskItem.put("userMobile", user.getUserPhone());
            //用户在商户系统中注册使用的名字
            riskItem.put("registerUserName", user.getUserName());
            //用户在平台是否已实名，1：是 ；0：不是
            riskItem.put("identifyState", "1");
            //用户身份证号
            riskItem.put("userIdNo", user.getUserCertNo());
            //格式为：YYYYMMDDHHMMSS
            riskItem.put("registerTime", TimeUtils.parseTime(user.getCreateTime(), TimeUtils.dateformat5));
            //用户在商户端注册时留存的IP
            riskItem.put("registerIp", RequestThread.getIp());
            //持卡人姓名
            riskItem.put("chName", user.getUserName());
            //持卡人身份证号
            riskItem.put("chIdNo", user.getUserCertNo());
            //持卡人银行卡号
            riskItem.put("chCardNo", userBank.getCardNo());
            //持卡人手机
            riskItem.put("chMobile", userBank.getCardPhone());
            //持卡人支付IP
            riskItem.put("chPayIp", RequestThread.getIp());
            //加载设备指纹中的订单号
            riskItem.put("deviceOrderNo", "");

            //放入风控参数
            dateArray.put("risk_item", JSONObject.toJSONString(riskItem));

            //最多填写三个地址,不同地址用间使用‘|’分隔
            dateArray.put("return_url", returnUrl);
            String signVStr = FormatUtil.coverMap2String(dateArray);
            //签名
            String signature = SecurityUtil.sha1X16(signVStr, "UTF-8");
            String sign = SignatureUtils.encryptByRSA(signature, pfxpath, Constant.baoFooKeyStorePassword);
            //签名域
            dateArray.put("signature", sign);

            String postString = HttpUtil.RequestForm(Constant.baoFooRepayUrl, dateArray);
            log.info("请求宝付协议支付返回接口: {}", postString);

            Map<String, String> returnData = FormatUtil.getParm(postString);

            if (!returnData.containsKey("signature")) {
                //缺少验签参数！
                log.error("请求宝付协议支付，返回结果缺少验签参数！，还款订单号={}, rseponse", orderSeriesId, postString);
                return new ResultMessage(ResponseEnum.M4000.getCode(), "还款异常");
            }
            String rSign = returnData.get("signature");
            //需要删除签名字段
            returnData.remove("signature");
            String rSignVStr = FormatUtil.coverMap2String(returnData);
            //签名
            String rSignature = SecurityUtil.sha1X16(rSignVStr, "UTF-8");

            if (!SignatureUtils.verifySignature(cerpath, rSignature, rSign)) {
                //验签成功
                log.error("请求宝付协议支付，返回结果缺少验签参数！，还款订单号={}, rseponse", orderSeriesId, postString);
                return new ResultMessage(ResponseEnum.M4000.getCode(), "还款异常");
            }
            if (!returnData.containsKey("resp_code")) {
                //缺少resp_code参数！
                log.error("请求宝付协议支付，返回结果缺少resp_code参数，还款订单号={}, rseponse", orderSeriesId, postString);
                return new ResultMessage(ResponseEnum.M4000.getCode(), "还款异常");
            }
            if ("S".equals(returnData.get("resp_code"))
                    || "I".equals(returnData.get("resp_code"))
                    || "F".equals(returnData.get("resp_code"))) {
                Log.write("协议支付请求成功");
                OrderRepay orderRepay = new OrderRepay();
                orderRepay.setRepayNo(orderSeriesId);
                orderRepay.setUid(uid);
                orderRepay.setOrderId(order.getId());
                orderRepay.setRepayType(2);
                BigDecimal repayMoney = new BigDecimal(amount).divide(new BigDecimal(100));
                orderRepay.setRepayMoney(repayMoney);
                orderRepay.setBank(userBank.getCardName());
                orderRepay.setBankNo(userBank.getCardNo());
                orderRepay.setCreateTime(new Date());
                orderRepay.setUpdateTime(new Date());
                orderRepay.setRepayStatus(0);
                orderRepayService.insertSelective(orderRepay);

                OrderRepayQueryMessage message = new OrderRepayQueryMessage();
                message.setMerchantAlias(RequestThread.getClientAlias());
                message.setRepayNo(orderSeriesId);
                message.setTimes(1);
                message.setRepayType(1);
                rabbitTemplate.convertAndSend(RabbitConst.baofoo_queue_repay_order_query, message);

                JSONObject object = new JSONObject();
                object.put("repayOrderNo", orderSeriesId);
                object.put("shouldRepayAmount", order.getShouldRepay().toPlainString());
                return new ResultMessage(ResponseEnum.M2000, object);
            } else {
                //异常不得做为订单状态。
                log.error("用户主动还款，调用宝付协议支付异常，还款订单号={}, rseponse", orderSeriesId, postString);
                return new ResultMessage(ResponseEnum.M4000.getCode(), "还款异常");
            }

        } catch (Exception e) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "还款异常");
        }
    }
}
