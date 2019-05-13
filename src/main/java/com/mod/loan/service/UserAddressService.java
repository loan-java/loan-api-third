package com.mod.loan.service;


import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.model.UserAddress;

import java.util.List;

public interface UserAddressService extends BaseService<UserAddress,Long> {

    List<UserAddress> getByUid(Long uid);

    void updateMasterByUid(UserAddress userAddress);

    void inserUserAddress(UserAddress userAddress);

    void updateUserAddress(UserAddress userAddress);

}