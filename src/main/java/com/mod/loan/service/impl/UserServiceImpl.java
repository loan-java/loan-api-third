package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.mapper.*;
import com.mod.loan.model.*;
import com.mod.loan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author kk
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
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

    @Override
    public User selectUserByPhone(String userPhone, String merchant) {
        // TODO Auto-generated method stub
        User u = new User();
        u.setUserPhone(userPhone);
        u.setMerchant(merchant);
        return userMapper.selectOne(u);
    }

    @Override
    public User addUser(String phone, String password, String userOrigin, String merchant, JSONObject param) {
        // TODO Auto-generated method stub
        User user = new User();
        user.setUserPhone(phone);
        user.setUserPwd(password);
        user.setUserOrigin(userOrigin);
        user.setMerchant(merchant);
        user.setCommonInfo(param.toJSONString());
        userMapper.insertSelective(user);
        UserIdent userIdent = new UserIdent();
        userIdent.setUid(user.getId());
        userIdent.setCreateTime(new Date());
        userIdentMapper.insertSelective(userIdent);
        UserAddressList addressList = new UserAddressList();
        addressList.setUid(user.getId());
        addressList.setCreateTime(new Date());
        addressList.setUpdateTime(new Date());
        addressListMapper.insertSelective(addressList);
        UserInfo userInfo = new UserInfo();
        userInfo.setUid(user.getId());
        userInfo.setCreateTime(new Date());
        userInfoMapper.insertSelective(userInfo);
        return user;
    }

    @Override
    public void updateUserRealName(User user, UserIdent userIdent) {
        // TODO Auto-generated method stub
        userMapper.updateByPrimaryKeySelective(user);
        userIdentMapper.updateByPrimaryKeySelective(userIdent);
    }

    @Override
    public User selectUserByCertNo(String userCertNo, String merchant) {
        // TODO Auto-generated method stub
        User u = new User();
        u.setUserCertNo(userCertNo);
        u.setMerchant(merchant);
        return userMapper.selectOne(u);
    }

    @Override
    public void updateUserInfo(UserInfo userInfo, UserIdent userIdent) {
        // TODO Auto-generated method stub
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
        userIdentMapper.updateByPrimaryKeySelective(userIdent);
    }


    @Override
    public boolean insertUserBank(Long uid, UserBank userBank) {
        // TODO Auto-generated method stub
        UserBank bank = userBankMapper.selectUserCurrentBankCard(uid);
        if (bank != null && bank.getCardNo().equals(userBank.getCardNo())) {
            log.error("绑卡已绑定，用户={}", uid);
            return false;
        }
        //1.更新绑卡认证状态
        UserIdent ident = new UserIdent();
        ident.setUid(uid);
        ident.setBindbank(2);
        ident.setBindbankTime(new Date());
        userIdentMapper.updateByPrimaryKeySelective(ident);

        //2.把之前的老卡无效
        userBankMapper.updateUserOldCardInvaild(uid);

        userBankMapper.insertSelective(userBank);
        return true;
    }
}
