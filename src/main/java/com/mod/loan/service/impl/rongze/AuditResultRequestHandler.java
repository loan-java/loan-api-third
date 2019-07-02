package com.mod.loan.service.impl.rongze;


import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.PbResultEnum;
import com.mod.loan.common.enums.PolicyResultEnum;
import com.mod.loan.common.enums.RiskAuditSourceEnum;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.message.RiskAuditMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.mapper.*;
import com.mod.loan.model.*;
import com.mod.loan.service.MerchantRateService;
import com.mod.loan.service.MerchantService;
import com.mod.loan.service.UserIdentService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询审批结论
 *
 * @author yutian
 */
@Slf4j
@Component
public class AuditResultRequestHandler {

    @Resource
    private UserIdentService userIdentService;
    @Resource
    private UserService userService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private TbDecisionResDetailMapper decisionResDetailMapper;
    @Autowired
    private DecisionPbDetailMapper decisionPbDetailMapper;
    @Autowired
    private DecisionZmDetailMapper decisionZmDetailMapper;
    @Resource
    private MerchantService merchantService;
    @Resource
    private MerchantRateService merchantRateService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderUserMapper orderUserMapper;


    public ResponseBean<Map<String, Object>> auditResult(JSONObject param) throws Exception {
        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));

        String orderNo = bizData.getString("order_no");
        log.info("===============查询审批结论开始===================="+ orderNo);

        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        if (merchant == null) {
            throw new BizException("商户【" + RequestThread.getClientAlias() + "】不存在，未配置");
        }
        Integer riskType = merchant.getRiskType();
        if (riskType == null) {
            riskType = 2;
        }

        User user = userService.selectByPrimaryKey(RequestThread.getUid());
        if (user == null || user.getUserOrigin().equals(UserOriginEnum.JH.getCode())) {
            throw new BizException("查询审批结论:用户不存在/用户非融泽用户,订单号=" + orderNo);
        }

        UserIdent userIdent = userIdentService.selectByPrimaryKey(user.getId());
        if (2 != userIdent.getRealName() || 2 != userIdent.getUserDetails()
                || 2 != userIdent.getMobile() || 2 != userIdent.getLiveness()) {
            // 提示认证未完成
            log.error("用户认证未完成：" + JSONObject.toJSONString(userIdent));
            throw new BizException("用户认证未完成");
        }

        //10=审批通过 40=审批拒绝30=审批处理中
        int conclusion = 40;
        String remark = "审批拒绝";

        //是否存在关联的借贷信息
        Long  merchantRateId = orderUserMapper.getMerchantRateByOrderNoAndSource(orderNo, Integer.parseInt(UserOriginEnum.RZ.getCode()));
        if(merchantRateId == null){
            throw new BizException("查询审批结论:商户不存在默认借贷信息");
        }
        MerchantRate merchantRate = merchantRateService.selectByPrimaryKey(merchantRateId);
        if(merchantRate == null){
            throw new BizException("查询审批结论:商户不存在默认借贷信息");
        }

        //不丢失复贷用户 复贷用户前四次不需要走风控
        List<Order> orderList = orderMapper.getDoubleLoanByUid(user.getId());
        if (orderList != null && orderList.size() > 0 && orderList.size() < 5) {
            conclusion = 10;
            remark = "审批成功";
            //单期产品
            int proType = 1;
            //审批金额是否固定，0 - 固定
            int amountType = 0;
            //审批期限是否固定，0 - 固定
            int termType = 0;
            BigDecimal approvalAmount = merchantRate.getProductMoney(); //审批金额
            if(approvalAmount == null) {
                throw new BizException("查询审批结论:商户不存在默认借贷金额");
            }
            Integer approvalTerm = merchantRate.getProductDay(); //审批期限
            if(approvalAmount == null) {
                throw new BizException("查询审批结论:商户不存在默认借贷期限");
            }
            //期限单位，1 - 天
            int termUnit = 1;
            //是否可再申请 1-是，0-不可以
            String reapply = "1";
            Timestamp approvalTime = new Timestamp(System.currentTimeMillis());
            //可再申请的时间，yyyy- MM-dd，比如（2020-10- 10）
            String reapplyTime = DateFormatUtils.format(System.currentTimeMillis() + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
            //审批结果有效期，往后30天
            String creditDeadline = DateUtil.getNextDay(DateUtil.getStringDateShort(), "30");

            Map<String, Object> map = new HashMap<>();
            map.put("reapplytime", reapplyTime);
            map.put("pro_type", proType);
            map.put("term_unit", termUnit);
            map.put("amount_type", amountType);
            map.put("term_type", termType);
            map.put("approval_amount", approvalAmount.intValue());
            map.put("approval_term", approvalTerm.intValue());
            map.put("credit_deadline", creditDeadline);
            map.put("refuse_time", approvalTime);
            map.put("remark", remark);
            map.put("reapply", reapply);
            map.put("order_no", orderNo);
            map.put("conclusion", conclusion);
            log.info("===============查询审批结论结束===================="+ orderNo);
            return ResponseBean.success(map);
        }

        switch (riskType) {
            case 1:
                TbDecisionResDetail resDetail = decisionResDetailMapper.selectByOrderNo(orderNo);
                if (resDetail != null && PolicyResultEnum.AGREE.getCode().equals(resDetail.getCode())) {
                    conclusion = 10;
                    remark = "审批成功";
                } else if (resDetail != null && PolicyResultEnum.REJECT.getCode().equals(resDetail.getCode())) {
                    conclusion = 40;
                    remark = "审批失败";
                } else {
                    if (resDetail == null) {
                        // 通知风控
                        RiskAuditMessage message = new RiskAuditMessage();
                        message.setOrderNo(orderNo);
                        message.setStatus(1);
                        message.setMerchant(RequestThread.getClientAlias());
                        message.setUid(user.getId());
                        message.setSource(RiskAuditSourceEnum.RONG_ZE.getCode());
                        message.setTimes(0);
                        try {
                            log.info("===============开始进入风控队列qjld====================" + orderNo);
                            rabbitTemplate.convertAndSend(RabbitConst.qjld_queue_risk_order_notify, message);
                        } catch (Exception e) {
                            log.error("消息发送异常：", e);
                        }
                    }
                    conclusion = 30;
                    remark = "审批处理中";
                }
                break;
            case 2:
                DecisionPbDetail pbDetail = decisionPbDetailMapper.selectByOrderNo(orderNo);
                if (pbDetail != null && PbResultEnum.APPROVE.getCode().equals(pbDetail.getResult())) {
                    conclusion = 10;
                    remark = "审批成功";
                } else if (pbDetail != null && PbResultEnum.MANUAL.getCode().equals(pbDetail.getResult())) {
                    conclusion = 40;
                    remark = "审批失败";
                } else if (pbDetail != null && PbResultEnum.DENY.getCode().equals(pbDetail.getResult())) {
                    conclusion = 40;
                    remark = "审批拒绝";
                } else {
                    if (pbDetail == null) {
                        // 通知风控
                        RiskAuditMessage message = new RiskAuditMessage();
                        message.setOrderNo(orderNo);
                        message.setStatus(1);
                        message.setMerchant(RequestThread.getClientAlias());
                        message.setUid(user.getId());
                        message.setSource(RiskAuditSourceEnum.RONG_ZE.getCode());
                        message.setTimes(0);
                        try {
                            log.info("===============开始进入风控队列pb====================" + orderNo);
                            rabbitTemplate.convertAndSend(RabbitConst.pb_queue_risk_order_notify, message);
                        } catch (Exception e) {
                            log.error("消息发送异常：", e);
                        }
                    }
                    conclusion = 30;
                    remark = "审批处理中";
                }
                break;
            case 3:
                DecisionZmDetail zmDetail = decisionZmDetailMapper.selectByOrderNo(orderNo);
                if (zmDetail != null && "0".equals(zmDetail.getReturnCode())) {
                    conclusion = 10;
                    remark = "审批成功";
                } else if (zmDetail != null && !"0".equals(zmDetail.getReturnCode())) {
                    conclusion = 40;
                    remark = "审批拒绝";
                } else {
                    if (zmDetail == null) {
                        // 通知风控
                        RiskAuditMessage message = new RiskAuditMessage();
                        message.setOrderNo(orderNo);
                        message.setStatus(1);
                        message.setMerchant(RequestThread.getClientAlias());
                        message.setUid(user.getId());
                        message.setSource(RiskAuditSourceEnum.RONG_ZE.getCode());
                        message.setTimes(0);
                        try {
                            log.info("===============开始进入风控队列zm====================" + orderNo);
                            rabbitTemplate.convertAndSend(RabbitConst.zm_queue_risk_order_notify, message);
                            conclusion = 30;
                            remark = "审批处理中";
                        } catch (Exception e) {
                            log.error("消息发送异常：", e);
                        }
                    }
                }
                break;
            default:
                throw new BizException("不存在当前的风控类型");
        }


        //单期产品
        int proType = 1;
        //审批金额是否固定，0 - 固定
        int amountType = 0;
        //审批期限是否固定，0 - 固定
        int termType = 0;
        //审批金额
        int approvalAmount = 1500;
        //审批期限
        int approvalTerm = 6;
        //期限单位，1 - 天
        int termUnit = 1;
        //是否可再申请 1-是，0-不可以
        String reapply = "1";
        Timestamp approvalTime = new Timestamp(System.currentTimeMillis());
        //可再申请的时间，yyyy- MM-dd，比如（2020-10- 10）
        String reapplyTime = DateFormatUtils.format(System.currentTimeMillis() + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
        //审批结果有效期，往后30天
        String creditDeadline = DateUtil.getNextDay(DateUtil.getStringDateShort(), "30");

        Map<String, Object> map = new HashMap<>();
        map.put("reapplytime", reapplyTime);
        map.put("pro_type", proType);
        map.put("term_unit", termUnit);
        map.put("amount_type", amountType);
        map.put("term_type", termType);
        map.put("approval_amount", approvalAmount);
        map.put("approval_term", approvalTerm);
        map.put("credit_deadline", creditDeadline);
        map.put("refuse_time", approvalTime);
        map.put("remark", remark);
        map.put("reapply", reapply);
        map.put("order_no", orderNo);
        map.put("conclusion", conclusion);
        log.info("===============查询审批结论结束====================");
        return ResponseBean.success(map);
    }
}
