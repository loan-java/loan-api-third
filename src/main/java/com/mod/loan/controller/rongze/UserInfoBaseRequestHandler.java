package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.mapper.*;
import com.mod.loan.model.*;
import com.mod.loan.service.BlacklistService;
import com.mod.loan.service.OrderService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 推送用户基本信息
 */
@Slf4j
@Component
public class UserInfoBaseRequestHandler {

    @Autowired
    UserService userService;
    @Autowired
    OrderUserMapper orderUserMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserIdentMapper userIdentMapper;
    @Autowired
    UserAddressListMapper addressListMapper;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserBankMapper userBankMapper;


    //推送用户基本信息
    ResponseBean<Map<String, Object>> userInfoBase(JSONObject param) throws BizException {
        Map<String, Object> map = new HashMap<>();
        String message="成功";
        JSONObject bizData = param.getJSONObject("biz_data");
        log.info("===============推送用户基本信息开始====================" + bizData.toJSONString());
        JSONObject orderInfo = param.getJSONObject("orderInfo");//订单基本信息
        String orderNo = orderInfo.getString("order_no");
//        Integer isReloan = orderInfo.getInteger("is_reloan");
        String userName = orderInfo.getString("user_name");
        String userMobile = orderInfo.getString("user_mobile");
//        String applicationAmount = orderInfo.getString("application_amount");
//        Integer applicationTerm = orderInfo.getInteger("application_term");
//        Integer termUnit = orderInfo.getInteger("term_unit");
//        String orderTime = orderInfo.getString("order_time");

        JSONObject applyDetail = param.getJSONObject("applyDetail");//用户填写的基本信息
//        String userNameoOwn = applyDetail.getString("user_name");
        String userId = applyDetail.getString("user_id");
        String famadr = applyDetail.getString("famadr");
//        String validstartdate = applyDetail.getString("validstartdate");
        String validenddate = applyDetail.getString("validenddate");
        String agency = applyDetail.getString("agency");
        String nation = applyDetail.getString("nation");
        String userEducation = applyDetail.getString("user_education");
//        String isOnType = applyDetail.getString("is_on_type");
//        String workPeriod = applyDetail.getString("work_period");
        String userIncomeByCard = applyDetail.getString("user_income_by_card");
//        String operatingYear = applyDetail.getString("operating_year");
//        String corporateFlow = applyDetail.getString("corporate_flow");
//        String monthlyAverageIncome = applyDetail.getString("monthly_average_income");
//        String userSocialSecurity = applyDetail.getString("user_social_security");
//        JSONObject addInfo = param.getJSONObject("addInfo");//抓取信审信息
        //开始新增用户
        User user = userService.selectUserByPhone(userMobile, RequestThread.getClientAlias());
        if (user == null) {
            user = new User();
            user.setUserPhone(userMobile);
            user.setUserPwd("");
            user.setUserCertNo(userId);
            user.setAddress(famadr);
            user.setIa(agency);
            user.setUserName(userName);
            user.setIndate(validenddate);
            user.setNation(nation);
            user.setUserOrigin(UserOriginEnum.RZ.getCode());
            user.setMerchant(RequestThread.getClientAlias());
            user.setCommonInfo(applyDetail.toJSONString());
            userMapper.insertSelective(user);
            UserIdent userIdent = new UserIdent();
            userIdent.setUid(user.getId());
            userIdent.setCreateTime(new Date());
            userIdentMapper.insertSelective(userIdent);
            UserAddressList addressList = new UserAddressList();
            addressList.setUid(user.getId());
            addressList.setCreateTime(new Date());
            addressListMapper.insertSelective(addressList);
            UserInfo userInfo = new UserInfo();
            userInfo.setEducation(userEducation);
            userInfo.setIncomeMonthlyYuan(new BigDecimal(userIncomeByCard));
            userInfoMapper.insertSelective(userInfo);
        } else {
            user.setUserPhone(userMobile);
            user.setUserPwd("");
            user.setUserCertNo(userId);
            user.setAddress(famadr);
            user.setIa(agency);
            user.setUserName(userName);
            user.setIndate(validenddate);
            user.setNation(nation);
            user.setUserOrigin("2");
            user.setMerchant(RequestThread.getClientAlias());
            user.setCommonInfo(applyDetail.toJSONString());
            userMapper.updateByPrimaryKey(user);
            UserInfo userInfo =userInfoMapper.selectByPrimaryKey(user.getId());
            userInfo.setEducation(userEducation);
            userInfo.setIncomeMonthlyYuan(new BigDecimal(userIncomeByCard));
            userInfoMapper.updateByPrimaryKey(userInfo);
        }
        //新增融泽用户订单关联信息
        OrderUser orderUser = new OrderUser();
        orderUser.setCreateTime(new Date());
        orderUser.setOrderNo(orderNo);
        orderUser.setSource(2);
        orderUser.setUid(user.getId());
        orderUserMapper.insert(orderUser);
        log.info("===============推送用户基本信息结束====================");
        return new ResponseBean<>(200, message, map);
    }


}
