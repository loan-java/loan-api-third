package com.mod.loan.util;

/**
 * loan-pay 2019/4/22 huijin.shuailijie Init
 */
public class ConstantUtils {
    public final static String Y_FLAG = "Y";
    public final static String OK = "OK";
    public final static String FAIL = "FAIL";
    public final static Integer ZERO = 0;
    public final static Integer ONE = 1;

    public final static Integer newOrderStatus = 11;//新建订单
    public final static Integer agreeOrderStatus = 22;//通过订单
    public final static Integer unsettledOrderStatus = 12;//人工审核订单
    /**
     * 订单状态：待放款
     */
    public final static Integer ORDER_FOR_LENDING = 21;
    public final static Integer rejectOrderStatus = 51;//风控自动审核订单失败
    public final static String JSON_TYPE = "json";

}
