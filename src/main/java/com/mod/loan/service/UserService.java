package com.mod.loan.service;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.mapper.BaseService;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;
import com.mod.loan.model.UserIdent;
import com.mod.loan.model.UserInfo;

public interface UserService extends BaseService< User,Long>{

	/**
	 * 判断手机号在商户下是否注册过
	 * @param userPhone
	 * @param merchant
	 * @return
	 */
	User selectUserByPhone(String userPhone,String merchant);
	/**
	 * 判断身份证在商户下是否注册
	 * @param certNo
	 * @param merchant
	 * @return
	 */
	User selectUserByCertNo(String userCertNo,String merchant);

	User addUser(String phone, String password, String userOrigin, String merchant, JSONObject param);
	
	void updateUserRealName(User user,UserIdent userIdent);
	
	void updateUserInfo(UserInfo userInfo,UserIdent userIdent);
	
	boolean insertUserBank(Long uid,UserBank userBank);
}
