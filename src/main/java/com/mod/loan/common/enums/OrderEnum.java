package com.mod.loan.common.enums;

public enum OrderEnum {
        DAI_FUKUAN(11, "待付款"),
        DAI_FAHUO(21, "待发货"),
        DAI_SHOUHUO(31, "待收货"),
        JIAOYI_WANCHENG(41, "交易成功"),
        JIAOYI_QUXIAO(51, "交易取消"),

        ;

        private Integer code;
        private String desc;

    OrderEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static String getDesc(Integer code) {
            for (OrderEnum status : OrderEnum.values()) {
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
