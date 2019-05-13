package com.mod.loan.service;

import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Bank;
import com.mod.loan.model.Merchant;
import com.mod.loan.model.UserBank;

/**
 * @author kk
 */
public interface UserBankService extends BaseService<UserBank, Long> {
    /**
     * 获取当前使用中的银行卡
     *
     * @param uid
     * @return
     */
    UserBank selectUserCurrentBankCard(Long uid);

    ResultMessage sendBaoFooSms(Long uid, String cardNo, String cardPhone);

    ResultMessage bindBaoFooSms(String validateCode, Long uid, String bindInfo, String cardNo,
                                String cardPhone, String bankCode, String bankName);
//
//    /**
//     * 获取合利宝绑卡短验
//     */
//    ResultMessage sendHeliSms(Long uid,String cardNo, String cardPhone,Bank bank);
//
//    /**
//     * 根据合利宝短验进行绑卡
//     */
//    ResultMessage bindByHeliSms(String validateCode,Long uid,String bindInfo);
//
//    /**
//     * 获取富友绑卡短验
//     */
//    ResultMessage sendFuyouSms(Long uid,String cardNo, String cardPhone,Bank bank);
//
//    /**
//     * 根据富友短验进行绑卡
//     */
//    ResultMessage bindByFuyouSms(String validateCode,Long uid,String bindInfo);
//
//    /**
//     * 根据富友协议号进行解约
//     */
//    ResultMessage unbindByFuyou(Long uid, String protocolNo, Merchant merchant);
//
//    /**
//     * 获取汇聚绑卡短验
//     */
//    ResultMessage sendHuijuSms(Long uid,String cardNo, String cardPhone,Bank bank);
//
//    /**
//     * 根据汇聚短验进行绑卡
//     */
//    ResultMessage bindByHuijuSms(String validateCode,Long uid,String bindInfo);

}
