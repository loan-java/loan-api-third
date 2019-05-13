package com.mod.loan.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_order_phone")
public class OrderPhone {
    /**
     * 订单id
     */
    @Id
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 手机类型（1，ios；2，android）
     */
    @Column(name = "phone_type")
    private String phoneType;

    /**
     * 估价参数值
     */
    @Column(name = "param_value")
    private String paramValue;

    /**
     * 型号
     */
    @Column(name = "phone_model")
    private String phoneModel;

    /**
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取手机类型（1，ios；2，android）
     *
     * @return phone_type - 手机类型（1，ios；2，android）
     */
    public String getPhoneType() {
        return phoneType;
    }

    /**
     * 设置手机类型（1，ios；2，android）
     *
     * @param phoneType 手机类型（1，ios；2，android）
     */
    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType == null ? null : phoneType.trim();
    }

    /**
     * 获取估价参数值
     *
     * @return param_value - 估价参数值
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * 设置估价参数值
     *
     * @param paramValue 估价参数值
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue == null ? null : paramValue.trim();
    }
    /**
     * 获取型号
     *
     * @return phone_model - 型号
     */
    public String getPhoneModel() {
        return phoneModel;
    }

    /**
     * 设置型号
     *
     * @param phoneModel 型号
     */
    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel == null ? null : phoneModel.trim();
    }
}