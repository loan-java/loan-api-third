package com.mod.loan.service.impl.rongze;


import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.OrderStatusEnum;
import com.mod.loan.common.enums.PolicyResultEnum;
import com.mod.loan.common.enums.RiskAuditSourceEnum;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.message.RiskAuditMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.config.Constant;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;
import com.mod.loan.model.UserIdent;
import com.mod.loan.model.dto.DecisionResDetailDTO;
import com.mod.loan.service.QjldPolicyService;
import com.mod.loan.service.UserBankService;
import com.mod.loan.service.UserIdentService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.DateUtil;
import com.mod.loan.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
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
    private QjldPolicyService qjldPolicyService;

    @Resource
    private UserBankService userBankService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    //查询审批结论
    public ResponseBean<Map<String, Object>> queryAuditResult(JSONObject param) throws BizException {

        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));
        log.info("===============查询审批结论开始====================" + bizData.toJSONString());

        String orderNo = bizData.getString("order_no");

        long uid = RequestThread.get().getUid();

        User user = userService.selectByPrimaryKey(uid);
        if (user == null || user.getUserOrigin().equals(UserOriginEnum.JH.getCode())) {
            throw new BizException("查询审批结论:用户不存在/用户非融泽用户,订单号=" + orderNo);
        }

        UserIdent userIdent = userIdentService.selectByPrimaryKey(uid);
        if (userIdent == null) {
            throw new BizException("查询审批结论:用户不存在,订单号=" + orderNo);
        }

        UserBank userBank = userBankService.selectUserCurrentBankCard(uid);
        if (userBank == null) userBank = new UserBank();

        String serials_no = String.format("%s%s%s", "p", new DateTime().toString(TimeUtils.dateformat5), user.getId());
        DecisionResDetailDTO pd = qjldPolicyService.qjldPolicyNoSync(serials_no, user, userBank);

        String reapply = null; //是否可再次申请
        String reapplyTime = null; //可再申请的时间
        String remark = "审批中"; //拒绝原因

        Long refuseTime = null; //审批拒绝时间
        Long approvalTime = null; //审批通过时间
        int conclusion = 30; //处理中
        Integer proType = null; //单期产品
        Integer amountType = null; //审批金额是否固定，0 - 固定
        Integer termType = null; //审批期限是否固定，0 - 固定
        Integer approvalAmount = null; //审批金额
        Integer approvalTerm = null; //审批期限
        Integer termUnit = null; //期限单位，1 - 天
        String creditDeadline = null; //审批结果有效期，当前时间

        if (pd != null) {
            DecisionResDetailDTO decisionResDetailDTO = qjldPolicyService.qjldPolicQuery(pd.getTrans_id());
            if (decisionResDetailDTO == null || OrderStatusEnum.INIT.getCode().equals(decisionResDetailDTO.getOrderStatus()) || OrderStatusEnum.WAIT.getCode().equals(decisionResDetailDTO.getOrderStatus())) {
                //处理中
            }
            if (decisionResDetailDTO != null) {
                String riskCode = decisionResDetailDTO.getCode();
                if (PolicyResultEnum.isAgree(riskCode)) {
                    //通过
                    conclusion = 10;
                    approvalTime = System.currentTimeMillis();
                    creditDeadline = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
                    proType = 1; //单期产品
                    amountType = 0; //审批金额是否固定，0 - 固定
                    termType = 0; //审批期限是否固定，0 - 固定
                    approvalAmount = 1500; //审批金额
                    approvalTerm = 6; //审批期限
                    termUnit = 1; //期限单位，1 - 天
                    remark = "通过";
                } else {
                    //拒绝
                    refuseTime = System.currentTimeMillis();
                    conclusion = 40;
                    remark = StringUtils.isNotBlank(decisionResDetailDTO.getDesc()) ? decisionResDetailDTO.getDesc() : "拒绝";
                    reapply = "1";
                    reapplyTime = DateFormatUtils.format(refuseTime + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("order_no", orderNo);
        map.put("conclusion", conclusion);
        map.put("reapply", reapply);
        map.put("reapplytime", reapplyTime);
        map.put("remark", remark);
        map.put("refuse_time", refuseTime);
        map.put("approval_time", approvalTime);
        map.put("pro_type", proType);
        map.put("term_unit", termUnit);
        map.put("amount_type", amountType);
        map.put("term_type", termType);
        map.put("approval_term", approvalTerm);
        map.put("credit_deadline", creditDeadline);
        map.put("approval_amount", approvalAmount);

        log.info("===============查询审批结论结束====================");

        return ResponseBean.success(map);
    }

    public ResponseBean<Map<String, Object>> auditResult(JSONObject param) throws Exception {
        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));
        log.info("===============查询审批结论开始====================" + bizData.toJSONString());

        String orderNo = bizData.getString("order_no");
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

        // 通知风控
        RiskAuditMessage message = new RiskAuditMessage();
        message.setOrderNo(orderNo);
        message.setStatus(1);
        message.setMerchant(RequestThread.getClientAlias());
        message.setUid(user.getId());
        message.setSource(RiskAuditSourceEnum.RONG_ZE.getCode());
        message.setTimes(0);
        try {
            rabbitTemplate.convertAndSend(RabbitConst.queue_risk_order_notify, message);
        } catch (Exception e) {
            log.error("消息发送异常：", e);
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
        //10=审批通过 40=审批拒绝30=审批处理中
        int conclusion = 30;
        //是否可再申请 1-是，0-不可以
        String reapply = "1";
        Timestamp approvalTime = new Timestamp(System.currentTimeMillis());
        //可再申请的时间，yyyy- MM-dd，比如（2020-10- 10）
        String reapplyTime = DateFormatUtils.format(System.currentTimeMillis() + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
        String remark = "审批处理中";
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
