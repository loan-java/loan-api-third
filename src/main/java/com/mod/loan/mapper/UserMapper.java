package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.User;
import org.apache.ibatis.annotations.Param;


public interface UserMapper extends MyBaseMapper<User> {

    //复贷
    User getMd5PhoneAndIdcard(@Param("str") String str);

}