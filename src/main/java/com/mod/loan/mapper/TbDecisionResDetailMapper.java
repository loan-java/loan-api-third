package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.TbDecisionResDetail;

public interface TbDecisionResDetailMapper extends MyBaseMapper<TbDecisionResDetail> {
    Integer updateByTransId(TbDecisionResDetail tbDecisionResDetail);

    TbDecisionResDetail selectByTransId(String transId);

    TbDecisionResDetail selectByOrderNo(String orderNo);
}