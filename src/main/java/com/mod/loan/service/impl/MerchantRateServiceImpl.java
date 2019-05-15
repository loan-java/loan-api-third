package com.mod.loan.service.impl;

import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.mapper.MerchantRateMapper;
import com.mod.loan.model.MerchantRate;
import com.mod.loan.service.MerchantRateService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class MerchantRateServiceImpl extends BaseServiceImpl<MerchantRate, Long> implements MerchantRateService {

	@Resource
	private MerchantRateMapper merchantRateMapper;

	@Override
	public MerchantRate findByMerchantAndBorrowType(String merchant, Integer borrowType) {
		if (StringUtils.isEmpty(merchant)) {
			return merchantRateMapper.findByMoneyAndDay(new BigDecimal(1000), 7);
		}
		return merchantRateMapper.findByMerchantAndBorrowType(merchant, borrowType);
	}

	@Override
	public MerchantRate findByMerchant(String merchant) {
		return merchantRateMapper.findByMerchant(RequestThread.getClientAlias());
	}
}
