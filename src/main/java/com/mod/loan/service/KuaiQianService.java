package com.mod.loan.service;

import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Order;

/**
 * @author kk
 */
public interface KuaiQianService {

    ResultMessage sendKuaiQianSms(Long uid, String cardNo, String cardPhone);

    ResultMessage bindKuaiQianSms(String validateCode, Long uid, String bindInfo, String cardNo,
                                String cardPhone, String bankCode, String bankName);

    ResultMessage repay(String orderNo);

    ResultMessage repay(Order order);
}
