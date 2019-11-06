package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;

public interface CaoPanShouService {

    ResultMessage pullUserMobileAuth(String identityNo, String identityName, String password, String mobile);
}
