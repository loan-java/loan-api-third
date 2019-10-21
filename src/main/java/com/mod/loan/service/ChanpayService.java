package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Bank;
import com.mod.loan.model.Order;
import com.mod.loan.model.vo.UserBankInfoVO;

public interface ChanpayService {

    ResultMessage bindCardRequest(Long uid, String cardNo, String cardPhone, Bank bank);

    ResultMessage bindCardConfirm(String validateCode, Long uid, UserBankInfoVO userBankInfoVO);

    /**
     * 还款
     *
     * @param order
     * @return
     */
    ResultMessage repay(Order order);
}
