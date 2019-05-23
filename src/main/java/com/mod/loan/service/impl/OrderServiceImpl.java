package com.mod.loan.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.message.RiskAuditMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.model.*;
import com.mod.loan.service.*;
import com.mod.loan.util.MoneyUtil;
import com.mod.loan.util.rongze.BizDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private OrderRepayService orderRepayService;
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
    private BaofooService baofooService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Order repayOrder(String orderNo, int source) throws BizException {

        Order order = findOrderByOrderNoAndSource(orderNo, source);
        if (order == null) throw new BizException("订单不存在");

        ResultMessage message;
        Merchant merchant = merchantService.findMerchantByAlias(Constant.merchant);
        log.info("还款Merchant(" + Constant.merchant + "): " + JSONObject.toJSONString(merchant));

        OrderRepay orderRepay = orderRepayService.selectByOrderId(order.getId());
        if (orderRepay.getRepayStatus() == 0 && orderRepay.getRepayType() == 7) {
            throw new BizException("系统正在自动扣款，请勿重复提交");
        } else if (orderRepay.getRepayStatus() == 0) {
            throw new BizException("订单正在自动扣款，请勿重复提交");
        } else if (orderRepay.getRepayStatus() == 3 && orderRepay.getRepayType() == 7) {
            throw new BizException("系统自动扣款成功，请勿重复提交");
        }

        if (merchant != null) {
            switch (merchant.getBindType()) {
                case 4:
                    message = baofooService.repay(order);
                    break;
                case 5:
                    message = kuaiQianService.repay(order);
                    break;
                default:
                    throw new BizException("支付渠道异常");
            }
        } else {
            message = kuaiQianService.repay(order);
        }
        log.info("还款结果message=" + JSONObject.toJSONString(message));
        if (ResponseEnum.M2000.getCode().equals(message.getStatus())) {
            JSONObject res = JSONObject.parseObject(JSON.toJSONString(message.getData()));
            order.setRepayOrderNo(res.getString("repayOrderNo"));
            return order;
        }
        throw new BizException(message.getStatus(), message.getMessage());
    }

    @Override
    public Order findOrderByOrderNoAndSource(String orderNo, int source) {
        return orderMapper.findByOrderNoAndSource(orderNo, source);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Order submitOrder(String orderNo, String loanAmount, int loanTerm, int source) throws BizException {
        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        if (merchant == null) {
            throw new BizException("商户【" + RequestThread.getClientAlias() + "】不存在，未配置");
        }

        MerchantRate merchantRate = merchantRateService.findByMerchant(RequestThread.getClientAlias());
        if (null == merchantRate) {
            throw new BizException("未查到规则");
        }
        Long productId = merchantRate.getId();

        Order order = orderMapper.findByOrderNoAndSource(orderNo, source);
        boolean orderExist = order != null;

        Long uid = orderExist ? order.getUid() : RequestThread.getUid();
        if (!redisMapper.lock(RedisConst.lock_user_order + uid, 5)) {
            throw new BizException("操作过于频繁");
        }
        UserIdent userIdent = userIdentService.selectByPrimaryKey(uid);
        if (2 != userIdent.getRealName() || 2 != userIdent.getUserDetails() || 2 != userIdent.getBindbank()
                || 2 != userIdent.getMobile() || 2 != userIdent.getLiveness()) {
            // 提示认证未完成
            log.error("用户认证未完成：" + JSONObject.toJSONString(userIdent));
            throw new BizException("用户认证未完成");
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

        if (!orderExist) order = new Order();

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
        order.setSource(source);
        addOrUpdateOrder(order, orderPhone);

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
        return orderMapper.findUserLatestOrder(uid);
    }

    @Override
    public List<Order> getByUid(Long uid) {
        return orderMapper.getByUid(uid);
    }

    @Override
    public int addOrder(Order order, OrderPhone orderPhone) {
        orderMapper.insertSelective(order);
        orderPhone.setOrderId(order.getId());
        return orderPhoneMapper.insertSelective(orderPhone);
    }

    @Override
    public void addOrUpdateOrder(Order order, OrderPhone orderPhone) {
        if (order.getId() != null && order.getId() > 0) {
            orderMapper.updateByPrimaryKeySelective(order);
            orderPhone.setOrderId(order.getId());
            orderPhoneMapper.updateByPrimaryKeySelective(orderPhone);
            return;
        }
        addOrder(order, orderPhone);
    }

    @Override
    public OrderPhone findOrderPhoneByOrderId(Long orderId) {
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

}
