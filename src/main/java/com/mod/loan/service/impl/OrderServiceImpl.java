package com.mod.loan.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.mapper.OrderPayMapper;
import com.mod.loan.mapper.OrderPhoneMapper;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderPay;
import com.mod.loan.model.OrderPhone;
import com.mod.loan.service.OrderService;

@Service
public class OrderServiceImpl  extends BaseServiceImpl<Order,Long> implements OrderService {

	@Autowired
	OrderMapper orderMapper;
	@Autowired
	OrderPhoneMapper orderPhoneMapper;
	@Autowired
	OrderPayMapper orderPayMapper;
	@Override
	public Order findUserLatestOrder(Long uid) {
		// TODO Auto-generated method stub
		return orderMapper.findUserLatestOrder(uid);
	}

	@Override
	public List<Order> getByUid(Long uid) {
		return orderMapper.getByUid(uid);
	}

	@Override
	public int addOrder(Order order, OrderPhone orderPhone) {
		// TODO Auto-generated method stub
		orderMapper.insertSelective(order);
		orderPhone.setOrderId(order.getId());
		return orderPhoneMapper.insertSelective(orderPhone);
	}

	@Override
	public OrderPhone findOrderPhoneByOrderId(Long orderId) {
		// TODO Auto-generated method stub
		return orderPhoneMapper.selectByPrimaryKey(orderId);
	}

	@Override
	public OrderPay findOrderPaySuccessRecord(Long orderId) {
		return orderPayMapper.selectByOrderIdAndStatus(orderId,3);
	}

	@Override
	public Integer judgeUserTypeByUid(Long uid) {
		return orderMapper.judgeUserTypeByUid(uid);
	}

	@Override
	public Integer countByUid(Long uid) {
		Order order = new Order();
		order.setUid(uid);
		return orderMapper.selectCount(order);
	}

	@Override
	public Integer countPaySuccessByUid(Long uid){
		return orderMapper.countPaySuccessByUid(uid);
	}

}
