package com.mod.loan.util.sms;

public enum EnumSmsTemplate {
	T1001("1001","您的验证码为%s，请于%s内正确输入，如非本人操作，请忽略此短信。"),
	T1002("1002","您的验证码为%s，请于%s内正确输入，如非本人操作，请忽略此短信。"),

	T2001("2001","您的手机已确认回收 ，%s元回收款已打至您的收款账户，请查收！请于%s前及时回购！"),
	T2002("2002","您的手机租赁已到期，请登录app查看！"),
	T2003("2003","您的手机租赁即将到期，请登录app查看！"),

	
	;
	private String key;  //自有模板key

    private String content; //模板内容
    
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	private EnumSmsTemplate(String key, String content) {
		this.key = key;
		this.content = content;
	}

	public static EnumSmsTemplate getTemplate(String key) {
        for (EnumSmsTemplate enumYunpianApikey : EnumSmsTemplate.values()) {
        	   if (enumYunpianApikey.getKey().equals(key)) {
        		   return enumYunpianApikey;
			   }        
        }
        return null;
    }
}
