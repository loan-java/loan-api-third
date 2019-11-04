package com.mod.loan.service;

import com.mod.loan.model.AppVersion;

public interface AppService {

    AppVersion findNewVersion(String versionAlias, String versionType);
}
