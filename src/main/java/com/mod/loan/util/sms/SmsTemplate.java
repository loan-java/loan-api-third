package com.mod.loan.util.sms;

public enum SmsTemplate {
    T001("001"),//注册短信
    T002("002"),//找回密码提示
    T005("005"),//催收提示
    ;
    private String key;  //自有模板key

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    SmsTemplate(String key) {
        this.key = key;
    }

    public static SmsTemplate getTemplate(String key) {
        for (SmsTemplate enumYunpianApikey : SmsTemplate.values()) {
            if (enumYunpianApikey.getKey().equals(key)) {
                return enumYunpianApikey;
            }
        }
        return null;
    }
}
