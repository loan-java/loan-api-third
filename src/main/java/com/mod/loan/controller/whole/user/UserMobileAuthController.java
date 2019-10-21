package com.mod.loan.controller.whole.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.mapper.MoxieMobileMapper;
import com.mod.loan.mapper.UserIdentMapper;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.MoxieMobile;
import com.mod.loan.model.User;
import com.mod.loan.model.UserIdent;
import com.mod.loan.util.Base64Util;
import com.mod.loan.util.aliyun.OSSUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/**
 * @author chenanle
 */
@RestController
@RequestMapping(value = "user")
public class UserMobileAuthController {

    private static Logger logger = LoggerFactory.getLogger(UserMobileAuthController.class);

    @Autowired
    private MoxieMobileMapper moxieMobileMapper;

    @Autowired
    private UserIdentMapper userIdentMapper;

    @Autowired
    private UserMapper userMapper;


    @Api
    @LoginRequired
    @RequestMapping(value = "userMobileAuth")
    public ResultMessage userMobileAuth(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        logger.info("=====运营商认证=====");
        logger.info("请求参数：" + JSON.toJSONString(jsonObject));

        if (jsonObject.size() == 0) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请求参数不能为空");
        }
        String mxMobile = jsonObject.getString("operaInfo");
        if (mxMobile == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "参数解析错误");
        }



        User user = userMapper.selectByPrimaryKey(RequestThread.getUid());
        UserIdent userIdent = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
        if (user == null && userIdent == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "用户不存在");
        }

        String mxMobilePath = OSSUtil.uploadStr(Base64Util.decode(mxMobile.getBytes()),user.getId());
        if (StringUtils.isBlank(mxMobilePath)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "文件上传失败");
        }
        logger.info("上传成功");


        MoxieMobile moxieMobile = new MoxieMobile();
        moxieMobile.setUid(RequestThread.getUid());
        moxieMobile.setPhone(user.getUserPhone());
        moxieMobile.setRemark(mxMobilePath);
        userIdent.setMobile(2);
        userIdent.setMobileTime(new Date());
        userIdentMapper.updateByPrimaryKeySelective(userIdent);
        moxieMobileMapper.insertSelective(moxieMobile);

        return new ResultMessage(ResponseEnum.M2000);
    }
}
