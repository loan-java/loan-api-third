package com.mod.loan.common.enums;

/**
 * @author yutian
 */
public enum RiskAuditSourceEnum {
    /**
     * 风控审核来源
     */
    JU_HE(0, "聚合"),
    RONG_ZE(1, "融泽"),
    ;

    private Integer code;
    private String message;

    RiskAuditSourceEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
