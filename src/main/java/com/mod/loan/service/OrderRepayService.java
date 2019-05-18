package com.mod.loan.service;

import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderRepay;

public interface OrderRepayService extends BaseService<OrderRepay,String>{

	
	void updateOrderRepayInfo(OrderRepay orderRepay,Order order);

	int countRepaySuccess(Long orderId);

	OrderRepay selectByOrderId(Long orderId);
}
