package com.mod.loan.config.rabbitmq;

public class RabbitConst {
    /**
     * 短信队列
     */
    public final static String queue_sms = "queue_sms";
    /**
     * 风控订单审核通知
     */
    public final static String qjld_queue_risk_order_notify = "qjld_queue_risk_order_notify";


    public final static String pb_queue_risk_order_notify = "pb_queue_risk_order_notify"; // 盘宝风控订单审核通知

    public final static String zm_queue_risk_order_notify = "zm_queue_risk_order_notify"; // 指谜风控订单审核通知

    /**
     * 还款结果查询
     */
    public final static String baofoo_queue_repay_order_query = "baofoo_queue_repay_order_query";

    public final static String kuaiqian_queue_repay_order_query = "kuaiqian_queue_repay_order_query";

    public final static String chanpay_queue_repay_order_query = "chanpay_queue_repay_order_query";

    public final static String yeepay_queue_repay_order_query = "yeepay_queue_repay_order_query";
}
