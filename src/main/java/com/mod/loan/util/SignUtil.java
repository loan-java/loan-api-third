package com.mod.loan.util;

import org.apache.commons.lang.StringUtils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * sign工具类
 *
 * @author kk
 */
public class SignUtil {

    /**
     * JuHeXinYong 签名加密后
     *
     * @return sign
     */
    public static String getSign(Map<String, Object> paramMap) {
        String sign = getSignStr(paramMap);
        return MD5.toMD5(sign);
    }

    /**
     * JuHeXinYong 签名
     *
     * @return sign
     */
    private static String getSignStr(Map<String, Object> paramMap) {
        //排序
        paramMap = sortMapByKey(paramMap);

        StringBuilder param = new StringBuilder();
        for (String key : paramMap.keySet()) {
            if (!"key".equals(key) && !"sign".equals(key) && StringUtils.isNotBlank(paramMap.get(key).toString())) {
                param.append(key).append("=").append(paramMap.get(key).toString()).append("&");
            }
        }
        param.append("key" + "=").append("e83fgehkJuHeXinYongContosedce9ff");
        return param.toString();
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map 需要排序的Map
     * @return 排序完成的Map
     */
    private static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Object> sortMap = new TreeMap<>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    /**
     * 比较器类
     */
    static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }
}
