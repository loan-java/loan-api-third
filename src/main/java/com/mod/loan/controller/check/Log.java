package com.mod.loan.controller.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * @author kk
 */
public class Log {
    private static Logger logger = LoggerFactory.getLogger(Log.class);

    public static void write(String msg) {
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "\t: " + msg);
    }

    public static void getAllParamLog(HttpServletRequest Request){
        String paraStr=null;
        String TmpStr = null;


        Enumeration<String> ehead = Request.getHeaderNames();
        while(ehead.hasMoreElements()) {
            paraStr = ehead.nextElement();
            if(paraStr != null){
                TmpStr = "Key:"+paraStr+",Value:"+Request.getHeader(paraStr);
            }
            Log.write("Header接收参数："+TmpStr);
        }


        Enumeration<String> e = Request.getParameterNames();
        while(e.hasMoreElements()) {
            paraStr = e.nextElement();
            if(paraStr != null){
                TmpStr = "Key:"+paraStr+",Value:"+Request.getParameter(paraStr);
            }
            Log.write("接收参数："+TmpStr);
        }

    }
}