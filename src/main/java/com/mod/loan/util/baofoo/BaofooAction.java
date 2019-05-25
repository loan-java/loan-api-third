package com.mod.loan.util.baofoo;


import com.mod.loan.util.baofoo.util.FormatUtil;
import com.mod.loan.util.baofoo.util.PathUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class BaofooAction implements Filter {
    /**
     * constants:系统常量
     */
    private static final Map<String, String> constants = new HashMap<String, String>();
    /**
     * charSet:默认编码
     */
    public static String charSet = "GBK";
    public static String charSet_Def = "UTF-8";
    public static String contextPath;
    public static String WebRoot;

    private static int contextPathLength;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.loadPropertyFile(filterConfig.getInitParameter("propertyFile"), constants);
        //加载配置文件时请隐藏敏感信息
        log("====加载配置文件[" + constants.toString() + "]====");
        charSet = FormatUtil.isEmpty(constants.get("char.set")) ? charSet : constants.get("char.set");
        contextPath = filterConfig.getServletContext().getContextPath();
        WebRoot = filterConfig.getServletContext().getRealPath("/");
        log("====根目录：" + WebRoot);
        contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0 : contextPath.length());
        log("====初始化完成====");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) rep;
        request.setCharacterEncoding(charSet);
        response.setCharacterEncoding(charSet);
        response.setContentType("text/plain; charset=utf-8");
        String target = request.getRequestURI().substring(contextPathLength);
        try {
            if (target.endsWith(".action")) {
                String mName = target.substring(target.lastIndexOf("/") + 1, target.indexOf("."));
                OrderApplyController wc = OrderApplyController.class.newInstance();
                Method m = OrderApplyController.class.getDeclaredMethod(mName, HttpServletRequest.class, HttpServletResponse.class);
                Object obg = m.invoke(wc, request, response);
                response.setContentType("text/html;charset=UTF-8");
                String str = (String) obg;
                response.getWriter().write(str);
            } else {
                PrintWriter out = response.getWriter();
                out.write("没有[" + target + "]执行方法。");
                out.close();
            }
        } catch (Exception e) {
            log("[执行异常]:" + e.getMessage());
            e.printStackTrace();
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    public void loadPropertyFile(String file, Map<String, String> constants) {
        if (FormatUtil.isEmpty(file)) {
            log("[警告]未设置配置文件：propertyFile");
            return;
        }
        int index = file.indexOf(":");
        String propPath = "";
        if (index < 0) {
            propPath = PathUtil.getWebRootPath() + "/" + ((file.charAt(0) == '/') || (file.charAt(0) == '\\') ? "WEB-INF" : "") + file;
        } else if (file.substring(0, index).equalsIgnoreCase("classpath")) {
            propPath = PathUtil.getRootClassPath() + "/" + file.substring(index + 1, file.length());
        } else if (file.substring(0, index).equalsIgnoreCase("file")) {
            propPath = "/" + file.substring(index + 1, file.length());
        }
        propPath = propPath.replaceAll("\\\\", "/");
        propPath = propPath.replaceAll("/+", "/");
        try {
            InputStream is = new FileInputStream(new File(propPath));
            Properties prop = new Properties();
            prop.load(is);
            for (Entry<Object, Object> entry : prop.entrySet()) {
                constants.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private void log(String msg) {
        System.out.println(getTime() + "\t: " + msg);
    }

    public static Map<String, String> getConstants() {
        return constants;
    }

    public static String getContextPath() {
        return contextPath;
    }

    public static String getWebRoot() {
        return WebRoot;
    }
}
