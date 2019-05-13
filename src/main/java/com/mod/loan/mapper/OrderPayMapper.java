package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.OrderPay;
import org.apache.ibatis.annotations.Param;

public interface OrderPayMapper extends MyBaseMapper<OrderPay> {

    OrderPay selectByOrderIdAndStatus(@Param("orderId")Long orderId,@Param("payStatus")Integer payStatus);

}