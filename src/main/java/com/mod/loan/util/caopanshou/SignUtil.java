package com.mod.loan.util.caopanshou;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.TreeMap;

public class SignUtil {

    public static String sign(Map<String, String> params, String secret) {
        Map<String, String> m = new TreeMap<String, String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            m.put(entry.getKey(), entry.getValue());
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : m.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("&");
        }
        sb.append("secret=");
        sb.append(secret);
        
        System.out.println(sb);
        
        String s = DigestUtils.md5Hex(sb.toString()).toLowerCase();
        return s;
    }
}
