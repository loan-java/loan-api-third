package com.mod.loan.common.enums;

/**
 * 聚合回调枚举
 *
 * @author kk
 */
public enum JuHeCallBackEnum {

    /**
     * 聚合回调枚举
     */
    APPROVE("审核中", JuHeOrderStatusEnum.APPROVE, OrderTypeEnum.JK, PayStatusEnum.NOTPAY, RepayStatusEnum.NOT_REPAY),
    TO_PAY("放款中", JuHeOrderStatusEnum.TO_PAY, OrderTypeEnum.JK, PayStatusEnum.NOTPAY, RepayStatusEnum.NOT_REPAY),
    PAYED("已放款", JuHeOrderStatusEnum.PAYED, OrderTypeEnum.JK, PayStatusEnum.PAYED, RepayStatusEnum.NOT_REPAY),
    REPAYED("已还款", JuHeOrderStatusEnum.REPAYED, OrderTypeEnum.HK, PayStatusEnum.PAYED, RepayStatusEnum.REPAYED),
    REPAY_FAILED("还款失败", JuHeOrderStatusEnum.REPAY_FAILED, OrderTypeEnum.HK, PayStatusEnum.PAYED, RepayStatusEnum.REPAY_FAILED),
    OVERDUE("逾期", JuHeOrderStatusEnum.OVERDUE, OrderTypeEnum.JK, PayStatusEnum.NOTPAY, RepayStatusEnum.NOT_REPAY),
    PAY_FAILED("放款失败", JuHeOrderStatusEnum.PAY_FAILED, OrderTypeEnum.JK, PayStatusEnum.NOTPAY, RepayStatusEnum.NOT_REPAY),
    AUDIT_REFUSE("审核失败", JuHeOrderStatusEnum.AUDIT_REFUSE, OrderTypeEnum.JK, PayStatusEnum.NOTPAY, RepayStatusEnum.NOT_REPAY),
    REPAYING("还款中", JuHeOrderStatusEnum.REPAYING, OrderTypeEnum.HK, PayStatusEnum.NOTPAY, RepayStatusEnum.REPAYING),
    WAIT_PAY("待放款", JuHeOrderStatusEnum.WAIT_PAY, OrderTypeEnum.JK, PayStatusEnum.NOTPAY, RepayStatusEnum.NOT_REPAY),
    REFUSE_PAY("拒绝放款", JuHeOrderStatusEnum.REFUSE_PAY, OrderTypeEnum.JK, PayStatusEnum.NOTPAY, RepayStatusEnum.NOT_REPAY),
    WITHHOLD("自动扣款", JuHeOrderStatusEnum.REPAYED, OrderTypeEnum.AUTHK, PayStatusEnum.PAYED, RepayStatusEnum.REPAYED);

    private String msg;
    private JuHeOrderStatusEnum orderStatusEnum;
    private OrderTypeEnum orderTypeEnum;
    private PayStatusEnum payStatusEnum;
    private RepayStatusEnum repayStatusEnum;

    JuHeCallBackEnum(String msg,
                     JuHeOrderStatusEnum orderStatusEnum,
                     OrderTypeEnum orderTypeEnum,
                     PayStatusEnum payStatusEnum,
                     RepayStatusEnum repayStatusEnum) {
        this.msg = msg;
        this.orderStatusEnum = orderStatusEnum;
        this.orderTypeEnum = orderTypeEnum;
        this.payStatusEnum = payStatusEnum;
        this.repayStatusEnum = repayStatusEnum;
    }

    public JuHeOrderStatusEnum getOrderStatusEnum() {
        return orderStatusEnum;
    }

    public OrderTypeEnum getOrderTypeEnum() {
        return orderTypeEnum;
    }

    public PayStatusEnum getPayStatusEnum() {
        return payStatusEnum;
    }

    public RepayStatusEnum getRepayStatusEnum() {
        return repayStatusEnum;
    }
}
