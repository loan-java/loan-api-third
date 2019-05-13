package com.mod.loan.common.enums;

public enum MerchantEnum {
    helibao(1, "合利宝"),
    fuyou(2, "富友"),
    huiju(3, "汇聚"),
    baofoo(4, "宝付"),
    kuaiqian(5, "快钱"),

    ;

    private Integer code;
    private String desc;

    MerchantEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        for (MerchantEnum status : MerchantEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}
