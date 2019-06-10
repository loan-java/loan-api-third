package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.OrderUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrderUserMapper extends MyBaseMapper<OrderUser> {

    @Select("select distinct uid from tb_user_order where order_no=#{orderNo} and source=#{source} limit 1")
    Long getUidByOrderNoAndSource(@Param("orderNo")String orderNo,@Param("source")Integer source);

    @Select("select id,order_no as orderNo,source,uid,create_time as createTime " +
            " from tb_user_order where order_no=#{orderNo} and source=#{source} and uid=#{uid} ")
    OrderUser getUidByOrderNoAndSourceAndUid(@Param("orderNo")String orderNo,@Param("source")Integer source,@Param("uid")Long uid);


    @Select("select a.id,a.order_no as orderNo,a.source,a.uid,a.create_time as createTime " +
            "from tb_user_order a inner join (select max(id) as id,uid from tb_user_order GROUP BY uid ) b on a.id=b.id ")
    List<OrderUser> getList();

}