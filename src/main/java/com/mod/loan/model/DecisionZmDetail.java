package com.mod.loan.model;


import javax.persistence.*;
import java.util.Date;

/**
 * 指谜分控
 */
@Table(name = "tb_decision_zm_detail")
public class DecisionZmDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "return_code")
    private String returnCode;

    @Column(name = "return_info")
    private String returnInfo;

    @Column(name = "history_apply")
    private String historyApply;

    @Column(name = "score")
    private String score;

    @Column(name = "create_time")
    private Date createtime;

    @Column(name = "update_time")
    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public String getHistoryApply() {
        return historyApply;
    }

    public void setHistoryApply(String historyApply) {
        this.historyApply = historyApply;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public String toString() {
        return "DecisionZmDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", requestId='" + requestId + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", returnInfo='" + returnInfo + '\'' +
                ", historyApply='" + historyApply + '\'' +
                ", score='" + score + '\'' +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                '}';
    }
}