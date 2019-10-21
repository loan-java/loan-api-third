//package com.mod.loan.controller.whole.store;
//
//import com.mod.loan.common.annotation.LoginRequired;
//import com.mod.loan.common.enums.OrderEnum;
//import com.mod.loan.common.enums.ResponseEnum;
//import com.mod.loan.common.model.RequestThread;
//import com.mod.loan.common.model.ResultMessage;
//import com.mod.loan.config.Constant;
//import com.mod.loan.config.redis.RedisConst;
//import com.mod.loan.config.redis.RedisMapper;
//import com.mod.loan.model.Product;
//import com.mod.loan.model.ProductOrder;
//import com.mod.loan.model.UserAddress;
//import com.mod.loan.model.dto.OrderDTO;
//import com.mod.loan.service.ProductOrderService;
//import com.mod.loan.service.ProductService;
//import com.mod.loan.service.ProductSnapService;
//import com.mod.loan.service.UserAddressService;
//import com.mod.loan.util.TimeUtils;
//import org.joda.time.DateTime;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//@CrossOrigin("*")
//@RestController
//@RequestMapping("product_order")
//public class ProductOrderController {
//	private static Logger logger = LoggerFactory.getLogger(ProductOrderController.class);
//	@Autowired
//    RedisMapper redisMapper;
//	@Autowired
//	ProductService productService;
//	@Autowired
//	ProductSnapService productSnapService;
//	@Autowired
//	ProductOrderService productOrderService;
//	@Autowired
//    UserAddressService userAddressService;
//
//	/**
//	 * 订单信息确认
//	 *
//	 * @return
//	 */
//	@LoginRequired(check = true)
//	@RequestMapping(value = "order_confirm", method = { RequestMethod.POST })
//	public ResultMessage order_confirm(@RequestParam(required = true) Long productId) {
//		Product product = productService.selectByPrimaryKey(productId);
//		if (product == null || product.getStatus() == 0) {
//			return new ResultMessage(ResponseEnum.M6001);
//		}
//		Map<String, Object> data = productOrderService.orderConfirm(product);
//		return new ResultMessage(ResponseEnum.M2000, data);
//	}
//
//    /**
//     * 商城提交订单
//     * @return
//     */
//	@LoginRequired(check = true)
//    @RequestMapping(value = "order_submit", method = { RequestMethod.POST })
//    public ResultMessage order_submit(@RequestParam(required = true) Long productId, @RequestParam(required = true) Long userAddressId, ProductOrder productOrder) {
//		Product product = productService.selectByPrimaryKey(productId);
//		if (product == null || product.getStatus() == 0) {
//			return new ResultMessage(ResponseEnum.M6001);
//		}
//		UserAddress userAddress = userAddressService.selectByPrimaryKey(userAddressId);
//		if (userAddress == null || userAddress.getStatus() == 0 || !RequestThread.getUid().equals(userAddress.getUid())) {
//			return new ResultMessage(ResponseEnum.M6002);
//		}
//		BigDecimal price = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getProductPrice();
//		if(!price.equals(productOrder.getProductAmount())) {
//			return new ResultMessage(ResponseEnum.M6003);
//		}
//		BigDecimal deliveryAmount = Constant.ENVIROMENT.equals("dev") ? new BigDecimal(0.01) : new BigDecimal(24);
//		if (!price.add(deliveryAmount).setScale(2, BigDecimal.ROUND_DOWN).equals(productOrder.getOrderAmount())) {
//			return new ResultMessage(ResponseEnum.M6003);
//		}
//		if (!redisMapper.lock(RedisConst.lock_user_product_order + RequestThread.getUid(), 5)) {
//			return new ResultMessage(ResponseEnum.M4005);
//		}
//		String orderNo = productOrderService.insertOrderSubmit(product, userAddress, productOrder);
//		redisMapper.remove(RedisConst.lock_user_product_order + RequestThread.getUid());
//		return new ResultMessage(ResponseEnum.M2000, orderNo);
//    }
//
//	/**
//	 * 商城订单记录
//	 * @return
//	 */
//	@LoginRequired(check = true)
//	@RequestMapping(value="order_records", method = { RequestMethod.POST })
//	public ResultMessage order_records(@RequestParam(required = false)Integer status) {
//		List<ProductOrder> productOrderList = productOrderService.findByUid(RequestThread.getUid(), status);
//		if (productOrderList.size() == 0) {
//			return new ResultMessage(ResponseEnum.M4000.getCode(), "暂无数据！");
//		}
//		List<OrderDTO> orderList = new ArrayList<OrderDTO>();
//		productOrderList.forEach(item -> {
//			OrderDTO orderDTO = new OrderDTO();
//			String orderStatus = OrderEnum.getDesc(item.getStatus());
//			orderDTO.setOrderAmount(item.getOrderAmount());
//			orderDTO.setDeliveryAmount(item.getDeliveryAmount());
//			orderDTO.setProductAmount(item.getProductAmount());
//			orderDTO.setOrderStatus(orderStatus);
//			orderDTO.setOrderId(item.getId());
//			orderDTO.setOrderNo(item.getOrderNo());
//
//			List<Product> productList = productService.findByOrderId(item.getId());
//			orderDTO.setProductList(productList);
//			orderList.add(orderDTO);
//		});
//		return new ResultMessage(ResponseEnum.M2000, orderList);
//	}
//
//	/**
//	 * 商城订单详情
//	 * @return
//	 */
//	@LoginRequired(check = true)
//	@RequestMapping(value="order_detail", method = { RequestMethod.POST })
//	public ResultMessage order_detail(@RequestParam(required = true)Long orderId) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		ProductOrder order = productOrderService.selectByPrimaryKey(orderId);
//		if (null == order) {
//			return new ResultMessage(ResponseEnum.M4000);
//		}
//		if (!order.getUid().equals(RequestThread.getUid())) {
//			return new ResultMessage(ResponseEnum.M4000.getCode(),"订单异常");
//		}
//
//	    if(OrderEnum.DAI_FAHUO.getCode() == order.getStatus()){
//			map.put("payFinishTime",new DateTime(order.getPayTime()).toString(TimeUtils.dateformat0));
//		}else if(OrderEnum.DAI_SHOUHUO.getCode() == order.getStatus()){
//			map.put("payFinishTime",new DateTime(order.getPayTime()).toString(TimeUtils.dateformat0));
//			map.put("deliveryTime", new DateTime(order.getDeliveryTime()).toString(TimeUtils.dateformat0));
//			map.put("deliveryName",order.getDeliveryName());
//			map.put("deliveryNo",order.getDeliveryNo());
//		}else if(OrderEnum.JIAOYI_WANCHENG.getCode() == order.getStatus()){
//			map.put("payFinishTime",new DateTime(order.getPayTime()).toString(TimeUtils.dateformat0));
//			map.put("deliveryTime", new DateTime(order.getDeliveryTime()).toString(TimeUtils.dateformat0));
//			map.put("deliveryName",order.getDeliveryName());
//			map.put("deliveryNo",order.getDeliveryNo());
//			map.put("receiveTime", new DateTime(order.getReceiveTime()).toString(TimeUtils.dateformat0));
//		}
//
//		map.put("orderStatus", OrderEnum.getDesc(order.getStatus()));
//		map.put("receiveName",order.getReceiveName());
//		map.put("receivePhone",order.getReceivePhone());
//		map.put("receiveDetail",order.getReceiveDetail());
//		map.put("orderNo",order.getOrderNo());
//		map.put("productAmount",order.getProductAmount());
//		map.put("deliveryAmount",order.getDeliveryAmount());
//		map.put("orderAmount",order.getOrderAmount());
//		map.put("remark",order.getRemark());
//		map.put("createTime", new DateTime(order.getCreateTime()).toString(TimeUtils.dateformat0));
//
//		List<Product> productList =  productService.findByOrderId(orderId);
//		map.put("productList",productList);
//
//		return new ResultMessage(ResponseEnum.M2000,map);
//	}
//
//	/**
//	 * 取消订单/确认收货
//	 */
//	@LoginRequired(check = true)
//	@RequestMapping(value="order_change", method = { RequestMethod.POST })
//	public ResultMessage order_change(@RequestParam(required = true)Long orderId, @RequestParam(required = true)Integer status) {
//		ProductOrder order = productOrderService.selectByPrimaryKey(orderId);
//		if (null == order) {
//			return new ResultMessage(ResponseEnum.M4000);
//		}
//		if (!order.getUid().equals(RequestThread.getUid())) {
//			return new ResultMessage(ResponseEnum.M4000.getCode(),"订单异常");
//		}
//		if(OrderEnum.DAI_FUKUAN.getCode() == order.getStatus() && OrderEnum.JIAOYI_QUXIAO.getCode() == status){  //付款中变为交易取消
//			ProductOrder order1 = new ProductOrder();
//			order1.setId(orderId);
//			order1.setStatus(OrderEnum.JIAOYI_QUXIAO.getCode());
//			productOrderService.updateByPrimaryKeySelective(order1);
//			return  new ResultMessage(ResponseEnum.M2000);
//		}
//		if(OrderEnum.DAI_SHOUHUO.getCode() == order.getStatus() && OrderEnum.JIAOYI_WANCHENG.getCode() == status){  //待发货变为交易成功（即确认收货）
//			ProductOrder order1 = new ProductOrder();
//			order1.setId(orderId);
//			order1.setStatus(OrderEnum.JIAOYI_WANCHENG.getCode());
//			order1.setReceiveTime(new Date());
//			productOrderService.updateByPrimaryKeySelective(order1);
//			return  new ResultMessage(ResponseEnum.M2000);
//		}
//		logger.info("订单{}状态异常",orderId);
//		return new ResultMessage(ResponseEnum.M4000.getCode(),"订单状态异常");
//	}
//}