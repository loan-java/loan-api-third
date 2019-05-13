package com.mod.loan.mapper;

import com.mod.loan.common.mapper.MyBaseMapper;
import com.mod.loan.model.Merchant;

public interface MerchantMapper extends MyBaseMapper<Merchant> {

    /**
     * 通过汇聚商户号获取密钥
     */
    Merchant findByHuijuId(String huijuId);
}