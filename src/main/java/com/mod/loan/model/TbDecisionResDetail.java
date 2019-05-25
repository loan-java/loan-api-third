package com.mod.loan.model;

import com.mod.loan.model.dto.DecisionResDetailDTO;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.FetchType.LAZY;

@Table(name = "tb_decision_res_detail")
public class TbDecisionResDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "decision_no")
    private String decisionNo;

    @Column(name = "trans_id")
    private String transId;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_money")
    private Long orderMoney;

    @Column(name = "fee")
    private Boolean fee;

    @Lob
    @Basic(fetch = LAZY)
    @Column(name = "custom_grade")
    private String customGrade;

    @Column(name = "code")
    private String code;

    @Column(name = "descs")
    private String descs;

    @Column(name = "res_score")
    private Double resScore;

    @Lob
    @Basic(fetch = LAZY)
    @Column(name = "strategies")
    private String strategies;

    @Column(name = "create_time")
    private Date createtime;

    @Column(name = "update_time")
    private Date updatetime;


    /**
     * @return decision_no
     */
    public String getDecisionNo() {
        return decisionNo;
    }

    /**
     * @param decisionNo
     */
    public void setDecisionNo(String decisionNo) {
        this.decisionNo = decisionNo;
    }

    /**
     * @return trans_id
     */
    public String getTransId() {
        return transId;
    }

    /**
     * @param transId
     */
    public void setTransId(String transId) {
        this.transId = transId;
    }

    /**
     * @return orderStatus
     */
    public String getOrderstatus() {
        return orderStatus;
    }

    /**
     * @param orderStatus
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * @return order_money
     */
    public Long getOrderMoney() {
        return orderMoney;
    }

    /**
     * @param orderMoney
     */
    public void setOrderMoney(Long orderMoney) {
        this.orderMoney = orderMoney;
    }

    /**
     * @return fee
     */
    public Boolean getFee() {
        return fee;
    }

    /**
     * @param fee
     */
    public void setFee(Boolean fee) {
        this.fee = fee;
    }

    /**
     * @return custom_grade
     */
    public String getCustomGrade() {
        return customGrade;
    }

    /**
     * @param customGrade
     */
    public void setCustomGrade(String customGrade) {
        this.customGrade = customGrade;
    }

    /**
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return desc
     */
    public String getDescs() {
        return descs;
    }

    /**
     * @param descs
     */
    public void setDescs(String descs) {
        this.descs = descs;
    }

    /**
     * @return resScore
     */
    public Double getResScore() {
        return resScore;
    }

    /**
     * @param resScore
     */
    public void setResScore(Double resScore) {
        this.resScore = resScore;
    }

    /**
     * @return strategies
     */
    public String getStrategies() {
        return strategies;
    }

    /**
     * @param strategies
     */
    public void setStrategies(String strategies) {
        this.strategies = strategies;
    }

    /**
     * @return createTime
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * @return updateTime
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * @param updatetime
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public TbDecisionResDetail(DecisionResDetailDTO decisionResDetailDTO) {
        this.decisionNo = decisionResDetailDTO.getDecision_no();
        this.transId = decisionResDetailDTO.getTrans_id();
        this.orderStatus = decisionResDetailDTO.getOrderStatus();
        this.orderMoney = decisionResDetailDTO.getOrder_money();
        this.fee = decisionResDetailDTO.getFee();
        this.customGrade = decisionResDetailDTO.getCustom_grade() != null ? decisionResDetailDTO.getCustom_grade().toString() : null;
        this.code = decisionResDetailDTO.getCode();
        this.descs = decisionResDetailDTO.getDesc();
        this.resScore = decisionResDetailDTO.getResScore();
        this.strategies = decisionResDetailDTO.getStrategies() != null ? decisionResDetailDTO.getStrategies().toString() : null;
        this.setUpdatetime(new Date());
    }


    public TbDecisionResDetail(Long order_id, String orderNo, String decisionNo, String transId, String orderStatus) {
        this.setOrderId(order_id);
        this.setDecisionNo(orderNo);
        this.setDecisionNo(decisionNo);
        this.setTransId(transId);
        this.setOrderStatus(orderStatus);
        this.setCreatetime(new Date());
        this.setUpdatetime(new Date());
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
}