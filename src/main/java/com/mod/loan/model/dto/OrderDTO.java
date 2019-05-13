package com.mod.loan.model.dto;

import com.alipay.api.domain.Product;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrderDTO {

	private Long orderId;

	private String orderNo;

	private String orderStatus;

	private BigDecimal productAmount;

	private BigDecimal deliveryAmount;

	private BigDecimal orderAmount;

	private List<Product> productList;

	private BigDecimal payMoney;

	private Date orderApplyDate;

	private Date payTime;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public BigDecimal getProductAmount() {
		return productAmount;
	}

	public void setProductAmount(BigDecimal productAmount) {
		this.productAmount = productAmount;
	}

	public BigDecimal getDeliveryAmount() {
		return deliveryAmount;
	}

	public void setDeliveryAmount(BigDecimal deliveryAmount) {
		this.deliveryAmount = deliveryAmount;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public BigDecimal getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}

	public Date getOrderApplyDate() {
		return orderApplyDate;
	}

	public void setOrderApplyDate(Date orderApplyDate) {
		this.orderApplyDate = orderApplyDate;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
}
