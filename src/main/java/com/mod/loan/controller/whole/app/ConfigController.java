//package com.mod.loan.controller.whole.app;
//
//import com.mod.loan.common.annotation.Api;
//import com.mod.loan.common.enums.ResponseEnum;
//import com.mod.loan.common.model.RequestThread;
//import com.mod.loan.common.model.ResultMessage;
//import com.mod.loan.model.AppStartup;
//import com.mod.loan.model.Merchant;
//import com.mod.loan.service.AppService;
//import com.mod.loan.service.MerchantService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@CrossOrigin("*")
//@RestController
//public class ConfigController {
//
//	@Autowired
//	private AppService appService;
//	@Autowired
//	private MerchantService merchantService;
//
//	/**
//	 * 启动页，首页图片弹窗
//	 * @return
//	 */
//	@RequestMapping(value = "app_home")
//	@Api
//	public ResultMessage app_home() {
//		Map<String, Object> data = new HashMap<>();
//		Map<String, Object> map = new HashMap<>();
//		AppStartup startup = appService.findAppStartupByMerchant(RequestThread.getClientAlias());
//		if(null != startup){
//			map.put("imgurl",startup.getAdImgurl());
//			map.put("url",startup.getAdUrl());
//		}
//		data.put("startup", map);
//		data.put("home", appService.findLatestHomeByMerchant(RequestThread.getClientAlias()));
//		return new ResultMessage(ResponseEnum.M2000, data);
//	}
//
//	/**
//	 * 客户端公告，轮播图，轮番图，关键字接口
//	 * @return
//	 */
//	@RequestMapping(value = "app_index")
//	@Api
//	public ResultMessage app_index() {
//		Map<String, Object> data=new HashMap<>();
//		data.put("notice", appService.findNoticeListByMerchant(RequestThread.getClientAlias()));
//		data.put("banner", appService.findBannerListByMerchant(RequestThread.getClientAlias()));
//		data.put("entry",appService.findEntryList(RequestThread.getClientAlias()));
//		data.put("keyword","iPad|小米SE8|男装|面膜");//搜索关键字
//		return new ResultMessage(ResponseEnum.M2000, data);
//	}
//
//	@RequestMapping(value = "check_version")
//	@Api
//	public ResultMessage check_version() {
//		return new ResultMessage(ResponseEnum.M2000, appService.findNewVersion(RequestThread.getClientAlias(), RequestThread.getClientType()));
//	}
//
//	@RequestMapping(value = "mechant_info")
//	@Api
//	public ResultMessage mechant_info() {
//		Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
//		Map<String, String> data=new HashMap<>();
//		data.put("alias", merchant.getMerchantAlias());
//		data.put("company", merchant.getMerchantName());
//		if ("ios".equals(RequestThread.getClientType())) {
//			data.put("app", merchant.getMerchantAppIos());
//		}else {
//			data.put("app", merchant.getMerchantApp());
//		}
//		return new ResultMessage(ResponseEnum.M2000, data);
//	}
//}