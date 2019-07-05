package com.mod.loan.service;


import com.mod.loan.model.User;

import java.io.UnsupportedEncodingException;

/**
 *  指针A
 */
public interface TypeFilterService {


    Boolean getInfoByTypeA(User user, String orderNo);
}
