package com.mod.loan.controller.whole.h5;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.MerchantOriginMapper;
import com.mod.loan.model.MerchantOrigin;
import com.mod.loan.service.MerchantService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.CheckUtils;
import com.mod.loan.util.MD5;
import com.mod.loan.util.RandomUtils;
import com.mod.loan.util.sms.SmsMessage;
import com.mod.loan.util.sms.SmsTemplate;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户注册
 *
 * @author wugy 2018年5月3日 下午9:32:05
 */
@CrossOrigin("*")
@RestController
@RequestMapping(value = "web")
public class RegisterController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantOriginMapper merchantOriginMapper;

    @RequestMapping(value = "graph_code")
    public ResultMessage graph_code() throws IOException {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        // 生产验证码字符串并保存到session中
        String createText = defaultKaptcha.createText();
        // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage challenge = defaultKaptcha.createImage(createText);
        ImageIO.write(challenge, "jpg", jpegOutputStream);
        String base64String = Base64.encodeBase64String(jpegOutputStream.toByteArray());
        Map<String, String> data = new HashMap<>();
        String uuid = UUID.randomUUID().toString();
        data.put("uuid", uuid);
        data.put("graph_code", "data:image/jpeg;base64," + base64String);
        redisMapper.set("web_user_register_graph_code:" + uuid, createText, 120);
        return new ResultMessage(ResponseEnum.M2000, data);
    }

    @RequestMapping(value = "mobile_code")
    public ResultMessage mobile_code(String alias, String phone, String graph_code, String uuid) {
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (!NumberUtils.isDigits(graph_code) || StringUtils.isBlank(uuid)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码错误");
        }
        if (merchantService.findMerchantByAlias(alias) == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
        }
        if (userService.selectUserByPhone(phone, alias) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }
        String verifyCode = redisMapper.get("web_user_register_graph_code:" + uuid);
        redisMapper.remove("web_user_register_graph_code:" + uuid);
        if (!graph_code.equals(verifyCode)) {
            return new ResultMessage(ResponseEnum.M2002);
        }
        String randomNum = RandomUtils.generateRandomNum(4);
        // 发送验证码，5分钟内有效
        redisMapper.set(RedisConst.USER_PHONE_CODE + phone, randomNum, 300);
        rabbitTemplate.convertAndSend(RabbitConst.queue_sms, new SmsMessage(alias, SmsTemplate.T001.getKey(), phone, randomNum + "|5分钟"));
        return new ResultMessage(ResponseEnum.M2000);
    }

    @RequestMapping(value = "register")
    public ResultMessage user_register(String phone, String password, String phone_code, String alias, String origin_id) {
        if (StringUtils.isBlank(origin_id)) {
            origin_id = "android";
        }
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (StringUtils.isBlank(password)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码不能为空");
        }
        if (password.length() < 6) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码至少6位");
        }
        String redis_phone_code = redisMapper.get(RedisConst.USER_PHONE_CODE + phone);
        if (redis_phone_code == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码错误");
        }
        if (!redis_phone_code.equals(phone_code)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码错误");
        }
        if (merchantService.findMerchantByAlias(alias) == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
        }
        if (userService.selectUserByPhone(phone, alias) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }
        userService.addUser(phone, MD5.toMD5(password), origin_id, alias, null);
        redisMapper.remove(RedisConst.USER_PHONE_CODE + phone);
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 注册页打开时检测渠道是否有效
     *
     * @param originNo
     * @return
     */
    @RequestMapping(value = "verify_origin")
    public ResultMessage verify_origin(@RequestParam String merchant, @RequestParam String originNo) {
        MerchantOrigin merchantOrigin = merchantOriginMapper.selectByOriginNo(merchant, originNo);
        if (merchantOrigin == null || merchantOrigin.getStatus() != 1) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "无效渠道");
        }
        return new ResultMessage(ResponseEnum.M2000, merchantOrigin.getId());
    }
}
