//package com.mod.loan.controller.whole.h5;
//
//import com.alibaba.fastjson.JSONObject;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.mod.loan.common.enums.ResponseEnum;
//import com.mod.loan.common.model.ResultMessage;
//import com.mod.loan.config.redis.RedisConst;
//import com.mod.loan.config.redis.RedisMapper;
//import com.mod.loan.mapper.AppArticleMapper;
//import com.mod.loan.model.*;
//import com.mod.loan.service.HelpService;
//import com.mod.loan.service.MerchantService;
//import com.mod.loan.service.OrderService;
//import com.mod.loan.service.UserService;
//import com.mod.loan.util.StringReplaceUtil;
//import com.mod.loan.util.TimeUtils;
//import org.apache.commons.lang.StringUtils;
//import org.joda.time.DateTime;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 帮助中心
// *
// * @author wugy 2018年5月3日 下午9:31:06
// */
//@CrossOrigin("*")
//@RestController
//public class HelpController {
//
//	@Autowired
//	HelpService helpService;
//	@Autowired
//    RedisMapper redisMapper;
//	@Autowired
//	AppArticleMapper articleMapper;
//	@Autowired
//    MerchantService merchantService;
//	@Autowired
//    OrderService orderService;
//	@Autowired
//    UserService userService;
//
//	@RequestMapping(value = "question_list")
//	public ResultMessage question_list(String merchant) {
//		if (StringUtils.isBlank(merchant)) {
//			return new ResultMessage(ResponseEnum.M4000);
//		}
//		Map<String, List<JSONObject>> data = redisMapper.get(RedisConst.app_question_list+merchant,
//				new TypeReference<Map<String, List<JSONObject>>>() {
//				});
//		if (data == null) {
//			data = helpService.findQuestionList(merchant);
//			if (data != null) {
//				redisMapper.set(RedisConst.app_question_list+merchant, data, 600);
//			}
//		}
//		return new ResultMessage(ResponseEnum.M2000, data);
//	}
//
//	@RequestMapping(value = "article_detail")
//	public ResultMessage article_detail(Long id) {
//		if (id == null) {
//			return new ResultMessage(ResponseEnum.M4000);
//		}
//		AppArticle article = redisMapper.get(RedisConst.app_article+id, new TypeReference<AppArticle>() {
//		});
//		if (article == null) {
//			article = articleMapper.selectByPrimaryKey(id);
//			if (article!=null) {
//				redisMapper.set(RedisConst.app_article+id, article, 300);
//			}else {
//				return new ResultMessage(ResponseEnum.M4000);
//			}
//		}
//		Map<String, String> map = new HashMap<>();
//		map.put("title", article.getArticleTitle());
//		map.put("date", new DateTime(article.getUpdateTime()).toString(TimeUtils.dateformat2));
//		map.put("content", article.getArticleContent());
//		return new ResultMessage(ResponseEnum.M2000, map);
//	}
//
//	@RequestMapping(value = "order_agreement")
//	public ResultMessage order_agreement(Long orderId) {
//		Map<String, String> data = new HashMap<>();
//		if (orderId != null) {
//			Order order = orderService.selectByPrimaryKey(orderId);
//			OrderPhone phone = orderService.findOrderPhoneByOrderId(orderId);
//			User user = userService.selectByPrimaryKey(order.getUid());
//			Merchant merchant = merchantService.findMerchantByAlias(order.getMerchant());
//			data.put("app", merchant.getMerchantApp());// app名称
//			data.put("company", merchant.getMerchantName());// 公司名
//			data.put("alias", merchant.getMerchantAlias());// 别名
//
//			data.put("phoneModel", phone.getPhoneModel());// 手机型号
//			data.put("paramValue", phone.getParamValue());// 选择参数
//
//			data.put("certNo", StringReplaceUtil.idCardReplaceWithStar(user.getUserCertNo()));
//			data.put("userName", StringReplaceUtil.userNameReplaceWithStar(user.getUserName()));
//
//			data.put("money", order.getActualMoney().toString());// 回收金额
//			data.put("moneyLimit", order.getBorrowMoney().toString());// 回收金额
//			data.put("day", order.getBorrowDay().toString());// 租赁期限
//			data.put("createTime", new DateTime(order.getCreateTime()).toString("yyyy年MM月dd日"));// 签订日期
//			data.put("overdueRate", order.getOverdueRate().toString());// 预期率
//		}
//		return new ResultMessage(ResponseEnum.M2000, data);
//	}
//}