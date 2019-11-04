package com.mod.loan.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.AppVersionMapper;
import com.mod.loan.model.AppVersion;
import com.mod.loan.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private RedisMapper redisMapper;

    @Autowired
    private AppVersionMapper versionMapper;

    @Override
    public AppVersion findNewVersion(String versionAlias, String versionType) {
        // TODO Auto-generated method stub
        AppVersion version = redisMapper.get(RedisConst.app_version + versionAlias + versionType, new TypeReference<AppVersion>() {
        });
        if (version == null) {
            version = versionMapper.findNewVersion(versionAlias, versionType);
            if (version != null) {
                redisMapper.set(RedisConst.app_version + versionAlias + versionType, version, 60);
            }
        }
        return version;
    }


}
