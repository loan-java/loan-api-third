package com.mod.loan.config.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.model.Merchant;
import com.mod.loan.service.MerchantService;
import com.mod.loan.util.HttpUtils;
import com.mod.loan.util.RSAUtils;
import com.mod.loan.util.jwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author wgy 2017年8月24日 上午10:48:12
 */
public class LoginInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private MerchantService merchantService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestThread.remove();// 移除本地线程变量
        String ip = HttpUtils.getIpAddr(request, ".");
        String clientVersion = request.getParameter("version");
        String clientType = request.getParameter("type");
        String clientAlias = request.getParameter("alias");
        RequestThread.setClientVersion(clientVersion);
        RequestThread.setClientType(clientType);
        RequestThread.setClientAlias(clientAlias);
        RequestThread.setIp(ip);
        RequestThread.setRequestTime(System.currentTimeMillis());
        HandlerMethod hm = (HandlerMethod) handler;
        Api api = hm.getMethodAnnotation(Api.class);
        if (api != null) {
            if (StringUtils.isEmpty(clientType) || !("android".equals(clientType) || "ios".equals(clientType))) {
                printMessage(response, new ResultMessage(ResponseEnum.M4000.getCode(), "无效的type"));
                return false;
            }
            if (StringUtils.isEmpty(clientVersion)) {
                printMessage(response, new ResultMessage(ResponseEnum.M4000.getCode(), "无效的version"));
                return false;
            }
            if (StringUtils.isEmpty(clientAlias)) {
                printMessage(response, new ResultMessage(ResponseEnum.M4000.getCode(), "无效的alias"));
                return false;
            }
            Merchant merchant = merchantService.findMerchantByAlias(clientAlias);
            if (merchant == null || merchant.getMerchantStatus() != 1) {
                printMessage(response, new ResultMessage(ResponseEnum.M4000.getCode(), "无效的alias"));
                return false;
            }
        }
        LoginRequired lr = hm.getMethodAnnotation(LoginRequired.class);
        if (lr != null && lr.check() && !isLogin(request)) {
            printMessage(response, new ResultMessage(ResponseEnum.M4002));
            return false;
        }
        return true;
    }

    private boolean checkHeaderAuth(HttpServletRequest request) throws Exception {
        String auth = request.getHeader("Authorization");
        logger.info("auth is " + auth);

        if ((auth != null) && (auth.length() > 6)) {
            auth = auth.substring(6);
            String encode = RSAUtils.base64Encode("JuHksidl92834LidfkLsssDIEN:I9089LISJDkdsl882kDilsDLLo");
            logger.info("us auth is " + encode);
            return encode.equals(auth);
        }
        return false;
    }

    public void nextStep(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.println("<html> next step, authentication is : " + request.getSession().getAttribute("auth") + "<br>");
        pw.println("<br></html>");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestThread.remove();// 移除本地线程变量
    }

    private void printMessage(HttpServletResponse response, Object message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().write(JSONObject.toJSONString(message));
    }


    private boolean isLogin(HttpServletRequest request) {
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            return false;
        }
        Claims verifyToken = jwtUtil.ParseJwt(token);
        if (verifyToken == null) {
            return false;
        }
        String uid = String.valueOf(verifyToken.get("uid"));
        String clientType = String.valueOf(verifyToken.get("clientType"));
        String clientVersion = String.valueOf(verifyToken.get("clientVersion"));
        String clientAlias = String.valueOf(verifyToken.get("clientAlias"));
        String token_redis = redisMapper.get(RedisConst.USER_TOKEN_PREFIX + uid);
        if (!token.equals(token_redis)) {
            return false;
        }
        redisMapper.set(RedisConst.USER_TOKEN_PREFIX + uid, token_redis, 3 * 86400);
        RequestThread.setUid(Long.parseLong(uid));
        RequestThread.setClientVersion(clientVersion);
        RequestThread.setClientType(clientType);
        RequestThread.setClientAlias(clientAlias);
        return true;
    }

}
