package com.mod.loan.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_bank")
public class Bank {
    @Id
    private String code;

    /**
     * 银行名称
     */
    @Column(name = "bank_name")
    private String bankName;

    /**
     * 银行图标
     */
    @Column(name = "bank_imgurl")
    private String bankImgurl;

    /**
     * 单笔交易限额
     */
    @Column(name = "money_unit_limit")
    private BigDecimal moneyUnitLimit;

    /**
     * 单日限额
     */
    @Column(name = "money_day_limit")
    private BigDecimal moneyDayLimit;

    private Integer idx;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 0-关闭，1-启用
     */
    @Column(name = "bank_status")
    private Integer bankStatus;

    /**
     * 合利宝银行代码code
     */
    @Column(name = "code_helipay")
    private String codeHelipay;

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
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取银行名称
     *
     * @return bank_name - 银行名称
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * 设置银行名称
     *
     * @param bankName 银行名称
     */
    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    /**
     * 获取银行图标
     *
     * @return bank_imgurl - 银行图标
     */
    public String getBankImgurl() {
        return bankImgurl;
    }

    /**
     * 设置银行图标
     *
     * @param bankImgurl 银行图标
     */
    public void setBankImgurl(String bankImgurl) {
        this.bankImgurl = bankImgurl == null ? null : bankImgurl.trim();
    }

    /**
     * 获取单笔交易限额
     *
     * @return money_unit_limit - 单笔交易限额
     */
    public BigDecimal getMoneyUnitLimit() {
        return moneyUnitLimit;
    }

    /**
     * 设置单笔交易限额
     *
     * @param moneyUnitLimit 单笔交易限额
     */
    public void setMoneyUnitLimit(BigDecimal moneyUnitLimit) {
        this.moneyUnitLimit = moneyUnitLimit;
    }

    /**
     * 获取单日限额
     *
     * @return money_day_limit - 单日限额
     */
    public BigDecimal getMoneyDayLimit() {
        return moneyDayLimit;
    }

    /**
     * 设置单日限额
     *
     * @param moneyDayLimit 单日限额
     */
    public void setMoneyDayLimit(BigDecimal moneyDayLimit) {
        this.moneyDayLimit = moneyDayLimit;
    }

    /**
     * @return idx
     */
    public Integer getIdx() {
        return idx;
    }

    /**
     * @param idx
     */
    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取0-关闭，1-启用
     *
     * @return bank_status - 0-关闭，1-启用
     */
    public Integer getBankStatus() {
        return bankStatus;
    }

    /**
     * 设置0-关闭，1-启用
     *
     * @param bankStatus 0-关闭，1-启用
     */
    public void setBankStatus(Integer bankStatus) {
        this.bankStatus = bankStatus;
    }

    /**
     * 获取合利宝银行代码code
     *
     * @return code_helipay - 合利宝银行代码code
     */
    public String getCodeHelipay() {
        return codeHelipay;
    }

    /**
     * 设置合利宝银行代码code
     *
     * @param codeHelipay 合利宝银行代码code
     */
    public void setCodeHelipay(String codeHelipay) {
        this.codeHelipay = codeHelipay == null ? null : codeHelipay.trim();
    }

}