package com.mod.loan.common.message;

/**
 * 通知风控消息模型
 */
public class RiskAuditMessage {

    /**
     * 订单id 只有聚合有
     */
    private Long orderId;

    /**
     * 订单编号  聚合融泽都有
     */
    private String orderNo;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 所属商户
     */
    private String merchant;


    // 查询次数
    private int times;


    /**
     * 1-返回上次结果 2-重新执行
     */
    private Integer status;


    /**
     * 订单来源，0-聚合，1-融泽
     */
    private Integer source;

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


    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "RiskAuditMessage{" +
                "orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", merchant='" + merchant + '\'' +
                ", status=" + status +
                ", times=" + times +
                ", uid=" + uid +
                ", source=" + source +
                '}';
    }
}
