//package com.mod.loan.controller.whole.store;
//
//import com.alipay.api.AlipayApiException;
//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;
//import com.alipay.api.domain.AlipayTradeWapPayModel;
//import com.alipay.api.internal.util.AlipaySignature;
//import com.alipay.api.request.AlipayTradeWapPayRequest;
//import com.mod.loan.common.annotation.LoginRequired;
//import com.mod.loan.common.enums.ResponseEnum;
//import com.mod.loan.common.model.ResultMessage;
//import com.mod.loan.config.Constant;
//import com.mod.loan.config.redis.RedisConst;
//import com.mod.loan.config.redis.RedisMapper;
//import com.mod.loan.model.Product;
//import com.mod.loan.model.ProductOrder;
//import com.mod.loan.model.ProductPay;
//import com.mod.loan.service.ProductOrderService;
//import com.mod.loan.service.ProductPayService;
//import com.mod.loan.service.ProductService;
//import com.mod.loan.util.StringUtil;
//import com.mod.loan.util.TimeUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.*;
//
//@CrossOrigin("*")
//@RestController
//@RequestMapping("product_pay")
//public class ProductPayAlipayController {
//
//	private static Logger logger = LoggerFactory.getLogger(ProductPayAlipayController.class);
//	@Autowired
//	private RedisMapper redisMapper;
//	@Autowired
//	private ProductService productService;
//	@Autowired
//	private ProductPayService productPayService;
//	@Autowired
//	private ProductOrderService productOrderService;
//
//	@LoginRequired(check = false)
//	@RequestMapping(value = "order_pay_alipay", method = { RequestMethod.GET })
//	public ResultMessage order_pay(HttpServletResponse response, @RequestParam String orderNo, @RequestParam BigDecimal orderAmount) {
//		ProductOrder productOrder = productOrderService.selectByOrderNo(orderNo);
//		if (productOrder == null || productOrder.getStatus() != 11 || !productOrder.getOrderAmount().equals(orderAmount)) {
//			return new ResultMessage(ResponseEnum.M6004);
//		}
//		List<Product> products = productService.findByOrderId(productOrder.getId());
//		if (products.size() == 0 || products.get(0).getStatus() == 0) {
//			return new ResultMessage(ResponseEnum.M6001);
//		}
//		if (!redisMapper.lock(RedisConst.lock_user_product_order_pay + productOrder.getId(), 5)) {
//			return new ResultMessage(ResponseEnum.M4005);
//		}
//		// 销售产品码 必填
//		String product_code = "QUICK_WAP_WAY";
//		// 商户订单号，商户网站订单系统中唯一订单号，必填
//		String out_trade_no = productOrder.getId() + StringUtil.getOrderNumber("");
//		// 订单名称，必填
//		String subject = products.get(0).getProductName();
//		// 付款金额，必填
//		String total_amount = productOrder.getOrderAmount().toString();
//		// 调用RSA签名方式
//		AlipayClient client = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", Constant.APPID, Constant.RSA_PRIVATE_KEY, "json", "UTF-8", Constant.ALIPAY_PUBLIC_KEY, "RSA2");
//
//		AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();
//		// 封装请求支付信息
//		AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
//		model.setOutTradeNo(out_trade_no);
//		model.setSubject(subject);
//		model.setTotalAmount(total_amount);
//		model.setProductCode(product_code);
//		alipay_request.setBizModel(model);
//		alipay_request.setNotifyUrl(Constant.NOTIFY_URL);
//		alipay_request.setReturnUrl(Constant.RETURN_URL);
//
//		ProductPay productPay = new ProductPay();
//		productPay.setPayNo(out_trade_no);
//		productPay.setOrderId(productOrder.getId());
//		productPay.setUid(productOrder.getUid());
//		productPay.setBuyerAmount(productOrder.getOrderAmount());
//		productPay.setPayWay(1);// 支付宝
//		productPay.setStatus(0);// 状态：初始
//		productPay.setCreateTime(new Date());
//		productPayService.insertSelective(productPay);
//		redisMapper.remove(RedisConst.lock_user_product_order_pay + productOrder.getId());
//
//		String form = "";
//		try {
//			// 调用SDK生成表单
//			form = client.pageExecute(alipay_request).getBody();
//			response.setContentType("text/html;charset=UTF-8");
//			// 直接将完整的表单html输出到页面
//			response.getOutputStream().write(form.getBytes());
//			response.getOutputStream().flush();
//			response.getOutputStream().close();
//		} catch (AlipayApiException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return new ResultMessage(ResponseEnum.M2000);
//	}
//
//	@LoginRequired(check = false)
//	@RequestMapping(value = "order_pay_alipay_notify", method = { RequestMethod.POST })
//	public void order_pay_alipay_notify(HttpServletRequest request, HttpServletResponse response) {
//		Map<String, String> params = new HashMap<String, String>();
//		Map<?, ?> requestParams = request.getParameterMap();
//		for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
//			String name = (String) iter.next();
//			String[] values = (String[]) requestParams.get(name);
//			String valueStr = "";
//			for (int i = 0; i < values.length; i++) {
//				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
//			}
//			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
//			params.put(name, valueStr);
//		}
//		try {
//			boolean verify_result = AlipaySignature.rsaCheckV1(params, Constant.ALIPAY_PUBLIC_KEY, "UTF-8", "RSA2");
//			if (verify_result) {
//				// 交易状态
//				String trade_status = params.get("trade_status");
//				// 商户付款流水号
//				String out_trade_no = params.get("out_trade_no");
//				// 支付宝交易号
//				String trade_no = params.get("trade_no");
//				// 买家支付账号
//				String buyer_logon_id = params.get("buyer_logon_id");
//				// 买家支付金额
//				String total_amount = params.get("total_amount");
//				// 卖家收款账号
//				String seller_email = params.get("seller_email");
//				// 付款时间
//				String gmt_payment = params.get("gmt_payment");
//				// app_id
//				String app_id = params.get("app_id");
//				if (!Constant.APPID.equals(app_id)) {
//					logger.error("商城系统，支付宝回调appid不匹配。回调参数={}", params);
//					return;
//				}
//				ProductPay productPay = productPayService.selectByPayNo(out_trade_no);
//				if (productPay == null) {
//					logger.error("商城系统，支付宝回调的订单支付流水为空。回调参数={}", params);
//					return;
//				}
//				ProductOrder productOrder = productOrderService.selectByPrimaryKey(productPay.getOrderId());
//				// 不是待付款的订单
//				if (productOrder.getStatus() != 11) {
//					logger.error("商城系统，支付宝回调的订单状态异常。回调参数={},productOrderNo={},productPayId={}", params, productOrder.getOrderNo(), productPay.getId());
//					return;
//				}
//				// 支付金额与订单金额不匹配的订单
//				if (!productPay.getBuyerAmount().equals(new BigDecimal(total_amount))) {
//					logger.error("商城系统，支付宝回调的订单金额异常。回调参数={},productOrderNo={},productPayId={}", params, productOrder.getOrderNo(), productPay.getId());
//					return;
//				}
//
//				if ("TRADE_SUCCESS".equals(trade_status)) {
//					productOrder.setStatus(21);// 状态：待发货
//					productOrder.setPayTime(TimeUtils.parseTime(gmt_payment, TimeUtils.dateformat1));
//					productPay.setOutPayNo(trade_no);
//					productPay.setBuyerAccount(buyer_logon_id);
//					productPay.setSellerAccount(seller_email);
//					productPay.setStatus(1); // 状态：已支付
//					productPay.setPayTime(TimeUtils.parseTime(gmt_payment, TimeUtils.dateformat1));
//					productOrderService.updateOrderAndPay(productOrder, productPay);
//				}
//				response.getOutputStream().write("success".getBytes());
//				response.getOutputStream().flush();
//				response.getOutputStream().close();
//			}
//		} catch (AlipayApiException e) {
//			logger.error("商城系统，支付宝回调验签异常。params={}", params);
//			logger.error("异常信息", e);
//		} catch (IOException e) {
//			logger.error("异常信息", e);
//		}
//	}
//}
