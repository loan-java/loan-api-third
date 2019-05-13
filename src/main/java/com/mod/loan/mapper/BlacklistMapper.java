package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.Blacklist;

public interface BlacklistMapper extends MyBaseMapper<Blacklist> {

    Blacklist getByUid(Long uid);

}