package com.mod.loan.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_type_filter")
public class TypeFilter {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "order_no")
    private String orderNo;

    /**
     * 1-新颜 ,2-规则
     */
    @Column(name = "type")
    private Integer type;

    @Column(name = "result")
    private String result;

    @Column(name = "resultl_str")
    private String resultlStr;

    @Column(name = "create_time")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getResultlStr() {
        return resultlStr;
    }

    public void setResultlStr(String resultlStr) {
        this.resultlStr = resultlStr;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}