package com.mod.loan.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.MerchantMapper;
import com.mod.loan.model.Merchant;
import com.mod.loan.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl extends BaseServiceImpl<Merchant, String> implements MerchantService {

    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public Merchant findMerchantByAlias(String merchantAlias) {
        Merchant merchant = null;
        if (redisMapper.hasKey("merchant:" + merchantAlias)) {
            merchant = redisMapper.get("merchant:" + merchantAlias, new TypeReference<Merchant>() {
            });
        }
        if (merchant == null || merchant.getBindType() == null || merchant.getRiskType() == null || merchant.getPaymentType() == null) {
            merchant = merchantMapper.selectByPrimaryKey(merchantAlias);
            if (merchant != null) {
                redisMapper.set("merchant:" + merchantAlias, merchant, 600);
            }
        }
        return merchant;
    }

    @Override
    public Merchant findByHuijuId(String huijuId) {
        Merchant merchant = redisMapper.get("merchant:" + huijuId, new TypeReference<Merchant>() {
        });
        if (merchant == null) {
            merchant = merchantMapper.findByHuijuId(huijuId);
            if (merchant != null) {
                redisMapper.set("merchant:" + huijuId, merchant, 600);
            }
        }
        return merchant;
    }

}
