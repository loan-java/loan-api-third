package com.mod.loan.model;


import javax.persistence.*;
import java.util.Date;

/**
 * 盘宝分控
 */
@Table(name = "tb_decision_pb_detail")
public class DecisionPbDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "loan_no")
    private String loanNo;

    @Column(name = "result")
    private String result;

    @Column(name = "loan_money")
    private Long loanMoney;

    @Column(name = "loan_rate")
    private String loanRate;

    @Column(name = "loan_number")
    private Integer loanNumber;

    @Column(name = "loan_unit")
    private String loanUnit;

    @Column(name = "pb_code")
    private String code;

    @Column(name = "msg")
    private String msg;

    @Column(name = "pb_desc")
    private String desc;

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

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public Long getLoanMoney() {
        return loanMoney;
    }

    public void setLoanMoney(Long loanMoney) {
        this.loanMoney = loanMoney;
    }

    public String getLoanRate() {
        return loanRate;
    }

    public void setLoanRate(String loanRate) {
        this.loanRate = loanRate;
    }

    public Integer getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(Integer loanNumber) {
        this.loanNumber = loanNumber;
    }

    public String getLoanUnit() {
        return loanUnit;
    }

    public void setLoanUnit(String loanUnit) {
        this.loanUnit = loanUnit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "DecisionPbDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", loanNo='" + loanNo + '\'' +
                ", result='" + result + '\'' +
                ", loanMoney=" + loanMoney +
                ", loanRate=" + loanRate +
                ", loanNumber=" + loanNumber +
                ", loanUnit='" + loanUnit + '\'' +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", desc='" + desc + '\'' +
                ", score=" + score +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                '}';
    }
}