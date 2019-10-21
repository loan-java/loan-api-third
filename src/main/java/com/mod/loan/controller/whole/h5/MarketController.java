//package com.mod.loan.controller.whole.h5;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.mod.loan.common.enums.ResponseEnum;
//import com.mod.loan.common.model.ResultMessage;
//import com.mod.loan.config.redis.RedisMapper;
//import com.mod.loan.mapper.MarketChannelMapper;
//import com.mod.loan.mapper.MarketConfigMapper;
//import com.mod.loan.mapper.MarketProductMapper;
//import com.mod.loan.model.MarketChannel;
//import com.mod.loan.model.MarketConfig;
//import com.mod.loan.model.dto.MarketModuleDTO;
//import com.mod.loan.service.MarketService;
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
// * 贷款超市
// *  @author wugy
// *  2018年5月3日  下午9:31:40
// */
//@CrossOrigin("*")
//@RestController
//@RequestMapping(value = "market")
//public class MarketController {
//
//	@Autowired
//    RedisMapper redisMapper;
//	@Autowired
//	MarketService marketService;
//	@Autowired
//	MarketChannelMapper channelMapper;
//	@Autowired
//	MarketProductMapper productMapper;
//	@Autowired
//	MarketConfigMapper configMapper;
//
//	/**
//	 * 弃用
//	 * @return
//	 */
//	@RequestMapping(value = "channel_list")
//	public ResultMessage channel_list() {
//		List<MarketChannel> channelList = channelMapper.selectMarketChannelList();
//		return new ResultMessage(ResponseEnum.M2000, channelList);
//	}
//	/**
//	 * 弃用
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping(value = "list")
//	public ResultMessage list(Long id) {
//		if (id==null) {
//			return new ResultMessage(ResponseEnum.M4000);
//		}
//		List<MarketModuleDTO> list=redisMapper.get("market_channel:"+id, new TypeReference<List<MarketModuleDTO>>() {
//		});
//		if (list==null) {
//			list = marketService.findModuleListByChannel(id);
//			if (list!=null&&list.size()>0) {
//				redisMapper.set("market_channel:"+id, list, 60);
//			}
//		}
//		return new ResultMessage(ResponseEnum.M2000, list);
//	}
//
//	/**
//	 * 内部产品列表
//	 * @return
//	 */
//	@RequestMapping(value = "product_list")
//	public ResultMessage product_list() {
//		Integer type=1;
//		List<MarketModuleDTO> list=redisMapper.get("product_list:"+type, new TypeReference<List<MarketModuleDTO>>() {
//		});
//		if (list==null) {
//			list = marketService.findModuleProductList(type);
//			if (list!=null&&list.size()>0) {
//				redisMapper.set("product_list:"+type, list, 60);
//			}
//		}
//		return new ResultMessage(ResponseEnum.M2000, list);
//	}
//
//
//	/**
//	 * 外部产品列表
//	 * @return
//	 */
//	@RequestMapping(value = "product_list_out")
//	public ResultMessage product_list_out() {
//		Integer type=2;
//		List<MarketModuleDTO> list=redisMapper.get("product_list:"+type, new TypeReference<List<MarketModuleDTO>>() {
//		});
//		if (list==null) {
//			list = marketService.findModuleProductList(type);
//			if (list!=null&&list.size()>0) {
//				redisMapper.set("product_list:"+type, list, 60);
//			}
//		}
//		return new ResultMessage(ResponseEnum.M2000, list);
//	}
//
//	@RequestMapping(value = "flow")
//	public ResultMessage flow(Long id) {
//		if (id==null||id==0) {
//			return new ResultMessage(ResponseEnum.M4000);
//		}
//		if (productMapper.selectByPrimaryKey(id)==null) {
//			return new ResultMessage(ResponseEnum.M4000);
//		}
//		marketService.updateMarketFlow(id, new DateTime().toLocalDate().toDate());
//		return new ResultMessage(ResponseEnum.M2000);
//	}
//
//	@RequestMapping(value = "config")
//	public ResultMessage config(String code) {
//		Map<String, Object> data=new HashMap<>();
//		int status=0;
//		if (!StringUtils.isBlank(code)) {
//			MarketConfig record=new MarketConfig();
//			record.setCode(code);
//			MarketConfig config = configMapper.selectOne(record);
//			if (config!=null) {
//				status=config.getStatus();
//			}
//		}
//		data.put("status", status);
//		return new ResultMessage(ResponseEnum.M2000,data);
//	}
//}