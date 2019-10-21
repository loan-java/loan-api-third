package com.mod.loan.controller.whole.store;

import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.User;
import com.mod.loan.model.UserAddress;
import com.mod.loan.service.UserAddressService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.CheckUtils;
import com.mod.loan.util.StringReplaceUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "user")
public class PersonController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserAddressService userAddressService;

    /**
     * 个人信息
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "person")
    public ResultMessage person() {
        User user = userService.selectByPrimaryKey(RequestThread.getUid());
        Map<String, String> map = new HashMap<String, String>();
        //todo 增加前缀
        map.put("imgHead", user.getImgHead());
        map.put("userNick", user.getUserNick());
        map.put("userPhone", user.getUserPhone());
        map.put("userQq", user.getUserQq());
        map.put("userEmail", user.getUserEmail());
        map.put("userWechat", user.getUserWechat());
        return new ResultMessage(ResponseEnum.M2000, map);
    }

    /**
     * 修改个人信息
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "person_modify")
    public ResultMessage person_modify(@RequestParam(required = false) String imgHead, @RequestParam(required = false) String nick,
                                       @RequestParam(required = false) String qq, @RequestParam(required = false) String email, @RequestParam(required = false) String wechat) {

        if (StringUtils.isBlank(imgHead) && StringUtils.isBlank(nick) && StringUtils.isBlank(qq) && StringUtils.isBlank(email) && StringUtils.isBlank(wechat)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "修改信息不能为空");
        }
        if (StringUtils.isNotBlank(nick) && nick.length() > 20) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "昵称过长，请重新输入");
        }
        if (StringUtils.isNotBlank(qq) && !CheckUtils.isQQNum(qq)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请输入正确的QQ号");
        }
        if (StringUtils.isNotBlank(email) && (!CheckUtils.isEmail(email) || email.length() > 50)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请输入正确的邮箱号");
        }
        if (StringUtils.isNotBlank(wechat) && !CheckUtils.isWechat(wechat)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请输入正确的微信号");
        }

        User user = new User();
        user.setId(RequestThread.getUid());
        user.setImgHead(imgHead);
        user.setUserNick(nick);
        user.setUserQq(qq);
        user.setUserEmail(email);
        user.setUserWechat(wechat);
        userService.updateByPrimaryKeySelective(user);
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 删除收货地址
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "address_del")
    public ResultMessage address_del(@RequestParam(required = true) Long id) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setStatus(0);
        userAddress.setUpdateTime(new Date());
        userAddressService.updateByPrimaryKeySelective(userAddress);
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 新增、修改 收货地址
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "address_modify")
    public ResultMessage address_modify(@RequestParam(required = false) Long id, @RequestParam(required = false) String tag,
                                        @RequestParam(required = true) String name, @RequestParam(required = true) String phone,
                                        @RequestParam(required = true) String province, @RequestParam(required = true) String city,
                                        @RequestParam(required = true) String district, @RequestParam(required = true) String detail,
                                        @RequestParam(required = true) Integer master) {

        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (name.length() > 32 || detail.length() > 200) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "长度勿过长");
        }
        UserAddress userAddress = new UserAddress();
        userAddress.setUid(RequestThread.getUid());
        userAddress.setName(StringReplaceUtil.replaceInvaildString(name));
        userAddress.setPhone(phone);
        userAddress.setProvince(province);
        userAddress.setCity(city);
        userAddress.setDistrict(district);
        userAddress.setDetail(StringReplaceUtil.replaceInvaildString(detail));
        userAddress.setMaster(master);
        userAddress.setTag(tag);
        userAddress.setStatus(1);
        userAddress.setUpdateTime(new Date());

        //id为空为新增
        if (null == id) {
            userAddress.setCreateTime(new Date());
            userAddressService.inserUserAddress(userAddress);
            return new ResultMessage(ResponseEnum.M2000);
        }

        userAddress.setId(id);
        userAddressService.updateUserAddress(userAddress);
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 修改为默认地址
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "address_default")
    public ResultMessage address_default(@RequestParam(required = true) Long id) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setUid(RequestThread.getUid());
        userAddress.setMaster(1);
        userAddress.setUpdateTime(new Date());
        userAddressService.updateMasterByUid(userAddress);
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 收获地址list
     *
     * @return
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "addr_list")
    public ResultMessage addr_list() {
        return new ResultMessage(ResponseEnum.M2000, userAddressService.getByUid(RequestThread.getUid()));
    }


}
