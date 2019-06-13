package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Order;

/**
 * @ author liujianjian
 * @ date 2019/5/17 15:54
 */
public interface BaofooService {

    ResultMessage sendBaoFooSms(Long uid, String cardNo, String cardPhone);

    ResultMessage bindBaoFooSms(String validateCode, Long uid, String bindInfo, String cardNo,
                                String cardPhone, String bankCode, String bankName);

    /**
     * 还款
     *
     * @param order
     * @return
     */
    ResultMessage repay(Order order);
}
