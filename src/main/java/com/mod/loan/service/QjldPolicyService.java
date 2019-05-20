package com.mod.loan.service;

import com.mod.loan.model.dto.DecisionResDetailDTO;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;

/**
 * loan-pay 2019/4/22 huijin.shuailijie Init
 */
public interface QjldPolicyService {

    DecisionResDetailDTO qjldPolicySync(String transId, User user, UserBank userBank);


    DecisionResDetailDTO qjldPolicyNoSync(String transId, User user, UserBank userBank);


    DecisionResDetailDTO qjldPolicQuery(String transId);
}
