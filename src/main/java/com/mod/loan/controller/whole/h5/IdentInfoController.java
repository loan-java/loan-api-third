package com.mod.loan.controller.whole.h5;

import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.mapper.UserIdentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 用户认证
 *
 * @author wgy 2018年4月21日 上午10:42:19
 */
@CrossOrigin("*")
@RestController
public class IdentInfoController {

    @Autowired
    private UserIdentMapper identMapper;

    @LoginRequired(check = true)
    @RequestMapping(value = "user_ident_info")
    public ResultMessage user_ident() {
        return Optional.ofNullable(identMapper.selectByPrimaryKey(RequestThread.getUid())).map(ident -> {
            ident.setUid(null);
            ident.setAlipayTime(null);
            ident.setBindbankTime(null);
            ident.setLivenessTime(null);
            ident.setMobileTime(null);
            ident.setRealNameTime(null);
            ident.setUserDetailsTime(null);
            ident.setCreateTime(null);
            ident.setUpdateTime(null);
            return new ResultMessage(ResponseEnum.M2000, ident);
        }).orElse(new ResultMessage(ResponseEnum.M4000, "用户不存在"));
    }
}