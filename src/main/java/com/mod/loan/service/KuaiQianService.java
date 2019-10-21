package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Bank;
import com.mod.loan.model.Order;
import com.mod.loan.model.vo.UserBankInfoVO;

/**
 * @author kk
 */
public interface KuaiQianService {

    ResultMessage sendKuaiQianSms(Long uid, String cardNo, String cardPhone, Bank bank);

    ResultMessage bindKuaiQianSms(String validateCode, Long uid, UserBankInfoVO userBankInfoVO);


    ResultMessage repay(Order order);
}
