package com.mod.loan.util.kuaiqian.entity;

import com.mod.loan.util.kuaiqian.util.PropFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MessageCodeInfo {
    public static String getMessage(String code){
        Properties props = new Properties();
        InputStream in = null;
        try{
            in= PropFile.class.getResourceAsStream("/code.properties");
            props.load(in);
            code=new String(props.getProperty(code).getBytes(StandardCharsets.ISO_8859_1),"GBK");
        } catch (IOException e) {
            System.out.println("读取配置文件失败");
            e.printStackTrace();
        }finally{
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return code;
    }
}
