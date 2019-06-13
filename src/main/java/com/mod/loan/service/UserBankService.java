package com.mod.loan.service;

import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.model.UserBank;

/**
 * @author kk
 */
public interface UserBankService extends BaseService<UserBank, Long> {
    /**
     * 获取当前使用中的银行卡
     *
     * @param uid
     * @return
     */
    UserBank selectUserCurrentBankCard(Long uid);
}
