package com.mod.loan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.mapper.OrderRepayMapper;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderRepay;
import com.mod.loan.service.OrderRepayService;

@Service
public class OrderRepayServiceImpl extends BaseServiceImpl<OrderRepay,String>  implements OrderRepayService {

	@Autowired
	OrderRepayMapper orderRepayMapper;
	@Autowired
	OrderMapper orderMapper;

	@Override
	public void updateOrderRepayInfo( OrderRepay orderRepay,Order order){
		orderRepayMapper.updateByPrimaryKeySelective(orderRepay);
		orderMapper.updateByPrimaryKeySelective(order);
	}

	@Override
	public int countRepaySuccess(Long orderId) {
		return orderRepayMapper.countRepaySuccess(orderId);
	}

}
