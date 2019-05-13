//package com.mod.loan.controller.order;
//
//import com.alibaba.fastjson.JSONObject;
//import com.fuiou.mpay.encrypt.DESCoderFUIOU;
//import com.mod.loan.common.annotation.LoginRequired;
//import com.mod.loan.common.enums.ResponseEnum;
//import com.mod.loan.common.model.RequestThread;
//import com.mod.loan.common.model.ResultMessage;
//import com.mod.loan.config.Constant;
//import com.mod.loan.model.Merchant;
//import com.mod.loan.model.Order;
//import com.mod.loan.model.OrderRepay;
//import com.mod.loan.model.User;
//import com.mod.loan.service.*;
//import com.mod.loan.util.MD5;
//import com.mod.loan.util.StringUtil;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@CrossOrigin("*")
//@RestController
//@RequestMapping("order")
//public class FuyouRepayController {
//
//	private static Logger logger = LoggerFactory.getLogger(FuyouRepayController.class);
//	@Autowired
//	OrderService orderService;
//	@Autowired
//	OrderRepayService orderRepayService;
//	@Autowired
//	UserService userService;
//	@Autowired
//	UserBankService userBankService;
//	@Autowired
//	MerchantService merchantService;
//
//	/**
//	 * 查询商户支持的支付通道
//	 */
//	@LoginRequired(check = true)
//	@RequestMapping(value = "repay_channel")
//	public ResultMessage repay_channel() {
//		Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
//		if(null == merchant){
//			return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
//		}
//		JSONObject object = JSONObject.parseObject(merchant.getMerchantChannel());
//		return  new ResultMessage(ResponseEnum.M2000,object );
//	}
//
//		/**
//         * 富友支付还款
//         */
//	@LoginRequired(check = true)
//	@RequestMapping(value = "order_repay_fuyou")
//	public ResultMessage order_repay_fuyou( @RequestParam(required = true) Long orderId,@RequestParam(required = true)String cardNo,
//										   @RequestParam(required = true)String cardName)throws IOException {
//		ResultMessage message = null;
//		Long uid = RequestThread.getUid();
//		if(StringUtils.isBlank(cardNo)|| StringUtils.isBlank(cardName)){
//			return new ResultMessage(ResponseEnum.M4000.getCode(), "请输入正确的卡号");
//		}
//		if(orderId == null) {
//			logger.info("订单异常，uid={},订单号={}", uid,orderId);
//			return new ResultMessage(ResponseEnum.M4000.getCode(), "订单不存在");
//		}
//		Order order = orderService.selectByPrimaryKey(orderId);
//		if (!order.getUid().equals(uid)) {
//			logger.info("订单异常，订单号为：{}", order.getId());
//			return new ResultMessage(ResponseEnum.M4000.getCode(), "订单异常");
//		}
//		if (order.getStatus() != 31 && order.getStatus() != 33 && order.getStatus() != 34) { // 已放款，逾期，坏账状态
//			logger.info("订单非还款状态，订单号为：{}", order.getId());
//			return new ResultMessage(ResponseEnum.M4000.getCode(), "订单状态异常");
//		}
//		User user = userService.selectByPrimaryKey(uid);
//		Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
//		//以分为单位，取整
//		Long amount = new BigDecimal(100).multiply(order.getShouldRepay()).longValue();
//		if ("dev".equals(Constant.ENVIROMENT)) {
//			amount = 200L;//以分为单位
//		}
//		Map<String,String> param = new HashMap<String, String>();
//		try {
//			String userId = uid.toString();
//			String idType = "0";
//			String type = "10";
//			StringBuffer orderPlain = new StringBuffer();
//			String orderSeriesId = StringUtil.getOrderNumber("r");// 支付流水号
//			//回调接口
//			String backUrl = Constant.SERVER_API_URL + "order/order_fuyou_callback";
//			//支付成功跳转页面
//			String homeUrl = Constant.SERVER_H5_URL + "order/store_pay_return.html?orderId=" + order.getId();
//			//支付失败跳转页面（订单记录页面）
//			String returnUrl = Constant.SERVER_H5_URL + "order/store_order_history.html";
//			String signPlain = type+"|"+"2.0"+"|"+merchant.getFuyou_merid()+"|"+orderSeriesId+"|"+userId
//					+"|"+amount+"|"+cardNo+"|"+backUrl+"|"+user.getUserName()+"|"+user.getUserCertNo()+"|"+idType+"|"+"0"+"|"
//					+ homeUrl +"|"+returnUrl+"|"+merchant.getFuyou_h5key();
//			String sign= MD5.toMD5(signPlain);
//			orderPlain.append("<ORDER>")
//					.append("<VERSION>2.0</VERSION>")
//					.append("<LOGOTP>0</LOGOTP>")
//					.append("<MCHNTCD>").append(merchant.getFuyou_merid()).append("</MCHNTCD>")
//					.append("<TYPE>").append(type).append("</TYPE>")
//					.append("<MCHNTORDERID>").append(orderSeriesId).append("</MCHNTORDERID>")
//					.append("<USERID>").append(userId).append("</USERID>")
//					.append("<AMT>").append(amount).append("</AMT>")
//					.append("<BANKCARD>").append(cardNo).append("</BANKCARD>")
//					.append("<BACKURL>").append(backUrl).append("</BACKURL>")
//					.append("<HOMEURL>").append(homeUrl).append("</HOMEURL>")
//					.append("<REURL>").append(returnUrl).append("</REURL>")
//					.append("<NAME>").append(user.getUserName()).append("</NAME>")
//					.append("<IDTYPE>").append(idType).append("</IDTYPE>")
//					.append("<IDNO>").append(user.getUserCertNo()).append("</IDNO>")
//					.append("<REM1>").append(userId).append("</REM1>")
//					.append("<REM2>").append(userId).append("</REM2>")
//					.append("<REM3>").append(userId).append("</REM3>")
//					.append("<SIGNTP>").append("md5").append("</SIGNTP>")
//					.append("<SIGN>").append(sign).append("</SIGN>")
//					.append("</ORDER>");
//			param.put("VERSION", "2.0");
//			param.put("ENCTP", "1");
//			param.put("LOGOTP", "0");
//			param.put("MCHNTCD", merchant.getFuyou_merid());
//			param.put("FM", DESCoderFUIOU.desEncrypt(orderPlain.toString(), DESCoderFUIOU.getKeyLength8(merchant.getFuyou_h5key())));
//			param.put("FUIOU_URL",Constant.FUIOU_PAY_URL);
//			// 还款记录表
//			OrderRepay orderRepay  = new OrderRepay();
//			orderRepay.setRepayNo(orderSeriesId);
//			orderRepay.setUid(order.getUid());
//			orderRepay.setOrderId(orderId);
//			orderRepay.setRepayType(5);
//			orderRepay.setRepayMoney(new BigDecimal(amount).divide(new BigDecimal(100)));
//			orderRepay.setBank(cardName);
//			orderRepay.setBankNo(cardNo);
//			orderRepay.setCreateTime(new Date());
//			orderRepay.setUpdateTime(new Date());
//			orderRepay.setRepayStatus(0);
//			orderRepayService.insertSelective(orderRepay);
//			message = new ResultMessage(ResponseEnum.M2000,param);
//		} catch (Exception e){
//			logger.info("富友支付异常。订单号为{}，卡号为{}，银行名称为{}",orderId,cardNo,cardName);
//			logger.error("富友支付异常",e);
//			message = new ResultMessage(ResponseEnum.M4000);
//		}
//		return message;
//	}
//
//	/**
//	 * 异步通知
//     * 回调的结果以http返回码是否是200来判断，
//     * 返回不是200，多次回调。最多10次，前3次每2分钟发，后几次每整点/半点发。
//	 */
//	@LoginRequired(check = false)
//	@RequestMapping(value = "order_fuyou_callback")
//	public void order_fuyou_callback(HttpServletRequest req) throws IOException {
//		String version = req.getParameter("VERSION");
//		String type = req.getParameter("TYPE");
//		String responseCode = req.getParameter("RESPONSECODE");
//		String responseMsg = req.getParameter("RESPONSEMSG");
//		String mchntCd = req.getParameter("MCHNTCD");
//		String mchntOrderId = req.getParameter("MCHNTORDERID");
//		String orderId = req.getParameter("ORDERID");////富友订单号
//		String bankCard = req.getParameter("BANKCARD");
//		String amt = req.getParameter("AMT");
//		String sign = req.getParameter("SIGN");
//		OrderRepay orderRepay = orderRepayService.selectByPrimaryKey(mchntOrderId);
//		if(null == orderRepay || 3 == orderRepay.getRepayStatus()){
//			logger.info("富友异步通知：流水不存在或已支付成功，还款订单流水为：{}，对应富友订单号为：{}",mchntOrderId,orderId);
//			return;
//		}
//		Order order = orderService.selectByPrimaryKey(orderRepay.getOrderId());
//		if(null == order || 41 == order.getStatus() || 42 == order.getStatus()){
//			logger.info("富友异步通知：订单不存在或已还，还款订单流水为：{}，对应富友订单号为：{}",mchntOrderId,orderId);
//			return;
//		}
//		Merchant merchant = merchantService.findMerchantByAlias(order.getMerchant());
//		// 校验签名
//		String signPain = new StringBuffer().append(type).append("|").append(version).append("|").append(responseCode)
//				.append("|").append(mchntCd).append("|").append(mchntOrderId).append("|").append(orderId).append("|")
//				.append(amt).append("|").append(bankCard).append("|").append(merchant.getFuyou_h5key()).toString();
//		if (!MD5.toMD5(signPain).equals(sign)) {
//			logger.info("富友异步通知验签失败，订单流水为：{}，对应富友订单号为：{}",mchntOrderId,orderId);
//			return;
//		}
//		if (!"0000".equals(responseCode)) {
//			logger.info("富友异步通知支付失败，订单流水为：{}，对应富友订单号为：{}，失败信息为：{}",mchntOrderId,orderId,responseMsg);
//			OrderRepay orderRepay1 = new OrderRepay();
//			orderRepay1.setRepayNo(mchntOrderId);
//			orderRepay1.setRepayStatus(2);
//			if(StringUtils.isNotBlank(responseMsg) && responseMsg.length() > 30){
//				responseMsg = responseMsg.substring(0,30);
//			}
//			orderRepay1.setRemark(responseMsg);
//			orderRepayService.updateByPrimaryKeySelective(orderRepay1);
//			return;
//		}
//		Order order1 = new Order();
//		order1.setId(orderRepay.getOrderId());
//		order1.setRealRepayTime(new Date());
//		order1.setHadRepay(order.getShouldRepay());
//		if (33 == order.getStatus() || 34 == order.getStatus()) {
//			order1.setStatus(42);
//		} else {
//			order1.setStatus(41);
//		}
//		OrderRepay orderRepay1 = new OrderRepay();
//		orderRepay1.setRepayNo(mchntOrderId);
//		orderRepay1.setUpdateTime(new Date());
//		orderRepay1.setRepayStatus(3);
//		orderRepayService.updateOrderRepayInfo(orderRepay1, order1);
//		return;
//	}
//
//}
