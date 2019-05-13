package com.mod.loan.service;

import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.model.Merchant;

public interface MerchantService extends BaseService<Merchant,String> {

	Merchant findMerchantByAlias(String merchantAlias);

	/**
	 * 通过汇聚商户号获取密钥
	 */
	Merchant findByHuijuId(String huijuId);

}
