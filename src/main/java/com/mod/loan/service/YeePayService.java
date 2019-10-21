package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Bank;
import com.mod.loan.model.Order;
import com.mod.loan.model.vo.UserBankInfoVO;

/**
 * @ author liujianjian
 * @ date 2019/6/15 20:41
 */
public interface YeePayService {
    /**
     * 绑卡请求
     */
    ResultMessage requestBindCard(Long uid, String cardNo, String cardPhone, Bank bank);

    /**
     * 绑卡确认
     */
    ResultMessage confirmBindCard(String validateCode, Long uid, UserBankInfoVO userBankInfoVO);

    /**
     * 还款
     */
    ResultMessage repay(Order order);
}
