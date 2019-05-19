package com.mod.loan.service.impl.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.*;
import com.mod.loan.service.*;
import com.mod.loan.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询复贷和黑名单信息
 */
@Slf4j
@Component
public class CertRequestHandler {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BlacklistService blacklistService;


    //复贷黑名单信息
    public ResponseBean<Map<String, Object>> certAuth(JSONObject param) throws BizException {
        Map<String, Object> map = new HashMap<>();
        String message="成功";
        JSONObject bizData =  JSONObject.parseObject(param.getString("biz_data"));
        log.info("===============贷相关信息开始====================" + bizData.toJSONString());
        String md5 = bizData.getString("md5");
        String userType = "3"; //1-不可申请用户，2-复贷用户，3-正常申请用户
        User user = userMapper.getMd5PhoneAndIdcard(md5);
        if(user != null) {
            Blacklist blacklist = blacklistService.getByUid(user.getId());
            if (null != blacklist) {
                if (1 == blacklist.getType()) {
                    // 校验灰名单锁定天数
                    DateTime d1 = new DateTime(new Date());
                    DateTime d2 = new DateTime(blacklist.getInvalidTime());
                    Integer remainDays = Days.daysBetween(d1, d2).getDays() + 1;
                    log.info(user.getId() + "暂时无法下单，请于" + remainDays + "天后再尝试");
                    log.info("==================================================================");
                    userType = "1";
                    message="C002";
                }else  if (2 == blacklist.getType()) {
                    // 黑名单
                    log.info(user.getId() + "您不符合下单条件");
                    log.info("==================================================================");
                    userType = "1";
                    message="C002";
                }
            }
            if (userType.equals("3")) {
                // 是否有正在借款中的订单
                Order orderIng = orderService.findUserLatestOrder(user.getId());
                if( null != orderIng) {
                    if (orderIng.getStatus() < 40) {
                        log.info(user.getId() + "订单进行中，无法提单");
                        log.info("==================================================================");
                        userType = "1";
                        message="C001";
                    }else if(orderIng.getStatus() == 51 || orderIng.getStatus() == 52){
                        // 审核拒绝的订单30天内无法再下单
                        DateTime applyTime = new DateTime(orderIng.getCreateTime()).plusDays(7);
                        DateTime nowTime = new DateTime();
                        Integer remainDays = Days.daysBetween(nowTime.withMillisOfDay(0), applyTime.withMillisOfDay(0)).getDays();
                        if (0 < remainDays && remainDays <= 30) {
                            log.info(user.getId() + "请" + remainDays + "天后重试提单");
                            log.info("==================================================================");
                            userType = "1";
                            message="C003";
                        }
                    } else if(orderIng.getStatus() == 41 || orderIng.getStatus() == 42){
                        // 订单已结清
                        log.info(user.getId() + "最后一笔订单已结清");
                        log.info("==================================================================");
                        userType = "2";
                    }
                }
            }
        }
        //userType： 1-不可申请用户，2-复贷用户，3-正常申请用户
        int code=200; //200-通过，400-不通过
        if(userType.equals("1")){
            code=400;
        }else if(userType.equals("2")){
            map.put("is_reloan",1);
            map.put("amount_type",0);
            map.put("pro_type",1);
            map.put("term_type",0);
            map.put("approval_term",1);
            map.put("term_unit",1);
            map.put("credit_deadline", DateUtil.getNextDay(DateUtil.getStringDateShort(),"1"));
        }else{
            map.put("is_reloan","1");
        }
        log.info("===============贷相关信息结束====================");
        return new ResponseBean<>(code, message, map);
    }


}
