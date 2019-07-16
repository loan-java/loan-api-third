package com.mod.loan.controller.juhe.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.controller.check.LoginCheck;
import com.mod.loan.mapper.UserAddressListMapper;
import com.mod.loan.mapper.UserIdentMapper;
import com.mod.loan.model.UserAddressList;
import com.mod.loan.model.UserIdent;
import com.mod.loan.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chenanle
 */
@RestController
@RequestMapping(value = "user")
public class UserAddressListController {

    private static Logger logger = LoggerFactory.getLogger(UserAddressListController.class);

    @Autowired
    private UserAddressListMapper userAddressListMapper;
    @Autowired
    private LoginCheck loginCheck;
    @Autowired
    private UserIdentMapper userIdentMapper;

    @RequestMapping("userAddressList")
    public Object userAddressList(HttpServletRequest request, @RequestBody JSONObject map) {
        logger.info("=====上送通讯录=====");
        logger.info("请求参数：" + JSON.toJSONString(map));

        ResultMessage m = loginCheck.check(request, map, true);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMessage());
        }

        if (map.size() == 0) {
            return ResultMap.fail("4000", "参数不能为空");
        }
        String addBookInfo = map.get("addBookInfo").toString();
        //明文密钥临时
        UserAddressList addressList = userAddressListMapper.selectByPrimaryKey(RequestThread.getUid());
        UserIdent userIdent = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
        if (addressList == null && userIdent == null) {
            ResultMap.fail("4000", "用户异常");
        }
        try {
            String userAddressList = Base64Util.decode(addBookInfo.getBytes());
            addressList.setAddressList(userAddressList);
            addressList.setStatus(3);
            userAddressListMapper.updateByPrimaryKeySelective(addressList);
        } catch (Exception e) {
            logger.info("解密异常");
            return ResultMap.fail("4000", "解密异常");
        }
        return ResultMap.success();

    }

}
