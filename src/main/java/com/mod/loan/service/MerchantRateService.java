package com.mod.loan.service;

import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.model.MerchantRate;

public interface MerchantRateService extends BaseService<MerchantRate, Long>{

	MerchantRate findByMerchantAndBorrowType(String merchant,Integer borrowType);

}
