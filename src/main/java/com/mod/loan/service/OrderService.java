package com.mod.loan.service;

import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderPay;
import com.mod.loan.model.OrderPhone;

import java.util.List;
import java.util.Map;

public interface OrderService extends BaseService<Order,Long> {

    /**
     * 还款
     * @param orderNo
     * @return
     * @throws BizException
     */
    ResultMessage repayOrder(String orderNo, int source) throws BizException;

    /**
     * 根据订单编号查订单
     * @param orderNo
     * @return
     */
    Order findOrderByOrderNoAndSource(String orderNo, int source);

    /**
     * 提交借款申请的订单
     * @param orderNo
     * @param loanAmount
     * @param loanTerm
     * @param source 订单来源
     * @return
     */
    Order submitOrder(String orderNo, String loanAmount, int loanTerm, int source) throws BizException;

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

    void addOrUpdateOrder(Order order ,OrderPhone orderPhone);


    OrderPhone findOrderPhoneByOrderId(Long orderId);
    /**
     * 查找用户收款成功记录
     */
    OrderPay findOrderPaySuccessRecord(Long orderId);

    Integer judgeUserTypeByUid(Long uid);

    Integer countByUid(Long uid);

    Integer countPaySuccessByUid(Long uid);

}
