package com.mod.loan.controller.juhe.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.OrderSourceEnum;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.enums.RiskAuditSourceEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.message.RiskAuditMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.controller.check.LoginCheck;
import com.mod.loan.mapper.MerchantRateMapper;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.model.*;
import com.mod.loan.service.*;
import com.mod.loan.util.MoneyUtil;
import com.mod.loan.util.StringUtil;
import com.mod.loan.util.TimeUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单申请详细展示，及下单确认 回收和贷款文案通用
 *
 * @author yhx 2018年4月26日 下午13:42:19
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/order")
public class OrderApplyController {

    private static Logger logger = LoggerFactory.getLogger(OrderApplyController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserBankService userBankService;

    @Autowired
    private MerchantRateService merchantRateService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisMapper redisMapper;

    @Autowired
    private OrderRepayService orderRepayService;
    @Autowired
    private LoginCheck loginCheck;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserIdentService userIdentService;

    @Autowired
    private MerchantRateMapper merchantRateMapper;

    @Autowired
    private KuaiQianService kuaiQianService;

    @Resource
    private BaofooService baofooService;

    @Autowired
    private MerchantService merchantService;

    /**
     * 借款确认 提交订单(提交-风控订单审核通知)
     */
    @LoginRequired()
    @RequestMapping(value = "/orderSubmit")
    @Api
    public JSONObject orderSubmit(HttpServletRequest request, @RequestBody JSONObject param) {
        logger.info("=====申请借款=====");
        logger.info("请求参数：" + JSON.toJSONString(param));

        ResultMessage m = loginCheck.check(request, param, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }

        String loanAmount = param.getString("loanAmount");
        String loanTerm = param.getString("loanTerm");
        String cardId = param.getString("cardId");

        //判断商户是否配置好
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        if (merchant == null) {
            logger.info("商户【" + RequestThread.getClientAlias() + "】不存在，未配置");
            logger.info("==========================================");
            return ResultMap.fail(ResponseEnum.M5000.getCode(), "商户不存在");
        }

        MerchantRate record = merchantRateMapper.findByMerchant(RequestThread.getClientAlias());
        Long productId = record.getId();

        Long uid = RequestThread.getUid();
        if (!redisMapper.lock(RedisConst.lock_user_order + uid, 5)) {
            return ResultMap.fail(ResponseEnum.M4005.getCode(), "操作过于频繁");
        }
        UserIdent userIdent = userIdentService.selectByPrimaryKey(uid);
        if (2 != userIdent.getRealName() || 2 != userIdent.getUserDetails() || 2 != userIdent.getBindbank()
                || 2 != userIdent.getMobile() || 2 != userIdent.getLiveness()) {
            // 提示认证未完成
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "认证未完成");
        }
        Blacklist blacklist = blacklistService.getByUid(uid);
        if (null != blacklist) {
            // 校验灰名单锁定天数
            if (1 == blacklist.getType()) {
                DateTime d1 = new DateTime(new Date());
                DateTime d2 = new DateTime(blacklist.getInvalidTime());
                Integer remainDays = Days.daysBetween(d1, d2).getDays() + 1;
                return ResultMap.fail(ResponseEnum.M4000.getCode(), "暂时无法下单，请于" + remainDays + "天后再尝试");
            }
            // 黑名单
            if (2 == blacklist.getType()) {
                return ResultMap.fail(ResponseEnum.M4000.getCode(), "您不符合下单条件");
            }
        }
        // 是否有正在借款中的订单
        Order orderIng = orderService.findUserLatestOrder(uid);
        if (null != orderIng) {
            if (orderIng.getStatus() < 40) {
                return ResultMap.fail(ResponseEnum.M4000.getCode(), "订单进行中，无法提单");
            }
            // 审核拒绝的订单7天内无法再下单
            if (orderIng.getStatus() == 51 || orderIng.getStatus() == 52) {
                DateTime applyTime = new DateTime(orderIng.getCreateTime()).plusDays(7);
                DateTime nowTime = new DateTime();
                Integer remainDays = Days.daysBetween(nowTime.withMillisOfDay(0), applyTime.withMillisOfDay(0))
                        .getDays();
                if (0 < remainDays && remainDays <= 7) {
                    return ResultMap.fail(ResponseEnum.M4000.getCode(), "请" + remainDays + "天后重试提单");
                }
            }
        }
        Order order = new Order();
        MerchantRate merchantRate = merchantRateService.selectByPrimaryKey(productId);
        if (null == merchantRate) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "未查到规则");
        }
        if (merchantRate.getProductMoney().compareTo(new BigDecimal(loanAmount)) != 0
                || merchantRate.getProductDay() != Integer.parseInt(loanTerm)) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "规则不匹配");
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
        Integer userType = orderService.judgeUserTypeByUid(uid);

        order.setOrderNo(StringUtil.getOrderNumber("b"));
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
        orderService.addOrder(order, orderPhone);

        // 通知风控
        RiskAuditMessage message = new RiskAuditMessage();
        message.setOrderId(order.getId());
        message.setOrderNo(order.getOrderNo());
        message.setStatus(1);
        message.setMerchant(RequestThread.getClientAlias());
        message.setUid(uid);
        message.setSource(RiskAuditSourceEnum.JU_HE.getCode());
        message.setTimes(0);
        try {
            rabbitTemplate.convertAndSend(RabbitConst.qjld_queue_risk_order_notify, message);
        } catch (Exception e) {
            logger.error("消息发送异常：", e);
        }

        Map<String, Object> map = new HashMap<>(5);
        map.put("orderNo", order.getOrderNo());
        map.put("loanFee", totalFee.toPlainString());
        map.put("interest", interestFee.toPlainString());
        map.put("shouldRepayAmount", shouldRepay.toPlainString());
        map.put("orderExpireTime", TimeUtils.addDate(merchantRate.getProductDay() - 1).getTime() / 1000);
        map.put("auditType", "MR");
        map.put("auditStatus", "AUDITING");

        JSONObject object = ResultMap.success();
        object.put("data", map);

        return object;
    }

    /**
     * 还款
     */
    @LoginRequired()
    @RequestMapping(value = "/repayment")
    @Api
    public JSONObject repayment(HttpServletRequest request, @RequestBody JSONObject param) throws BizException {
        logger.info("=====还款=====");
        logger.info("请求参数：" + JSON.toJSONString(param));
        logger.info("===============================");
        ResultMessage m = loginCheck.check(request, param, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }

        String orderNo = param.getString("orderNo");
        Order order = orderService.findOrderByOrderNoAndSource(orderNo, OrderSourceEnum.JUHE.getSoruce());
        ResultMessage message = orderService.repayOrder(orderNo, OrderSourceEnum.JUHE.getSoruce());
        logger.info("还款Constant.merchant参数：" + Constant.merchant);
        Merchant merchant = merchantService.findMerchantByAlias(Constant.merchant);
        if (merchant != null) {
            logger.info("还款Merchant参数：" + merchant.toString());
            logger.info("===============================");
            switch (merchant.getBindType()) {
                case 4:
                    message = baofooService.repay(order);
                    break;
                case 5:
                    message = kuaiQianService.repay(order);
                    break;
                default:
                    return ResultMap.fail(ResponseEnum.M4000.getCode(), "支付渠道异常");
            }
        } else {
            message = kuaiQianService.repay(order);
        }
        logger.info("还款ResultMessage参数：" + (message != null ? message.toString() : null));
        logger.info("===============================");
        if (ResponseEnum.M2000.getCode().equals(message.getStatus())) {
            Map<String, Object> map = new HashMap<>(1);
            JSONObject res = JSONObject.parseObject(JSON.toJSONString(message.getData()));
            map.put("repayOrderNo", res.getString("repayOrderNo"));
            map.put("shouldRepayAmount", res.getString("shouldRepayAmount"));

            JSONObject object = ResultMap.success();
            object.put("data", map);
            logger.info("=====还款成功=====");
            return object;
        }
        logger.info("=====还款失败=====");
        return ResultMap.fail(message.getStatus(), message.getMsg());
    }

}
