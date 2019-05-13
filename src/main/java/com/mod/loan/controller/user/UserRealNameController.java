package com.mod.loan.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.controller.check.LoginCheck;
import com.mod.loan.mapper.UserIdentMapper;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.User;
import com.mod.loan.model.UserIdent;
import com.mod.loan.util.Base64Util;
import com.mod.loan.util.aliyun.OSSUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author chenanle
 */
@RestController
@RequestMapping(value = "userRealName")
public class UserRealNameController {

    private static Logger logger = LoggerFactory.getLogger(UserRealNameController.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserIdentMapper userIdentMapper;

    @Autowired
    private LoginCheck loginCheck;

    @RequestMapping(value = "userRealName")
    public JSONObject userRealName(@RequestParam("userName") String userName,
                                   @RequestParam("certNo") String certNo,
                                   @RequestParam("CP") MultipartFile CP,
                                   @RequestParam("CO") MultipartFile CO,
                                   @RequestParam("LPA1") MultipartFile LPA1,
                                   @RequestParam("token") String token,
                                   @RequestParam("version") String version,
                                   @RequestParam("accountId") String accountId,
                                   @RequestParam("timeStamp") String timeStamp,
                                   @RequestParam("source") String source,
                                   @RequestParam("intefaceType") String intefaceType,
                                   @RequestParam("terminalId") String terminalId,
                                   @RequestParam("sign") String sign,
                                   @RequestParam("deviceCode") String deviceCode,
                                   HttpServletRequest request) {
        logger.info("=====实名认证=====");

        if (StringUtils.isBlank(userName) && StringUtils.isBlank(certNo) && CP == null && CO == null && LPA1 == null && StringUtils.isBlank(token) && StringUtils.isBlank(version) && StringUtils.isBlank(terminalId) && StringUtils.isBlank(sign) && StringUtils.isBlank(deviceCode)) {
            logger.info("参数不能为空");
            return ResultMap.fail("4000", "请求的参数错误");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        jsonObject.put("version", version);
        jsonObject.put("terminalId", terminalId);
        jsonObject.put("sign", sign);
        jsonObject.put("deviceCode", deviceCode);
        jsonObject.put("intefaceType", intefaceType);
        jsonObject.put("source", source);
        jsonObject.put("timeStamp", timeStamp);
        jsonObject.put("accountId", accountId);
        jsonObject.put("certNo", certNo);
        jsonObject.put("userName", userName);

        logger.info("请求参数：" + JSON.toJSONString(jsonObject));

        ResultMessage m = loginCheck.check(request, jsonObject, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMessage());
        }

        User user = userMapper.selectByPrimaryKey(RequestThread.getUid());
        UserIdent userIdent = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
        if (user == null && userIdent == null) {
            return ResultMap.fail("4000", "用户不存在");
        }
        if (Base64Util.juHeRsaDecode(certNo).equals(user.getUserCertNo()) && userIdent.getRealName() == 2) {
            return ResultMap.fail("4000", "请不要重复认证");
        }
        String cpPath = OSSUtil.upload(CP);
        String coPath = OSSUtil.upload(CO);
        String lap1Path = OSSUtil.upload(LPA1);
        if (StringUtils.isBlank(cpPath) && StringUtils.isBlank(coPath) && StringUtils.isBlank(lap1Path)) {
            return ResultMap.fail("4000", "上传失败！");
        }
        logger.info("上传成功");

        user.setImgCertFront(cpPath);
        user.setImgCertBack(coPath);
        user.setImgFace(lap1Path);
        user.setUserName(Base64Util.juHeRsaDecode(userName));
        user.setUserCertNo(Base64Util.juHeRsaDecode(certNo));
        userMapper.updateByPrimaryKeySelective(user);

        userIdent.setLiveness(2);
        userIdent.setLivenessTime(new Date());
        userIdent.setRealName(2);
        userIdent.setRealNameTime(new Date());
        userIdentMapper.updateByPrimaryKeySelective(userIdent);

        return ResultMap.success();
    }
}
