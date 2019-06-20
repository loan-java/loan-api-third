package com.mod.loan.common.enums;

public enum MerchantEnum {
    helibao(1, "", "合利宝"),
    fuyou(2, "", "富友"),
    huiju(3, "", "汇聚"),
    baofoo(4, "", "宝付"),
    kuaiqian(5, "", "快钱"),
    chanpay(6, "", "快捷"),
    yeepay(7, "", "易宝"),

    jishidai(-1, "jishidai", "及时贷"),
    huashidai(-1, "huashidai", "华时贷"),

    ;

    private Integer code;
    private String name;
    private String desc;

    MerchantEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static String getDescByName(String name) {
        for (MerchantEnum e : MerchantEnum.values()) {
            if (e.getName().equals(name)) {
                return e.getDesc();
            }
        }
        return "";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
