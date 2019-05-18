package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface UserMapper extends MyBaseMapper<User> {

    //复贷
    @Select("select id  from tb_user where UPPER(md5(CONCAT(user_phone,UPPER(user_cert_no))))=#{str} ")
    User getMd5PhoneAndIdcard(@Param("str") String str);

}