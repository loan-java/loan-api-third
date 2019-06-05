package com.mod.loan.mapper;

import com.mod.loan.model.UserAuthInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface UserAuthInfoMapper extends Mapper<UserAuthInfo> {

    UserAuthInfo selectByUid(@Param("uid") Long uid);

}