package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.DecisionPbDetail;
import com.mod.loan.model.DecisionZmDetail;
import com.mod.loan.model.TbDecisionResDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface DecisionZmDetailMapper extends MyBaseMapper<DecisionZmDetail> {

    @Select("select id,return_code as returnCode from tb_decision_zm_detail where order_no=#{orderNo} ")
    DecisionZmDetail selectByOrderNo(@Param("orderNo") String orderNo);
}