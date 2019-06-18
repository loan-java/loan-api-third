package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Order;

public interface ChanpayService {

    ResultMessage bindCardRequest(String orderNo, long uid, String bankCardNo, String mobileNo);

    ResultMessage bindCardConfirm(String orderNo, String smsCode);

    /**
     * 还款
     *
     * @param order
     * @return
     */
    ResultMessage repay(Order order);
}
