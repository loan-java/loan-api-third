package com.mod.loan.config.redis;

/**
 * 
 * @author wgy 2018年4月21日 上午10:22:40
 */
public interface RedisConst {
	String USER_TOKEN_PREFIX = "user_token:";
	String USER_PHONE_CODE = "user_phone_code:";
	String USER_GRAPH_CODE = "user_graph_code:";
	String USER_LOGIN = "user_login:";

	String app_notice = "app_notice:";
	String app_banner = "app_banner:";
	String app_startup = "app_startup:";
	String app_version = "app_version:";
	String app_entry = "app_entry:";
	String app_home = "app_home:";

	String lock_user_bind_card_code = "lock_user_bind_card_code:";
	String lock_user_bind_card = "lock_user_bind_card:";
	String lock_user_order = "lock_user_order:";

	String app_question_list = "app_question_list:";
	String app_article = "app_article:";

	String user_bank_bind = "user_bank_bind:";

	String repay_text = "repay_text:";
	String huiju_repay_text = "huiju_repay_text:";
	String huiju_repay_info = "huiju_repay_info:";

	String product = "store_product";
	String sowing_banners = "store_sowing_banners";
	String standard_banners = "store_standard_banners";
	String product_channel_detail = "store_product_channel_detail:";

	String lock_user_product_order = "lock_user_product_order:";
	String lock_user_product_order_pay = "lock_user_product_order_pay:";
}
