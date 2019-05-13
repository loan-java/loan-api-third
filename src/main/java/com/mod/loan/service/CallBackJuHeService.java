package com.mod.loan.service;

import com.mod.loan.common.enums.JuHeCallBackEnum;
import com.mod.loan.model.User;

/**
 * @author kk
 */
public interface CallBackJuHeService {

    void callBack(User user, String orderNo, JuHeCallBackEnum juHeCallBackEnum);

    void callBack(User user, String orderNo, JuHeCallBackEnum juHeCallBackEnum, String remark);
}
