package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.OrderRepay;

public interface OrderRepayMapper extends MyBaseMapper<OrderRepay> {

    int countRepaySuccess(Long orderId);

}