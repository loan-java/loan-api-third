package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.UserAddress;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface UserAddressMapper extends MyBaseMapper<UserAddress> {

	List<UserAddress> getByUid(Long uid);

	void updateMasterByUid(Long uid);

	/**
	 * 选择用户默认地址
	 * 
	 * @param uid
	 * @return
	 */
	UserAddress selectDefaultUserAddress(@Param("uid") Long uid);

}