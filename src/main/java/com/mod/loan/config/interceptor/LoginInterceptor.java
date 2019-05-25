package com.mod.loan.config.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.util.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionAuth = (String) request.getSession().getAttribute("auth");
        if (sessionAuth != null) {
            System.out.println("this is next step");
            nextStep(request, response);
        } else {
            if (!checkHeaderAuth(request)) {
                logger.error("auth 校验失败");
                response.setStatus(401);
                response.setHeader("Cache-Control", "no-store");
                response.setDateHeader("Expires", 0);
                response.setHeader("WWW-authenticate", "Basic Realm=\"test\"");
                response.getWriter().write("auth fail");
                return false;
            }
        }
        logger.info("auth 校验成功");
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
}
