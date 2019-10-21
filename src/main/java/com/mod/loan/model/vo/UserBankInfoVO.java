package com.mod.loan.model.vo;

import lombok.Data;

@Data
public class UserBankInfoVO {


    /**
     * 用户Id
     */
    private Long uid;

    /**
     * 银行代码
     */
    private String cardCode;

    /**
     * 银行名称
     */
    private String cardName;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 预留手机号
     */
    private String cardPhone;

    /**
     * 第三方标识
     */
    private String foreignId;

    private String remark;

    private Integer bindType;


    /**
     * 预签约唯一码
     * 加密方式:Base64转码后，使用数字信封指定的方式和密钥加密
     */
    private String unique_code;


    //外部跟踪编号
    private String externalRefNumber;


    //安全校验值
    private String token;

}