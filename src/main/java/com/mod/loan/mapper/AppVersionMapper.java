package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.AppVersion;
import org.apache.ibatis.annotations.Param;

public interface AppVersionMapper extends MyBaseMapper<AppVersion> {
	
	AppVersion findNewVersion(@Param("versionAlias") String versionAlias, @Param("versionType") String versionType);
}