package com.mod.loan.controller.bengbeng.handler;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.*;
import com.mod.loan.model.*;
import com.mod.loan.service.MerchantRateService;
import com.mod.loan.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 推送用户基本信息
 */
@Slf4j
@Service
public class BengBengUserInfoBaseRequestHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderUserMapper orderUserMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserIdentMapper userIdentMapper;
    @Autowired
    private UserAddressListMapper addressListMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Resource
    private RedisMapper redisMapper;
    @Resource
    private MerchantRateService merchantRateService;

    //推送用户基本信息
    @Transactional
    public ResponseBean<Map<String, Object>> userInfoBase(JSONObject param) throws BizException {
        Map<String, Object> map = new HashMap<>();
        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));
        JSONObject orderInfo = bizData.getJSONObject("orderInfo");//订单基本信息
        String orderNo = orderInfo.getString("order_no");
        String userName = orderInfo.getString("user_name");
        String userMobile = orderInfo.getString("user_mobile");
        JSONObject applyDetail = bizData.getJSONObject("applyDetail");//用户填写的基本信息
        String userId = applyDetail.getString("user_id");
        String famadr = applyDetail.getString("famadr");
        String validenddate = applyDetail.getString("validenddate");
        String agency = applyDetail.getString("agency");
        String nation = applyDetail.getString("nation");
        String userEducation = applyDetail.getString("user_education");
        String userIncomeByCard = applyDetail.getString("user_income_by_card");
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
            user.setUserOrigin(UserOriginEnum.BB.getCode());
            user.setMerchant(RequestThread.getClientAlias());
            user.setCommonInfo(applyDetail.toJSONString());
            int n = userMapper.insertSelective(user);
            if (n == 0) throw new RuntimeException("推送用户基本信息:新增用户失败");
            UserIdent userIdent = new UserIdent();
            userIdent.setUid(user.getId());
            userIdent.setCreateTime(new Date());
            n = userIdentMapper.insertSelective(userIdent);
            if (n == 0) throw new RuntimeException("推送用户基本信息:新增用户认证信息失败");
            UserAddressList addressList = new UserAddressList();
            addressList.setUid(user.getId());
            addressList.setCreateTime(new Date());
            addressList.setUpdateTime(new Date());
            n = addressListMapper.insertSelective(addressList);
            if (n == 0) throw new RuntimeException("推送用户基本信息:新增通讯录信息失败");
            UserInfo userInfo = new UserInfo();
            userInfo.setEducation(this.getXLBM(userEducation));
            userInfo.setUid(user.getId());
            userInfo.setIncomeMonthlyYuan(new BigDecimal(userIncomeByCard));
            userInfo.setCreateTime(new Date());
            n = userInfoMapper.insertSelective(userInfo);
            if (n == 0) throw new RuntimeException("推送用户基本信息:新增用户详情信息失败");
        } else {
            user.setUserPhone(userMobile);
            user.setUserPwd("");
            user.setUserCertNo(userId);
            user.setAddress(famadr);
            user.setIa(agency);
            user.setUserName(userName);
            user.setIndate(validenddate);
            user.setNation(nation);
            user.setUserOrigin(UserOriginEnum.BB.getCode());
            user.setMerchant(RequestThread.getClientAlias());
            user.setCommonInfo(applyDetail.toJSONString());
            int n = userMapper.updateByPrimaryKey(user);
            if (n == 0) throw new RuntimeException("推送用户基本信息:更新用户失败");
            UserInfo userInfo = userInfoMapper.selectByPrimaryKey(user.getId());
            userInfo.setEducation(this.getXLBM(userEducation));
            userInfo.setIncomeMonthlyYuan(new BigDecimal(userIncomeByCard));
            userInfo.setUpdateTime(new Date());
            n = userInfoMapper.updateByPrimaryKey(userInfo);
            if (n == 0) throw new RuntimeException("推送用户基本信息:更新用户详情信息失败");
        }
        OrderUser orderUser = orderUserMapper.getUidByOrderNoAndSourceAndUid(orderNo, Integer.valueOf(UserOriginEnum.BB.getCode()), user.getId());
        if (orderUser == null) {
            String merchatRateKey = redisMapper.getMerchantRateId(orderNo, UserOriginEnum.BB.getCode());
            String  merchantRateId = null;
            if(redisMapper.hasKey(merchatRateKey)) {
                merchantRateId =redisMapper.get(merchatRateKey);
            }
            if("null".equals(merchantRateId) || merchantRateId == null || "".equals(merchantRateId)){
                MerchantRate merchantRate = merchantRateService.findByMerchant(RequestThread.getClientAlias());
                if(merchantRate == null){
                    throw new BizException("推送用户基本信息:商户不存在默认借贷信息");
                }
                merchantRateId = String.valueOf(merchantRate.getId());
                redisMapper.set(merchatRateKey, merchantRateId);
            }

            orderUser = new OrderUser();
            orderUser.setCreateTime(new Date());
            orderUser.setOrderNo(orderNo);
            orderUser.setSource(Integer.valueOf(UserOriginEnum.BB.getCode()));
            orderUser.setUid(user.getId());
            orderUser.setMerchantRateId(Long.parseLong(merchantRateId));
            int m = orderUserMapper.insert(orderUser);
            if (m == 0) throw new RuntimeException("推送用户基本信息:新增用户订单关联信息");
            //设置缓存
            String key=redisMapper.getOrderUserKey(orderNo,UserOriginEnum.BB.getCode());
            redisMapper.set(key, user.getId());
        }

        log.info("推送用户基本信息结束");
        return ResponseBean.success(map);
    }


    public static String getXLBM(String type) {
        Map<String, String> map = new HashMap<>();
        map.put("E02", "高中/职中");
        map.put("E04", "本科(学士)");
        map.put("E05", "研究生及以上");
        map.put("E03", "大专");
        map.put("E01", "初中及以下");
        return map.get(type);
    }


}
