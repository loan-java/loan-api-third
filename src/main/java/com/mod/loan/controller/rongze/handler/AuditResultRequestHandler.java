package com.mod.loan.controller.rongze.handler;


import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.mapper.OrderUserMapper;
import com.mod.loan.mapper.TypeFilterMapper;
import com.mod.loan.model.Merchant;
import com.mod.loan.model.MerchantRate;
import com.mod.loan.model.User;
import com.mod.loan.model.UserIdent;
import com.mod.loan.service.*;
import com.mod.loan.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
    private MerchantService merchantService;
    @Resource
    private MerchantRateService merchantRateService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderUserMapper orderUserMapper;

    @Autowired
    private TypeFilterService typeFilterService;

    @Resource
    private TypeFilterMapper typeFilterMapper;

    public ResponseBean<Map<String, Object>> auditResult(JSONObject param) throws Exception {
        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));

        String orderNo = bizData.getString("order_no");
        log.info("===============查询审批结论开始====================" + orderNo);

        Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
        if (merchant == null) {
            throw new BizException("商户【" + RequestThread.getClientAlias() + "】不存在，未配置");
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
        Long merchantRateId = orderUserMapper.getMerchantRateByOrderNoAndSource(orderNo, Integer.parseInt(UserOriginEnum.RZ.getCode()));
        if (merchantRateId == null) {
            throw new BizException("查询审批结论:商户不存在默认借贷信息");
        }
        MerchantRate merchantRate = merchantRateService.selectByPrimaryKey(merchantRateId);
        if (merchantRate == null) {
            throw new BizException("查询审批结论:商户不存在默认借贷信息");
        }

        //收款撤退 准备回款 全部不走风控
        //单期产品
        int proType = 1;
        //审批金额是否固定，0 - 固定
        int amountType = 0;
        //审批期限是否固定，0 - 固定
        int termType = 0;
        BigDecimal approvalAmount = merchantRate.getProductMoney(); //审批金额
        if (approvalAmount == null) {
            throw new BizException("查询审批结论:商户不存在默认借贷金额");
        }
        Integer approvalTerm = merchantRate.getProductDay(); //审批期限
        if (approvalAmount == null) {
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
        log.info("===============查询审批结论结束====================" + orderNo);
        return ResponseBean.success(map);


//        //不丢失复贷用户 复贷用户前四次不需要走规则集以及探针
//        List<Order> orderList = orderMapper.getDoubleLoanByUid(user.getId());
//        if (orderList != null && orderList.size() > 0 && orderList.size() < 5) {
//            conclusion = 10;
//            remark = "审批成功";
//            //单期产品
//            int proType = 1;
//            //审批金额是否固定，0 - 固定
//            int amountType = 0;
//            //审批期限是否固定，0 - 固定
//            int termType = 0;
//            BigDecimal approvalAmount = merchantRate.getProductMoney(); //审批金额
//            if (approvalAmount == null) {
//                throw new BizException("查询审批结论:商户不存在默认借贷金额");
//            }
//            Integer approvalTerm = merchantRate.getProductDay(); //审批期限
//            if (approvalAmount == null) {
//                throw new BizException("查询审批结论:商户不存在默认借贷期限");
//            }
//            //期限单位，1 - 天
//            int termUnit = 1;
//            //是否可再申请 1-是，0-不可以
//            String reapply = "1";
//            Timestamp approvalTime = new Timestamp(System.currentTimeMillis());
//            //可再申请的时间，yyyy- MM-dd，比如（2020-10- 10）
//            String reapplyTime = DateFormatUtils.format(System.currentTimeMillis() + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
//            //审批结果有效期，往后30天
//            String creditDeadline = DateUtil.getNextDay(DateUtil.getStringDateShort(), "30");
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("reapplytime", reapplyTime);
//            map.put("pro_type", proType);
//            map.put("term_unit", termUnit);
//            map.put("amount_type", amountType);
//            map.put("term_type", termType);
//            map.put("approval_amount", approvalAmount.intValue());
//            map.put("approval_term", approvalTerm.intValue());
//            map.put("credit_deadline", creditDeadline);
//            map.put("refuse_time", approvalTime);
//            map.put("remark", remark);
//            map.put("reapply", reapply);
//            map.put("order_no", orderNo);
//            map.put("conclusion", conclusion);
//            log.info("===============查询审批结论结束====================" + orderNo);
//            return ResponseBean.success(map);
//        }
//
//        //todo 自己的规则集逻辑
//
//        TypeFilter typeFilter = new TypeFilter();
//        typeFilter.setOrderNo(orderNo);
//        typeFilter.setType(1);
//        typeFilter = typeFilterMapper.selectOne(typeFilter);
//        if (typeFilter == null) {
//            ThreadPoolUtils.executor.execute(() -> {
//                //探针A逻辑
//                typeFilterService.getInfoByTypeA(user, orderNo);
//            });
//            conclusion = 30;
//            remark = "审批处理中";
//        } else {
//            if ("true".equalsIgnoreCase(typeFilter.getResult())) {
//                conclusion = 40;
//                remark = "审批拒绝";
//            } else {
//                conclusion = 10;
//                remark = "审批成功";
//            }
//        }
//
//        BigDecimal approvalAmount1 = merchantRate.getProductMoney(); //审批金额
//        if (approvalAmount1 == null) {
//            throw new BizException("查询审批结论:商户不存在默认借贷金额");
//        }
//        Integer approvalTerm1 = merchantRate.getProductDay(); //审批期限
//        if (approvalAmount1 == null) {
//            throw new BizException("查询审批结论:商户不存在默认借贷期限");
//        }
//        //单期产品
//        int proType = 1;
//        //审批金额是否固定，0 - 固定
//        int amountType = 0;
//        //审批期限是否固定，0 - 固定
//        int termType = 0;
//        //审批金额
//        int approvalAmount = approvalAmount1.intValue();
//        //审批期限
//        int approvalTerm = approvalTerm1.intValue();
//        //期限单位，1 - 天
//        int termUnit = 1;
//        //是否可再申请 1-是，0-不可以
//        String reapply = "1";
//        Timestamp approvalTime = new Timestamp(System.currentTimeMillis());
//        //可再申请的时间，yyyy- MM-dd，比如（2020-10- 10）
//        String reapplyTime = DateFormatUtils.format(System.currentTimeMillis() + (1000L * 3600 * 24 * 7), "yyyy-MM-dd");
//        //审批结果有效期，往后30天
//        String creditDeadline = DateUtil.getNextDay(DateUtil.getStringDateShort(), "30");
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("reapplytime", reapplyTime);
//        map.put("pro_type", proType);
//        map.put("term_unit", termUnit);
//        map.put("amount_type", amountType);
//        map.put("term_type", termType);
//        map.put("approval_amount", approvalAmount);
//        map.put("approval_term", approvalTerm);
//        map.put("credit_deadline", creditDeadline);
//        map.put("refuse_time", approvalTime);
//        map.put("remark", remark);
//        map.put("reapply", reapply);
//        map.put("order_no", orderNo);
//        map.put("conclusion", conclusion);
//        log.info("===============查询审批结论结束====================");
//        return ResponseBean.success(map);
    }
}
