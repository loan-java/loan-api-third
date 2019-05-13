package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.MerchantRate;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface MerchantRateMapper extends MyBaseMapper<MerchantRate> {

    MerchantRate findByMoneyAndDay(@Param("productMoney") BigDecimal productMoney, @Param("productDay") Integer productDay);

    MerchantRate findByMerchantAndBorrowType(@Param("merchant") String merchant, @Param("borrowType") Integer borrowType);

    MerchantRate findByMerchant(@Param("merchant") String merchant);
}