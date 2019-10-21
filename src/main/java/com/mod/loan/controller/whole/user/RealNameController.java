package com.mod.loan.controller.whole.user;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.FaceVerifyRequest;
import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.mapper.UserAddressListMapper;
import com.mod.loan.mapper.UserIdentMapper;
import com.mod.loan.mapper.UserInfoMapper;
import com.mod.loan.model.*;
import com.mod.loan.model.dto.UserContact;
import com.mod.loan.service.OrderService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.CheckUtils;
import com.mod.loan.util.StringReplaceUtil;
import com.mod.loan.util.aliyun.OSSUtil;
import com.mod.loan.util.baidu.FaceUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 用户认证
 *
 * @author wgy 2018年4月21日 上午10:42:19
 */
@RestController
@RequestMapping(value = "user")
public class RealNameController {
    private static Logger logger = LoggerFactory.getLogger(RealNameController.class);

    // 设置APPID/AK/SK
    @Value("${baidu.face.app.id:}")
    String APP_ID;
    @Value("${baidu.face.api.key:}")
    String API_KEY;
    @Value("${baidu.face.secret.key:}")
    String SECRET_KEY;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserIdentMapper userIdentMapper;
    @Autowired
    OrderService orderService;
    @Autowired
    UserAddressListMapper addressListMapper;
    @Autowired
    UserService userService;

    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "user_info")
    public ResultMessage user_info() {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(RequestThread.getUid());
        userInfo.setUid(null);
        return new ResultMessage(ResponseEnum.M2000, userInfo);
    }

    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "user_info_save")
    public ResultMessage user_info_save(@RequestParam(required = true) String education,
                                        @RequestParam(required = true) String liveProvince, @RequestParam(required = true) String liveCity,
                                        @RequestParam(required = true) String liveDistrict, @RequestParam(required = true) String liveAddress,
                                        @RequestParam(required = true) String liveTime, @RequestParam(required = true) String liveMarry,
                                        @RequestParam(required = true) String workType, @RequestParam(required = true) String workCompany,
                                        @RequestParam(required = true) String workAddress, @RequestParam(required = true) String directContact,
                                        @RequestParam(required = true) String directContactName,
                                        @RequestParam(required = true) String directContactPhone,
                                        @RequestParam(required = true) String othersContact,
                                        @RequestParam(required = true) String othersContactName,
                                        @RequestParam(required = true) String othersContactPhone) {
        UserAddressList userAddressList = new UserAddressList();
        userAddressList.setUid(RequestThread.getUid());
        userAddressList.setStatus(1);
        if (null == addressListMapper.selectOne(userAddressList)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请重新选择其他联系人");
        }

        if (workCompany.length() > 50) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "公司名称太长");
        }
        if (liveAddress.length() > 100) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "居住地址太长");
        }
        if (workAddress.length() > 100) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "工作地址太长");
        }
        directContactName = StringReplaceUtil.replaceInvaildString(directContactName);
        othersContactName = StringReplaceUtil.replaceInvaildString(othersContactName);
        liveAddress = StringReplaceUtil.replaceInvaildString(liveAddress);
        workAddress = StringReplaceUtil.replaceInvaildString(workAddress);
        workCompany = StringReplaceUtil.replaceInvaildString(workCompany);
        if (directContactName.equals(othersContactName)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "两位联系人不能相同");
        }
        if (directContactPhone.equals(othersContactPhone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "两位联系人号码不能相同");
        }
        if (!CheckUtils.isMobiPhoneNum(directContactPhone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "直系联系人的手机号码不正确");
        }
        if (!CheckUtils.isMobiPhoneNum(othersContactPhone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "其他联系人的手机号码不正确");
        }
        User user = userService.selectByPrimaryKey(RequestThread.getUid());
        if (user.getUserPhone().equals(directContactPhone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "直系联系人的手机号码不能是自己");
        }
        if (user.getUserPhone().equals(othersContactPhone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "间接联系人的手机号码不能是自己");
        }
        // 当前订单 ,无法修改信息
        Order order = orderService.findUserLatestOrder(RequestThread.getUid());
        if (order != null && order.getStatus() < 40) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "无法修改个人信息");
        }
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(RequestThread.getUid());
        userInfo.setEducation(education);
        userInfo.setLiveProvince(liveProvince);
        userInfo.setLiveCity(liveCity);
        userInfo.setLiveDistrict(liveDistrict);
        userInfo.setLiveAddress(liveAddress);
        userInfo.setLiveTime(liveTime);
        userInfo.setLiveMarry(liveMarry);
        userInfo.setWorkType(workType);
        userInfo.setWorkCompany(workCompany);
        userInfo.setWorkAddress(workAddress);
        userInfo.setDirectContact(directContact);
        userInfo.setDirectContactName(directContactName);
        userInfo.setDirectContactPhone(directContactPhone);

        userInfo.setOthersContact(othersContact);
        userInfo.setOthersContactName(othersContactName);
        userInfo.setOthersContactPhone(othersContactPhone);
        userInfo.setUpdateTime(new Date());
        UserIdent record = new UserIdent();
        record.setUid(RequestThread.getUid());
        record.setUserDetails(2);
        record.setUserDetailsTime(new Date());
        userService.updateUserInfo(userInfo, record);
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 保存用户通讯录
     *
     * @return
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "address_list")
    public ResultMessage address_list(String addressList) {
        Order order = orderService.findUserLatestOrder(RequestThread.getUid());
        if (order != null && order.getStatus() < 40) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "无法修改通讯录");
        }
        List<UserContact> list = JSON.parseArray(StringReplaceUtil.replaceInvaildString(addressList),
                UserContact.class);
        Set<UserContact> contacts = new LinkedHashSet<>();
        for (UserContact userContact : list) {
            if (CheckUtils.isMobiPhoneNum(userContact.getP())) {
                contacts.add(userContact);
            }
        }

        UserAddressList userAddressList = new UserAddressList();
        userAddressList.setUid(RequestThread.getUid());
        userAddressList.setAddressList(JSON.toJSONString(contacts));
        userAddressList.setStatus(1);
        userAddressList.setUpdateTime(new Date());
        addressListMapper.updateByPrimaryKeySelective(userAddressList);
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 实名信息
     *
     * @return
     */
    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "real_name_info")
    public ResultMessage real_name_info() {
        User user = userService.selectByPrimaryKey(RequestThread.getUid());
        Map<String, Object> map = new HashMap<>();
        map.put("userPhone", user.getUserPhone());
        map.put("userName", user.getUserName());
        map.put("userCertNo", user.getUserCertNo());
        //todo 身份证人脸识别照片前缀
        if (!StringUtils.isBlank(user.getImgFace())) {
            map.put("imgFace", user.getImgFace());
        }
        if (!StringUtils.isBlank(user.getImgCertFront())) {
            map.put("imgCertFront", user.getImgCertFront());
        }
        if (!StringUtils.isBlank(user.getImgCertBack())) {
            map.put("imgCertBack", user.getImgCertBack());
        }
        UserIdent userIdent = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
        if (userIdent.getRealName() == 2) {
            map.put("identStatus", 1);
        } else {
            map.put("identStatus", 0);
        }
        if (userIdent.getLiveness() == 2) {
            map.put("liveStatus", 1);
        } else {
            map.put("liveStatus", 0);
        }
        return new ResultMessage(ResponseEnum.M2000, map);
    }

    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "real_name_save")
    public ResultMessage real_name_save(@RequestParam(required = true) String imgCertFront,
                                        @RequestParam(required = true) String imgCertBack, String userName, String userCertNo, String ia,
                                        String indate, String address, String nation) {
        if (!CheckUtils.cheackCardIndate(indate)) {
            logger.error("身份证日期异常uid={},date={},url={}", RequestThread.getUid(), indate, imgCertBack);
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请重新识别身份证背面");
        }
        if (StringUtils.isBlank(address) || StringUtils.isBlank(nation) || StringUtils.isBlank(userName)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请重新识别身份证正面");
        }
        if (!CheckUtils.isValidateIdcard(userCertNo)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请重新识别身份证正面");
        }
        User user_old = userService.selectUserByCertNo(userCertNo, RequestThread.getClientAlias());
        if (user_old != null && !user_old.getId().equals(RequestThread.getUid())) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "您的身份已被注册");
        }
        UserIdent userIdentRecord = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
        if (userIdentRecord.getRealName() == 2) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "不要重复实名认证");
        }
        UserIdent userIdent = new UserIdent();
        userIdent.setUid(userIdentRecord.getUid());
        userIdent.setRealName(2);
        userIdent.setRealNameTime(new Date());
        User user = new User();
        user.setId(userIdentRecord.getUid());
        user.setUserName(userName.trim());
        user.setUserCertNo(userCertNo.trim());
        user.setIa(ia);
        user.setIndate(indate);
        user.setAddress(address);
        user.setNation(nation);
        user.setImgCertFront(imgCertFront);
        user.setImgCertBack(imgCertBack);
        userService.updateUserRealName(user, userIdent);
        return new ResultMessage(ResponseEnum.M2000);
    }

    @Api
    @LoginRequired(check = true)
    @RequestMapping(value = "face_check")
    public ResultMessage face_check(@RequestParam(required = true) String imgFace) {
        UserIdent userIdentRecord = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
        if (userIdentRecord.getLiveness() == 2) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "不要重复认证");
        }
        UserIdent userIdent = new UserIdent();
        userIdent.setUid(userIdentRecord.getUid());
        userIdent.setLiveness(2);
        userIdent.setLivenessTime(new Date());
        User user = new User();
        user.setId(userIdentRecord.getUid());
        user.setImgFace(imgFace);
        userService.updateUserRealName(user, userIdent);
        return new ResultMessage(ResponseEnum.M2000);
    }

    @RequestMapping(value = "face_save")
    @LoginRequired(check = true)
    public ResultMessage face_save(@RequestParam("file") MultipartFile file) {
        try {
            // 初始化一个AipFace
            AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            // String face_fields =
            // "age,beauty,expression,faceshape,gender,glasses,race,quality,facetype";
            String face_fields = "quality,facetype,faceshape";
            FaceVerifyRequest req = new FaceVerifyRequest(base64, "BASE64", face_fields);
            List<FaceVerifyRequest> requests = new ArrayList<FaceVerifyRequest>();
            requests.add(req);
            JSONObject res = client.faceverify(requests);
            ResultMessage faceDTO = FaceUtils.checkArgs(res.toString());
            if (!"2000".equals(faceDTO.getStatus())) {
                return faceDTO;
            }
            UserIdent userIdentRecord = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
            if (userIdentRecord.getLiveness() == 2) {
                return new ResultMessage(ResponseEnum.M4000.getCode(), "请不要重复认证");
            }
            String filePath = OSSUtil.upload(file);
            if (StringUtils.isBlank(filePath)) {
                return new ResultMessage(ResponseEnum.M4000.getCode(), "认证失败！");
            }
            UserIdent userIdent = new UserIdent();
            userIdent.setUid(userIdentRecord.getUid());
            userIdent.setLiveness(2);
            userIdent.setLivenessTime(new Date());
            User user = new User();
            user.setId(userIdentRecord.getUid());
            user.setImgFace(filePath);
            userService.updateUserRealName(user, userIdent);
            return new ResultMessage(ResponseEnum.M2000);
        } catch (Exception e) {
            logger.error("上传人脸头像异常！", e);
        }
        return new ResultMessage(ResponseEnum.M4000);
    }

}