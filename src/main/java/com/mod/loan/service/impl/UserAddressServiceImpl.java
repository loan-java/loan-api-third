package com.mod.loan.service.impl;


import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.mapper.UserAddressMapper;
import com.mod.loan.model.UserAddress;
import com.mod.loan.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressServiceImpl extends BaseServiceImpl<UserAddress,Long> implements UserAddressService {

    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getByUid(Long uid) {
        return userAddressMapper.getByUid(uid);
    }

    @Override
    public void updateMasterByUid(UserAddress userAddress) {
        userAddressMapper.updateMasterByUid(userAddress.getUid());
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Override
    public void inserUserAddress(UserAddress userAddress) {
        if(1==userAddress.getMaster()){
            userAddressMapper.updateMasterByUid(userAddress.getUid());
        }
        userAddressMapper.insertSelective(userAddress);
    }

    @Override
    public void updateUserAddress(UserAddress userAddress) {
        if(1==userAddress.getMaster()){
            userAddressMapper.updateMasterByUid(userAddress.getUid());
        }
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

}