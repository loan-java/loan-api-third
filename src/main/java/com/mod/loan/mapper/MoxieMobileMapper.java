package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.MoxieMobile;
import org.apache.ibatis.annotations.Param;

public interface MoxieMobileMapper extends MyBaseMapper<MoxieMobile> {
    /**
     * 查找最新的用户支付宝认证
     *
     * @param id
     * @return
     */
    MoxieMobile selectLastOne(@Param("uid") Long id);
}