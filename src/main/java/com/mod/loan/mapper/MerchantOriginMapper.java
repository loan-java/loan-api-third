package com.mod.loan.mapper;

import org.apache.ibatis.annotations.Param;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.MerchantOrigin;

public interface MerchantOriginMapper extends MyBaseMapper<MerchantOrigin> {

	MerchantOrigin selectByOriginNo(@Param("merchant") String merchant, @Param("originNo") String originNo);
}