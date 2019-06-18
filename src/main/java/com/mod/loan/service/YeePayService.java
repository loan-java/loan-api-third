package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Order;

/**
 * @ author liujianjian
 * @ date 2019/6/15 20:41
 */
public interface YeePayService {
    //绑卡请求
    ResultMessage requestBindCard(long uid, String orderNo, String cardno, String phone);

    //绑卡确认
    ResultMessage confirmBindCard(String orderNo, String validateCode);

    //还款
    ResultMessage repay(Order order) throws Exception;
}
