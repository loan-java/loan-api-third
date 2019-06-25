package com.mod.loan.util.yeepay;

import lombok.Data;

/**
 * @author yutian
 */
@Data
public class StringResultDTO {
    /**
     * 业务请求号 入参回传
     */
    private String requestno;
    /**
     * 商户编号 入参回传
     */
    private String merchantno;
    /**
     * 易宝流水号
     */
    private String yborderid;
    /**
     * 是否发送短验
     */
    private String issms;
    /**
     * 银行编码
     */
    private String bankcode;
    /**
     * 短验码
     */
    private String smscode;
    /**
     * 短验发送方 枚举
     * YEEPAY： 易宝发送
     * BANK： 银行发送
     */
    private String codesender;
    /**
     * 实际短验发送类型 枚举
     * VOICE： 语音
     * MESSAGE： 短信
     */
    private String smstype;
    /**
     * 鉴权类型
     */
    private String authtype;
    /**
     * 卡号前六位
     */
    private String cardtop;
    /**
     * 卡号后四位
     */
    private String cardlast;
    /**
     * 订单状态
     * 枚举：
     * BIND_SUCCESS ： 绑卡成功
     * TO_VALIDATE： 待短验
     * BIND_FAIL： 绑卡失败
     * BIND_ERROR： 绑卡异常(可重试)
     * TIME_OUT： 超时失败
     * FAIL： 系统异常
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 错误码
     */
    private String errorcode;
    /**
     * 错误信息
     */
    private String errormsg;
}
