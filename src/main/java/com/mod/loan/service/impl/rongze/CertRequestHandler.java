package com.mod.loan.service.impl.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.OrderUserMapper;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.*;
import com.mod.loan.service.BlacklistService;
import com.mod.loan.service.MerchantRateService;
import com.mod.loan.service.OrderService;
import com.mod.loan.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询复贷和黑名单信息
 */
@Slf4j
@Component
public class CertRequestHandler {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BlacklistService blacklistService;
    @Resource
    private OrderUserMapper orderUserMapper;
    @Resource
    private RedisMapper redisMapper;
    @Resource
    private MerchantRateService merchantRateService;

    //复贷黑名单信息
    public ResponseBean<Map<String, Object>> certAuth(JSONObject param) throws BizException {
        Map<String, Object> map = new HashMap<>();
        String message = "成功";
        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));
        log.info("查询复贷和黑名单信息开始");
        String md5 = bizData.getString("md5");
        String orderNo = bizData.getString("order_no");
        String userType = "3"; //1-不可申请用户，2-复贷用户，3-正常申请用户
        User user = userMapper.getMd5PhoneAndIdcard(md5);
        if (user != null) {
            if (StringUtils.isEmpty(user.getUserOrigin()) || !user.getUserOrigin().equals(UserOriginEnum.RZ.getCode())) {
                log.info(user.getId() + "聚合用户，不走当前线路");
                userType = "1";
                message = "聚合用户，不走当前线路";
            } else {
                Blacklist blacklist = blacklistService.getByUid(user.getId());
                if (null != blacklist) {
                    if (1 == blacklist.getType()) {
                        // 校验灰名单锁定天数
                        DateTime d1 = new DateTime(new Date());
                        DateTime d2 = new DateTime(blacklist.getInvalidTime());
                        Integer remainDays = Days.daysBetween(d1, d2).getDays() + 1;
                        log.info(user.getId() + "暂时无法下单，请于" + remainDays + "天后再尝试");
                        userType = "1";
                        message = "C002";
                    } else if (2 == blacklist.getType()) {
                        // 黑名单
                        log.info(user.getId() + "您不符合下单条件");
                        userType = "1";
                        message = "C002";
                    }
                }
                if (userType.equals("3")) {
                    // 是否有正在借款中的订单
                    Order orderIng = orderService.findUserLatestOrder(user.getId());
                    if (null != orderIng) {
                        if (orderIng.getStatus() < 40 && orderIng.getStatus() != 23) {
                            log.info(user.getId() + "订单进行中，无法提单");
                            userType = "1";
                            message = "C001";
                        } else if (orderIng.getStatus() == 51 || orderIng.getStatus() == 52) {
                            // 审核拒绝的订单30天内无法再下单
                            DateTime applyTime = new DateTime(orderIng.getCreateTime()).plusDays(7);
                            DateTime nowTime = new DateTime();
                            Integer remainDays = Days.daysBetween(nowTime.withMillisOfDay(0), applyTime.withMillisOfDay(0)).getDays();
                            if (0 < remainDays && remainDays <= 30) {
                                log.info(user.getId() + "请" + remainDays + "天后重试提单");
                                userType = "1";
                                message = "C003";
                            }
                        } else if (orderIng.getStatus() == 41 || orderIng.getStatus() == 42) {
                            // 订单已结清
                            log.info(user.getId() + "最后一笔订单已结清");
                            userType = "2";

                            OrderUser ou = orderUserMapper.getUidByOrderNoAndSourceAndUid(orderNo, Integer.valueOf(UserOriginEnum.RZ.getCode()), user.getId());
                            if (ou == null) {
                                ou = new OrderUser();
                                ou.setCreateTime(new Date());
                                ou.setOrderNo(orderNo);
                                ou.setSource(Integer.valueOf(UserOriginEnum.RZ.getCode()));
                                ou.setUid(user.getId());
                                orderUserMapper.insertSelective(ou);
                                //设置缓存
                                String key=redisMapper.getOrderUserKey(orderNo, UserOriginEnum.RZ.getCode());
                                redisMapper.set(key, user.getId());
                            }
                        }
                    }
                }
            }
        }
        //userType： 1-不可申请用户，2-复贷用户，3-正常申请用户
        MerchantRate merchantRate = merchantRateService.findByMerchant(RequestThread.getClientAlias());
        if(merchantRate == null){
            throw new BizException("查询复贷和黑名单信息开始:不存在借贷金额信息");
        }
        int proType = 1; //单期产品
        int amountType = 0; //审批金额是否固定，0 - 固定
        int termType = 0; //审批期限是否固定，0 - 固定
        BigDecimal approvalAmount = merchantRate.getProductMoney(); //审批金额
        int approvalTerm = merchantRate.getProductDay(); //审批期限
        int termUnit = 1; //期限单位，1 - 天

        int code = 200; //200-通过，400-不通过
        if (userType.equals("1")) {
            code = 400;
        } else if (userType.equals("2")) {
            map.put("is_reloan", 1);
            map.put("amount_type", amountType);
            map.put("pro_type", proType);
            map.put("term_type", termType);
            map.put("approval_term", approvalTerm);
            map.put("approval_amount", approvalAmount);
            map.put("term_unit", termUnit);
            map.put("credit_deadline", DateUtil.getNextDay(DateUtil.getStringDateShort(), "1"));
        } else {
            map.put("is_reloan", 0);
        }
        log.info("查询复贷和黑名单信息结束");
        return new ResponseBean<>(code, message, map);
    }


}
