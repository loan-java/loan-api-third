package com.mod.loan.common.message;

/**
 * 还款结果队列的DTO
 */
public class OrderRepayQueryMessage {

    /**
     * 还款流水号
     */
    private String repayNo;
    /**
     * 商户别名
     */
    private String merchantAlias;
    /**
     * 查询次数
     */
    private int times;

    /**
     * 还款类型[1: 主动还款, 2: 自动扣款]
     */
    private int repayType;

    public OrderRepayQueryMessage() {
        super();
    }

    public OrderRepayQueryMessage(String repayNo, String merchantAlias, int repayType) {
        super();
        this.repayNo = repayNo;
        this.merchantAlias = merchantAlias;
        this.repayType = repayType;
    }


    public String getMerchantAlias() {
        return merchantAlias;
    }

    public void setMerchantAlias(String merchantAlias) {
        this.merchantAlias = merchantAlias;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getRepayType() {
        return repayType;
    }

    public void setRepayType(int repayType) {
        this.repayType = repayType;
    }

    public String getRepayNo() {
        return repayNo;
    }

    public void setRepayNo(String repayNo) {
        this.repayNo = repayNo;
    }
}
