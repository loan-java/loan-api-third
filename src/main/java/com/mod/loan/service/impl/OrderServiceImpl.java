package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.*;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.common.message.RiskAuditMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.*;
import com.mod.loan.model.*;
import com.mod.loan.service.*;
import com.mod.loan.util.ConstantUtils;
import com.mod.loan.util.MoneyUtil;
import com.mod.loan.util.rongze.BizDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


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
    private OrderUserMapper orderUserMapper;
    @Resource
    private MerchantService merchantService;
    @Resource
    private RedisMapper redisMapper;
    @Resource
    private UserIdentService userIdentService;
    @Resource
    private BlacklistService blacklistService;
    @Resource
    private KuaiQianService kuaiQianService;
    @Resource
    private BaofooService baofooService;
    @Resource
    private ChanpayService chanpayService;
    @Resource
    private TbDecisionResDetailMapper decisionResDetailMapper;
    @Resource
    private DecisionPbDetailMapper decisionPbDetailMapper;
    @Autowired
    private DecisionZmDetailMapper decisionZmDetailMapper;
    @Autowired
    private MerchantRateMapper merchantRateMapper;

    @Resource
    private YeePayService yeePayService;

    @Resource
    private RabbitTemplate rabbitTemplate;


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Order repayOrder(String orderNo, int source) throws BizException {

        Order order = findOrderByOrderNoAndSource(orderNo, source);
        if (order == null) throw new BizException("订单不存在");

        ResultMessage message;
        Merchant merchant = merchantService.findMerchantByAlias(Constant.merchant);
        log.info("还款Merchant(" + Constant.merchant + "): " + JSONObject.toJSONString(merchant));

        OrderRepay orderRepay = orderRepayService.selectByOrderId(order.getId());
        if (orderRepay != null) {
            if (orderRepay.getRepayStatus() == 0 && orderRepay.getRepayType() == 7) {
                throw new BizException("系统正在自动扣款，请勿重复提交");
            } else if (orderRepay.getRepayStatus() == 0) {
                throw new BizException("订单正在自动扣款，请勿重复提交");
            } else if (orderRepay.getRepayStatus() == 3 && orderRepay.getRepayType() == 7) {
                throw new BizException("已经还款了，请刷新页面");
            }
        }

        if (merchant != null) {
            switch (merchant.getBindType()) {
                case 4:
                    message = baofooService.repay(order);
                    break;
                case 5:
                    message = kuaiQianService.repay(order);
                    break;
                case 6:
                    message = chanpayService.repay(order);
                    break;
                case 7:
                    message = yeePayService.repay(order);
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
        Integer riskType = merchant.getRiskType();
        if (riskType == null) {
            riskType = 2;
        }

        //是否存在关联的借贷信息===============================================================
        Long productId = orderUserMapper.getMerchantRateByOrderNoAndSource(orderNo, Integer.parseInt(UserOriginEnum.RZ.getCode()));
        if (productId == null) {
            throw new BizException("推送用户确认收款信息:商户不存在默认借贷信息");
        }
        MerchantRate merchantRate = merchantRateMapper.selectByPrimaryKey(productId);
        if (merchantRate == null) {
            throw new BizException("推送用户确认收款信息:商户不存在默认借贷信息");
        }
        BigDecimal approvalAmount = merchantRate.getProductMoney(); //审批金额
        if (approvalAmount == null) {
            throw new BizException("推送用户确认收款信息:商户不存在默认借贷金额");
        }
        Integer approvalTerm = merchantRate.getProductDay(); //审批期限
        if (approvalAmount == null) {
            throw new BizException("推送用户确认收款信息:商户不存在默认借贷期限");
        }
        if (loanTerm != approvalTerm.intValue()) {
            throw new BizException("推送用户确认收款信息:商户实际借贷期限：" + approvalTerm.intValue() + ",现在借款期限：" + loanTerm);
        }
        //借款金额
        BigDecimal borrowMoney = new BigDecimal(loanAmount);
        if (borrowMoney.intValue() != approvalAmount.intValue()) {
            throw new BizException("推送用户确认收款信息:商户实际借贷金额：" + approvalAmount.intValue() + ",现在借款金额：" + borrowMoney.intValue());
        }
        //===================================================================================

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

        //进入风控模块
        switch (riskType) {
            case 1:
                TbDecisionResDetail resDetail = decisionResDetailMapper.selectByOrderNo(orderNo);
                if (resDetail != null && PolicyResultEnum.AGREE.getCode().equals(resDetail.getCode())) {
                    order.setStatus(ConstantUtils.unsettledOrderStatus);
                    orderMapper.updateByPrimaryKey(order);
                } else if (resDetail != null && PolicyResultEnum.REJECT.getCode().equals(resDetail.getCode())) {
                    order.setStatus(ConstantUtils.rejectOrderStatus);
                    orderMapper.updateByPrimaryKey(order);
                } else {
                    if (resDetail == null) {
                        // 通知风控
                        RiskAuditMessage message = new RiskAuditMessage();
                        message.setOrderId(order.getId());
                        message.setOrderNo(orderNo);
                        message.setStatus(1);
                        message.setMerchant(RequestThread.getClientAlias());
                        message.setUid(uid);
                        message.setSource(RiskAuditSourceEnum.RONG_ZE.getCode());
                        message.setTimes(0);
                        try {
                            log.info("===============开始进入风控队列qjld====================" + orderNo);
                            rabbitTemplate.convertAndSend(RabbitConst.qjld_queue_risk_order_notify, message);
                        } catch (Exception e) {
                            log.error("消息发送异常：", e);
                        }
                    }
                }
                break;
            case 2:
                DecisionPbDetail pbDetail = decisionPbDetailMapper.selectByOrderNo(orderNo);
                if (pbDetail != null && PbResultEnum.APPROVE.getCode().equals(pbDetail.getResult())) {
                    order.setStatus(ConstantUtils.unsettledOrderStatus);
                    orderMapper.updateByPrimaryKey(order);
                } else if (pbDetail != null && PbResultEnum.MANUAL.getCode().equals(pbDetail.getResult())) {
                    order.setStatus(ConstantUtils.rejectOrderStatus);
                    orderMapper.updateByPrimaryKey(order);
                } else if (pbDetail != null && PbResultEnum.DENY.getCode().equals(pbDetail.getResult())) {
                    order.setStatus(ConstantUtils.rejectOrderStatus);
                    orderMapper.updateByPrimaryKey(order);
                } else {
                    if (pbDetail == null) {
                        // 通知风控
                        RiskAuditMessage message = new RiskAuditMessage();
                        message.setOrderId(order.getId());
                        message.setOrderNo(orderNo);
                        message.setStatus(1);
                        message.setMerchant(RequestThread.getClientAlias());
                        message.setUid(uid);
                        message.setSource(RiskAuditSourceEnum.RONG_ZE.getCode());
                        message.setTimes(0);
                        try {
                            log.info("===============开始进入风控队列pb====================" + orderNo);
                            rabbitTemplate.convertAndSend(RabbitConst.pb_queue_risk_order_notify, message);
                        } catch (Exception e) {
                            log.error("消息发送异常：", e);
                        }
                    }
                }
                break;
            case 3:
                DecisionZmDetail zmDetail = decisionZmDetailMapper.selectByOrderNo(orderNo);
                if (zmDetail != null && "0".equals(zmDetail.getReturnCode())) {
                    order.setStatus(ConstantUtils.unsettledOrderStatus);
                    orderMapper.updateByPrimaryKey(order);
                } else if (zmDetail != null && !"0".equals(zmDetail.getReturnCode())) {
                    order.setStatus(ConstantUtils.rejectOrderStatus);
                    orderMapper.updateByPrimaryKey(order);
                } else {
                    if (zmDetail == null) {
                        // 通知风控
                        RiskAuditMessage message = new RiskAuditMessage();
                        message.setOrderId(order.getId());
                        message.setOrderNo(orderNo);
                        message.setStatus(1);
                        message.setMerchant(RequestThread.getClientAlias());
                        message.setUid(uid);
                        message.setSource(RiskAuditSourceEnum.RONG_ZE.getCode());
                        message.setTimes(0);
                        try {
                            log.info("===============开始进入风控队列zm====================" + orderNo);
                            rabbitTemplate.convertAndSend(RabbitConst.zm_queue_risk_order_notify, message);
                        } catch (Exception e) {
                            log.error("消息发送异常：", e);
                        }
                    }
                }
                break;
            default:
                throw new BizException("不存在当前的风控类型");
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
