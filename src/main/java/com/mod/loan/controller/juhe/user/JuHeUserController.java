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
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.controller.check.LoginCheck;
import com.mod.loan.model.User;
import com.mod.loan.service.UserService;
import com.mod.loan.util.jwtUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kk
 */
@RestController
@RequestMapping(value = "/juheUser")
public class JuHeUserController {
    private static Logger logger = LoggerFactory.getLogger(JuHeUserController.class);

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
}
