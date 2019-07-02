package com.mod.loan.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_user_order")
public class OrderUser {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    @Column(name = "order_no")
    private String orderNo;
    /**
     * 用户Id
     */
    @Column(name = "uid")
    private Long uid;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 订单来源，0-聚合，1-融泽
     */
    @Column(name = "source")
    private Integer source;

    /**
     * 借款信息id
     */
    @Column(name = "merchant_rate_id")
    private Long merchantRateId;

    public Long getMerchantRateId() {
        return merchantRateId;
    }

    public void setMerchantRateId(Long merchantRateId) {
        this.merchantRateId = merchantRateId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "OrderUser{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", uid=" + uid +
                ", createTime=" + createTime +
                ", source=" + source +
                '}';
    }
}