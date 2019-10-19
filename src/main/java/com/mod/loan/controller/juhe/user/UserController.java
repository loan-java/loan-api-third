package com.mod.loan.controller.juhe.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMap;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.controller.check.LoginCheck;
import com.mod.loan.model.User;
import com.mod.loan.service.UserService;
import com.mod.loan.util.CheckUtils;
import com.mod.loan.util.RandomUtils;
import com.mod.loan.util.jwtUtil;
import com.mod.loan.util.sms.EnumSmsTemplate;
import com.mod.loan.util.sms.SmsMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kk
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginCheck loginCheck;
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 登陆授权
     */
    @RequestMapping(value = "/userLogin")
    @LoginRequired(check = false)
    @Api
    public JSONObject userLogin(HttpServletRequest request, @RequestBody JSONObject param) {
        logger.info("=====登陆授权=====");
        logger.info("请求参数：" + JSON.toJSONString(param));

        ResultMessage m = loginCheck.check(request, param, false);
        if (!ResponseEnum.M2000.getCode().equals(m.getStatus())) {
            return ResultMap.fail(m.getStatus(), m.getMsg());
        }
        String phone = param.getString("mobile");

        if (StringUtils.isBlank(phone)) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "手机号不能为空");
        }

        User user = userService.selectUserByPhone(phone, RequestThread.getClientAlias());

        if (user == null) {
            user = userService.addUser(phone, "", UserOriginEnum.JH.getCode(), RequestThread.getClientAlias(), param);
        } else {
            user.setCommonInfo(param.toJSONString());
            userService.updateByPrimaryKey(user);
        }

        long increment = NumberUtils.toLong(redisMapper.get(RedisConst.USER_LOGIN + user.getId()));
        if (increment > 5) {
            return ResultMap.fail(ResponseEnum.M4000.getCode(), "错误次数过多，请稍后重试");
        }

        // 返回用户的一些信息
        String token = jwtUtil.generToken(user.getId().toString(), phone, RequestThread.getClientType(),
                RequestThread.getClientAlias(), RequestThread.getClientVersion());
        redisMapper.set(RedisConst.USER_TOKEN_PREFIX + user.getId(), token, 3 * 86400);

        JSONObject object = ResultMap.success();
        Map<String, Object> map = new HashMap<>(2);
        map.put("token", token);
        map.put("userId", user.getId().toString());
        object.put("data", map);
        return object;
    }

    @RequestMapping(value = "user_graph_code")
    @Api
    public void user_graph_code(HttpServletResponse response, String phone) throws Exception {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return;
        }
        // 生产验证码字符串并保存到session中
        String createText = defaultKaptcha.createText();
        // logger.info(createText);
        redisMapper.set(RedisConst.USER_GRAPH_CODE + phone, createText, 120);
        // request.getSession().setAttribute("vrifyCode", createText);
        // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage challenge = defaultKaptcha.createImage(createText);
        ImageIO.write(challenge, "jpg", jpegOutputStream);
        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        byte[] captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    @RequestMapping(value = "mobile_code")
    @Api
    public ResultMessage mobile_code(String phone, String graph_code, String sms_type) {
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (!NumberUtils.isDigits(graph_code)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码错误");
        }
        EnumSmsTemplate enumSmsType = EnumSmsTemplate.getTemplate(sms_type);
        if (enumSmsType == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "短信事件类型错误");
        }
        // 注册类型,判断用户是否存在
        if ("1001".equals(sms_type) && userService.selectUserByPhone(phone, RequestThread.getClientAlias()) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }
        String redis_graph_code = redisMapper.get(RedisConst.USER_GRAPH_CODE + phone);
        if (!graph_code.equals(redis_graph_code)) {
            redisMapper.remove(RedisConst.USER_GRAPH_CODE + phone);
            return new ResultMessage(ResponseEnum.M2002);
        }
        String randomNum = RandomUtils.generateRandomNum(4);
        // 发送验证码，5分钟内有效
        redisMapper.set(RedisConst.USER_PHONE_CODE + phone, randomNum, 300);
        rabbitTemplate.convertAndSend(RabbitConst.queue_sms,
                new SmsMessage(RequestThread.getClientAlias(), enumSmsType.getKey(), phone, randomNum + "|5分钟"));
        redisMapper.remove(RedisConst.USER_GRAPH_CODE + phone);
        return new ResultMessage(ResponseEnum.M2000);
    }

    @RequestMapping(value = "user_judge_register")
    @Api
    public ResultMessage user_judge_register(String phone) {
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (userService.selectUserByPhone(phone, RequestThread.getClientAlias()) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }
        return new ResultMessage(ResponseEnum.M2000);
    }

    @RequestMapping(value = "user_login")
    @Api
    public ResultMessage user_login(String phone, String password) {
        if (StringUtils.isBlank(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码不能为空");
        }
        User user = userService.selectUserByPhone(phone, RequestThread.getClientAlias());
        if (user == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "用户不存在");
        }
        long increment = NumberUtils.toLong(redisMapper.get(RedisConst.USER_LOGIN + user.getId()));
        if (increment > 5) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "错误次数过多，请稍后重试");
        }
        if (!password.equals(user.getUserPwd())) {
            increment = redisMapper.increment(RedisConst.USER_LOGIN + user.getId(), 1L, 3600);
            if (increment > 5) {// 一个小时登录错误次数超过5次
                return new ResultMessage(ResponseEnum.M4000.getCode(), "错误次数过多，请稍后重试");
            }
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码错误");
        }

        // 返回用户的一些信息
        String token = jwtUtil.generToken(user.getId().toString(), phone, RequestThread.getClientType(),
                RequestThread.getClientAlias(), RequestThread.getClientVersion());
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("token", token);
        userdata.put("uid", user.getId().toString());
        redisMapper.set(RedisConst.USER_TOKEN_PREFIX + user.getId(), token, 3 * 86400);
        return new ResultMessage(ResponseEnum.M2000, userdata);
    }


}
