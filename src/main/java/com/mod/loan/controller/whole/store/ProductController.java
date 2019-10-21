//package com.mod.loan.controller.whole.store;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.mod.loan.common.annotation.Api;
//import com.mod.loan.common.annotation.LoginRequired;
//import com.mod.loan.common.enums.ResponseEnum;
//import com.mod.loan.common.model.Page;
//import com.mod.loan.common.model.ResultMessage;
//import com.mod.loan.config.redis.RedisConst;
//import com.mod.loan.config.redis.RedisMapper;
//import com.mod.loan.model.ProductBanner;
//import com.mod.loan.model.ProductChannel;
//import com.mod.loan.model.dto.BannerDTO;
//import com.mod.loan.model.dto.ChannelDTO;
//import com.mod.loan.service.ProductBannerService;
//import com.mod.loan.service.ProductChannelService;
//import com.mod.loan.service.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@CrossOrigin("*")
//@RestController
//@RequestMapping("product")
//public class ProductController {
//
//	@Autowired
//    RedisMapper redisMapper;
//	@Autowired
//	ProductService productService;
//	@Autowired
//	ProductBannerService productBannerService;
//	@Autowired
//	ProductChannelService productChannelService;
//
//	/**
//	 * 首页运营推广banner
//	 *
//	 * @return
//	 */
//	@Api
//	@LoginRequired(check = false)
//	@RequestMapping(value = "banner_list")
//	public ResultMessage banner_list() {
//		Map<String, Object> data = new HashMap<>();
//		List<BannerDTO> sowing_banners = redisMapper.get(RedisConst.sowing_banners, new TypeReference<List<BannerDTO>>() {
//		});
//		List<BannerDTO> standard_banners = redisMapper.get(RedisConst.standard_banners, new TypeReference<List<BannerDTO>>() {
//		});
//		if (sowing_banners == null) {
//			sowing_banners = new ArrayList<BannerDTO>();
//			List<ProductBanner> channelList = productBannerService.findListByPid(0L);// 一级List
//			for (ProductBanner secondBanner : channelList) {
//				if (secondBanner.getStyle() == 1) {// 轮播图
//					BannerDTO bannerDTO = new BannerDTO();
//					bannerDTO.setBannerName(secondBanner.getBannerName());
//					bannerDTO.setBannerImg(secondBanner.getBannerImg());
//					bannerDTO.setBannerUrl(secondBanner.getBannerUrl());
//					bannerDTO.setBannerList(productBannerService.findListByPid(secondBanner.getId()));
//					sowing_banners.add(bannerDTO);
//				}
//			}
//			if (sowing_banners.size() > 0) {
//				redisMapper.set(RedisConst.sowing_banners, sowing_banners, 800);
//			}
//		}
//		if (standard_banners == null) {
//			standard_banners = new ArrayList<BannerDTO>();
//			List<ProductBanner> channelList = productBannerService.findListByPid(0L);// 一级List
//			for (ProductBanner secondBanner : channelList) {
//				if (secondBanner.getStyle() == 2) {// 标准图
//					BannerDTO bannerDTO = new BannerDTO();
//					bannerDTO.setBannerName(secondBanner.getBannerName());
//					bannerDTO.setBannerImg(secondBanner.getBannerImg());
//					bannerDTO.setBannerUrl(secondBanner.getBannerUrl());
//					bannerDTO.setBannerList(productBannerService.findListByPid(secondBanner.getId()));
//					standard_banners.add(bannerDTO);
//				}
//			}
//			if (standard_banners.size() > 0) {
//				redisMapper.set(RedisConst.standard_banners, standard_banners, 420);
//			}
//		}
//
//		data.put("sowingBanners", sowing_banners);// 轮播图
//		data.put("standardBanners", standard_banners);// 标准图
//		return new ResultMessage(ResponseEnum.M2000, data);
//	}
//
//	/**
//	 * 所有商品列表
//	 *
//	 * @return
//	 */
//	@Api
//	@LoginRequired(check = false)
//	@RequestMapping(value = "product_list")
//	public ResultMessage product_list() {
//		return new ResultMessage(ResponseEnum.M2000, productService.findProductList());
//	}
//
//	/**
//	 * H5 商城list
//	 */
//	@LoginRequired(check = false)
//	@RequestMapping(value = "channel_list")
//	public ResultMessage channel_list() {
//		return new ResultMessage(ResponseEnum.M2000, productChannelService.findListByPid(0L));
//	}
//
//	/**
//	 * H5 商城详细分类
//	 */
//	@LoginRequired(check = false)
//	@RequestMapping(value = "channel_detail")
//	public ResultMessage channel_detail(@RequestParam(required = true) Long id) {
//		List<ChannelDTO> list = redisMapper.get(RedisConst.product_channel_detail + id, new TypeReference<List<ChannelDTO>>() {
//		});
//		if (list == null) {
//			ProductChannel productChannel = productChannelService.selectByPrimaryKey(id);// 一级
//			List<ProductChannel> secondChannelList = productChannelService.findListByPid(productChannel.getId());// 二级
//			List<ChannelDTO> channels = new ArrayList<ChannelDTO>();
//			for (ProductChannel secondChannel : secondChannelList) {
//				ChannelDTO channelDTO = new ChannelDTO();
//				channelDTO.setName(secondChannel.getChannelName());
//				channelDTO.setChannelUrl(secondChannel.getChannelUrl());
//				List<ProductChannel> thirdChannels = productChannelService.findListByPid(secondChannel.getId());
//				channelDTO.setChannelList(thirdChannels);
//				channels.add(channelDTO);
//			}
//			if (channels.size() > 0) {
//				redisMapper.set(RedisConst.product_channel_detail + id, channels, 60);
//				list = channels;
//			}
//		}
//		return new ResultMessage(ResponseEnum.M2000, list);
//	}
//
//	/**
//	 * 根据关键字分页搜索商品
//	 *
//	 * @param key
//	 * @param pageNo
//	 * @param pageSize
//	 * @return
//	 */
//	@LoginRequired(check = false)
//	@RequestMapping(value = "product_search")
//	public ResultMessage product_search(@RequestParam(required = true) String key, @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "100") Integer pageSize) {
//		ResultMessage resultMessage = new ResultMessage();
//		Page page = new Page();
//		page.setPageNo(pageNo);
//		page.setPageSize(pageSize);
//		resultMessage.setPage(page);
//		productService.searchProductByKey(resultMessage, key);// 根据key值分页搜索商品
//		return resultMessage;
//	}
//
//	/**
//	 * 单一商品详情
//	 *
//	 * @param id
//	 * @return
//	 */
//	@LoginRequired(check = false)
//	@RequestMapping(value = "product_detail")
//	public ResultMessage product_detail(@RequestParam(required = true) Long id) {
//		return new ResultMessage(ResponseEnum.M2000, productService.selectById(id));
//	}
//
//}