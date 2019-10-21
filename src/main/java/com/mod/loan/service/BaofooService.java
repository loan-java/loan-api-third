package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Bank;
import com.mod.loan.model.Order;
import com.mod.loan.model.vo.UserBankInfoVO;

/**
 * @ author liujianjian
 * @ date 2019/5/17 15:54
 */
public interface BaofooService {

    ResultMessage sendBaoFooSms(Long uid, String cardNo, String cardPhone, Bank bank);

    ResultMessage bindBaoFooSms(String validateCode, Long uid, UserBankInfoVO userBankInfoVO);

    /**
     * 还款
     *
     * @param order
     * @return
     */
    ResultMessage repay(Order order);
}
