package com.mod.loan.controller.check;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.util.HttpUtils;
import com.mod.loan.util.SignUtil;
import com.mod.loan.util.jwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验
 */
@Component
public class LoginCheck {
    private static Logger logger = LoggerFactory.getLogger(LoginCheck.class);

    @Autowired
    private RedisMapper redisMapper;



    public ResultMessage check(HttpServletRequest request, JSONObject obj, boolean shouldLogin) {
        RequestThread.remove();// 移除本地线程变量
        String ip = HttpUtils.getIpAddr(request, ".");
        String clientVersion = obj.getString("version");
        String clientType = obj.getString("terminalId");
        String clientAlias = Constant.merchant;
        String sign = obj.getString("sign");
        String deviceCode = obj.getString("deviceCode");
        String token = obj.getString("token");

        String signEncode = SignUtil.getSign(obj);
        if (!signEncode.equals(sign)) {
            logger.error("sign鉴权失败, signEncode=" + signEncode + ", sign=" + sign);
            return new ResultMessage(ResponseEnum.M4006);
        }

        RequestThread.setClientVersion(clientVersion);
        RequestThread.setClientType(clientType);
        RequestThread.setClientAlias(clientAlias);
        RequestThread.setIp(ip);
        RequestThread.setRequestTime(System.currentTimeMillis());
        RequestThread.setDeviceCode(deviceCode);
        RequestThread.setToken(token);
        RequestThread.setSign(sign);

        if (shouldLogin && !isLogin()) {
            return new ResultMessage(ResponseEnum.M4002);
        }

        return new ResultMessage(ResponseEnum.M2000);
    }

    private boolean isLogin() {
        String token = RequestThread.getToken();
        if (StringUtils.isBlank(token)) {
            return false;
        }
        Claims verifyToken = jwtUtil.ParseJwt(token);
        if (verifyToken == null) {
            return false;
        }
        String uid = String.valueOf(verifyToken.get("uid"));
        String tokenRedis = redisMapper.get(RedisConst.USER_TOKEN_PREFIX + uid);
        if (!token.equals(tokenRedis)) {
            return false;
        }
        redisMapper.set(RedisConst.USER_TOKEN_PREFIX + uid, tokenRedis, 3 * 86400);
        RequestThread.setUid(Long.parseLong(uid));
        return true;
    }
}
