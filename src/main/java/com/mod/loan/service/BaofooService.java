package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Order;

/**
 * @ author liujianjian
 * @ date 2019/5/17 15:54
 */
public interface BaofooService {

    /**
     * 还款
     *
     * @param order
     * @return
     */
    ResultMessage repay(Order order);
}
