package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.DecisionPbDetail;
import com.mod.loan.model.TbDecisionResDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface DecisionPbDetailMapper extends MyBaseMapper<DecisionPbDetail> {

    @Select("select id,result from tb_decision_pb_detail where order_no=#{orderNo} ")
    DecisionPbDetail selectByOrderNo(@Param("orderNo") String orderNo);

}