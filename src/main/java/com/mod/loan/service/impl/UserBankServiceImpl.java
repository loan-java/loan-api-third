package com.mod.loan.service.impl;

import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.mapper.UserBankMapper;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.UserBankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBankServiceImpl extends BaseServiceImpl<UserBank, Long> implements UserBankService {

    private static Logger log = LoggerFactory.getLogger(UserBankServiceImpl.class);

    @Autowired
    private UserBankMapper userBankMapper;


    @Override
    public UserBank selectUserCurrentBankCard(Long uid) {
        return userBankMapper.selectUserCurrentBankCard(uid);
    }
}
