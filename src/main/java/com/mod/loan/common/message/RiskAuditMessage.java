package com.mod.loan.common.message;

/**
 *通知风控消息模型
 */
public class RiskAuditMessage {

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 所属商户
     */
    private String merchant;
    /**
     * 1-返回上次结果 2-重新执行
     */
    private Integer status;

    /**
     * 用户uid
     */
    private Long uid;
    /**
     * 用户电话
     */
    private String userPhone;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
