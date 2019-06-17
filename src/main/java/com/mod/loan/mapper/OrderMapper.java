package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper extends MyBaseMapper<Order> {

    Order findByOrderNoAndSource(@Param("orderNo") String orderNo, @Param("source") int source);

    Order findByOrderNo(String orderNo);

    Order findUserLatestOrder(Long uid);

    List<Order> getByUid(Long uid);

    Integer judgeUserTypeByUid(Long uid);

    Integer countPaySuccessByUid(Long uid);

    Order findByOrderNoAndUid(@Param("orderNo") String orderNo, @Param("uid") Long uid,@Param("source") int source);

    /**
     * 查找用户完成订单
     */
    List<Order> getDoubleLoanByUid(Long uid);
}