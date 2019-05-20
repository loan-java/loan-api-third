package com.mod.loan.service.impl.rongze;


import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.OrderStatusEnum;
import com.mod.loan.common.enums.PolicyResultEnum;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询审批结论
 */
@Slf4j
@Component
public class AuditResultRequestHandler {


    @Autowired
    private UserIdentService userIdentService;

    @Autowired
    private UserService userService;
    @Resource
    private QjldPolicyService qjldPolicyService;
    @Resource
    private UserBankService userBankService;

    //查询审批结论
    public ResponseBean<Map<String, Object>> queryAuditResult(JSONObject param) throws Exception {

        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));
        String orderNo = bizData.getString("order_no");

        long uid = RequestThread.get().getUid();
        UserBank userBank = userBankService.selectUserCurrentBankCard(uid);
        if (userBank == null) userBank = new UserBank();

        User user = userService.selectByPrimaryKey(uid);
        String serials_no = String.format("%s%s%s", "p", new DateTime().toString(TimeUtils.dateformat5),
                user.getId());
        DecisionResDetailDTO pd = qjldPolicyService.qjldPolicyNoSync(serials_no, user, userBank);

        String reapply = ""; //是否可再次申请
        String reapplyTime = ""; //可再申请的时间
        String remark = ""; //拒绝原因

        Long refuseTime = null; //审批拒绝时间
        Long approvalTime = null; //审批通过时间
        int conclusion = 30; //处理中
        int proType = 1; //单期产品
        int amountType = 0; //审批金额是否固定，0 - 固定
        int termType = 0; //审批期限是否固定，0 - 固定
        int approvalAmount = 1500; //审批金额
        int approvalTerm = 6; //审批期限
        int termUnit = 1; //期限单位，1 - 天
        String creditDeadline = DateFormatUtils.format(new Date(), "yyyy-MM-dd"); //审批结果有效期，当前时间

//        if (pd != null) {
//            DecisionResDetailDTO decisionResDetailDTO = qjldPolicyService.qjldPolicQuery(pd.getTrans_id());
//            if (decisionResDetailDTO == null || OrderStatusEnum.INIT.getCode().equals(decisionResDetailDTO.getOrderStatus()) || OrderStatusEnum.WAIT.getCode().equals(decisionResDetailDTO.getOrderStatus())) {
//                //处理中
//            }
//            if (decisionResDetailDTO != null) {
//                String riskCode = decisionResDetailDTO.getCode();
//                if (!PolicyResultEnum.isAgree(riskCode)) {
//                    //拒绝
//                    refuseTime = System.currentTimeMillis();
//                    conclusion = 40;
//                    remark = StringUtils.isNotBlank(decisionResDetailDTO.getDesc()) ? decisionResDetailDTO.getDesc() : "拒绝";
//                    reapply = "1";
//                    reapplyTime = DateFormatUtils.format(refuseTime + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
//                } else {
//                    conclusion = 10; //通过
//                    approvalTime = System.currentTimeMillis();
//                }
//            }
//        }
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
        return ResponseBean.success(map);
    }


    public ResponseBean<Map<String, Object>> auditResult(JSONObject param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));
        log.info("===============查询审批结论开始====================" + bizData.toJSONString());

        String orderNo = bizData.getString("order_no");

        User user = userService.selectByPrimaryKey(RequestThread.getUid());
        if (user == null || user.getUserOrigin().equals(UserOriginEnum.JH.getCode())) {
            throw new BizException("查询审批结论:用户不存在/用户非融泽用户,订单号=" + orderNo);
        }

        UserIdent userIdent = userIdentService.selectByPrimaryKey(RequestThread.getUid());
        if (userIdent == null) {
            throw new BizException("查询审批结论:用户不存在,订单号=" + orderNo);
        }
        int conclusion = 30; //10=审批通过 40=审批拒绝30=审批处理中
        String reapply = "0"; //是否可再申请 1-是，0-不可以
        Timestamp approvalTime = new Timestamp(System.currentTimeMillis()); //是否可再申请 1-是，0-不可以
        String reapplyTime = ""; //可再申请的时间，yyyy- MM-dd，比如（2020-10- 10）
        String remark = "";
        //
        if (userIdent.getRealName() == 2
                && userIdent.getMobile() == 2
                && userIdent.getUserDetails() == 2
                && userIdent.getAlipay() == 2
                && userIdent.getLiveness() == 2
                && userIdent.getBindbank() == 2) {
            conclusion = 10;
            int proType = 1; //单期产品
            int amountType = 0; //审批金额是否固定，0 - 固定
            int termType = 0; //审批期限是否固定，0 - 固定
            int approvalAmount = 1500; //审批金额
            int approvalTerm = 6; //审批期限
            int termUnit = 1; //期限单位，1 - 天
            String creditDeadline = DateUtil.getStringDateShort(); //审批结果有效期，当前时间
            map.put("reapplytime", approvalTime);
            map.put("pro_type", proType);
            map.put("term_unit", termUnit);
            map.put("amount_type", amountType);
            map.put("term_type", termType);
            map.put("approval_amount", approvalAmount);
            map.put("approval_term", approvalTerm);
            map.put("credit_deadline", creditDeadline);
        } else {
            conclusion = 40;
            reapply = "1";
            reapplyTime = DateFormatUtils.format(new Date().getTime() + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
            remark = "";
            map.put("order_no", orderNo);
            map.put("conclusion", conclusion);
            map.put("reapplytime", reapplyTime);
            map.put("remark", remark);
            map.put("refuse_time", approvalTime);
        }
        map.put("reapply", reapply);
        map.put("order_no", orderNo);
        map.put("conclusion", conclusion);
        log.info("===============查询审批结论结束====================");
        return ResponseBean.success(map);
    }


}
