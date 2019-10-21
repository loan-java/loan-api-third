package com.mod.loan.controller.juhe.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.controller.check.LoginCheck;
import com.mod.loan.mapper.UserIdentMapper;
import com.mod.loan.mapper.UserInfoMapper;
import com.mod.loan.model.UserIdent;
import com.mod.loan.model.UserInfo;
import com.mod.loan.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chenanle
 */
@RestController
@RequestMapping(value = "userInfo")
public class UserInfoController {

    private static Logger logger = LoggerFactory.getLogger(JuHeUserController.class);

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserIdentMapper userIdentMapper;

    @Autowired
    private LoginCheck loginCheck;

    /**
     * 上送用户基本信息
     *
     * @param map
     * @return
     */
    @LoginRequired(check = true)
    @RequestMapping(value = "userInfoSave")
    public JSONObject userInfoSave(HttpServletRequest request, @RequestBody JSONObject map) {
        logger.info("=====上送用户基本信息=====");
        logger.info("请求参数：" + JSON.toJSONString(map));

        ResultMessage m = loginCheck.check(request, map, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }

        if (map.size() == 0) {
            return ResultMap.fail("4000", "参数不能为空");
        }
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(RequestThread.getUid());
        UserIdent userIdent = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
        if (userInfo == null && userIdent == null) {
            return ResultMap.fail("4000", "用户信息不存在");
        }
        userInfo.setEducation(map.get("education").toString());
        userInfo.setLiveProvince(map.get("province").toString());
        userInfo.setLiveCity(map.get("city").toString());
        userInfo.setLiveDistrict(map.get("area").toString());
        userInfo.setLiveAddress(map.get("address").toString());
        userInfo.setWorkCompanyProvince(map.get("companyProvince").toString());
        userInfo.setWorkCompanyCity(map.get("companyCity").toString());
        userInfo.setWorkCompanyArea(map.get("companyArea").toString());
        userInfo.setWorkAddress(map.get("companyAddress").toString());
        userInfo.setWorkCompany(map.get("company").toString());
        userInfo.setWorkCompanyPhone(map.get("companyPhone").toString());
        userInfo.setLiveMarry(map.get("marriage").toString());
        userInfo.setWorkType(map.get("career").toString());
        userInfo.setJobTitle(map.get("jobTitle").toString());
        userInfo.setIncomeMonthlyYuan(new BigDecimal(map.get("incomeMonthlyYuan").toString()));
        userInfoMapper.updateByPrimaryKeySelective(userInfo);

        userIdent.setUserDetails(2);
        userIdent.setUserDetailsTime(new Date());
        userIdentMapper.updateByPrimaryKeySelective(userIdent);

        return ResultMap.success();
    }

    /**
     * 上送紧急联系人信息
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "contactInfoSave")
    public JSONObject contactInfoSave(HttpServletRequest request, @RequestBody JSONObject map) {
        logger.info("=====上送紧急联系人信息=====");
        logger.info("请求参数：" + JSON.toJSONString(map));

        ResultMessage m = loginCheck.check(request, map, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }

        if (map.size() < 1) {
            return ResultMap.fail("4000", "参数不能为空");
        }

        System.out.println("Request Param = " + JSON.toJSONString(map));
        String contactStr = Base64Util.decode(map.getString("contact").getBytes());
        System.out.println("contact = " + contactStr);
        JSONArray jsonArray = JSONArray.parseArray(contactStr);

        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(RequestThread.getUid());
        //获取list里的第一个值set到userInfo里做第一个联系人
        JSONObject data1 = jsonArray.getJSONObject(0);
        userInfo.setDirectContact(data1.getString("contactType"));
        userInfo.setDirectContactName(data1.getString("contactName"));
        userInfo.setDirectContactPhone(data1.getString("contactMobile"));

        //获取list里的第二个值set到userInfo里做第二个联系人
        JSONObject data2 = jsonArray.getJSONObject(1);
        userInfo.setOthersContact(data2.getString("contactType"));
        userInfo.setOthersContactName(data2.getString("contactName"));
        userInfo.setOthersContactPhone(data2.getString("contactMobile"));

        userInfoMapper.updateByPrimaryKeySelective(userInfo);
        return ResultMap.success();
    }

}
