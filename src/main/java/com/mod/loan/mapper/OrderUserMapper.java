package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.OrderUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OrderUserMapper extends MyBaseMapper<OrderUser> {

    @Select("select uid from tb_user_order where order_no=${}")
    Long getUidByOrderNo(@Param("orderNo")String orderNo);

}