package com.mod.loan.service;

import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.model.MerchantRate;
import org.apache.ibatis.annotations.Param;

public interface MerchantRateService extends BaseService<MerchantRate, Long>{

	MerchantRate findByMerchantAndBorrowType(String merchant,Integer borrowType);

	MerchantRate findByMerchant(String merchant);

}
