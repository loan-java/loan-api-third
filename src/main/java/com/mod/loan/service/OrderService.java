package com.mod.loan.service;

import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderPay;
import com.mod.loan.model.OrderPhone;

import java.util.List;

public interface OrderService extends BaseService<Order,Long> {

	/**
	 * 查找用户最近一张订单
	 * @return
	 */
	Order findUserLatestOrder(Long uid);

    /**
     * 查找用户历史订单
     */

    List<Order> getByUid(Long uid);
    
    
    int addOrder(Order order ,OrderPhone orderPhone);
    
    
    OrderPhone findOrderPhoneByOrderId(Long orderId);
    /**
     * 查找用户收款成功记录
     */
    OrderPay findOrderPaySuccessRecord(Long orderId);

    Integer judgeUserTypeByUid(Long uid);

    Integer countByUid(Long uid);

    Integer countPaySuccessByUid(Long uid);

}
