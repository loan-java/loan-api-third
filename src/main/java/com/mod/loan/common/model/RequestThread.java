package com.mod.loan.common.model;

/**
 * requestBean的ThreadLocal Created by lijy on 2017/7/20.
 */
public class RequestThread {
    private static final ThreadLocal<RequestBean> requestThread = new ThreadLocal<RequestBean>();

    public static RequestBean get() {
        RequestBean s = requestThread.get();
        if (s == null) {
            s = new RequestBean();
            requestThread.set(s);
        }
        return s;
    }

    public static void remove() {
        requestThread.remove();
    }

    public static String getIp() {
        return get().getIp();
    }

    public static void setIp(String ip) {
        get().setIp(ip);
    }

    public static Long getRequestTime() {
        return get().getRequestTime();
    }

    public static void setRequestTime(Long requestTime) {
        get().setRequestTime(requestTime);
    }

    public static Long cost() {
        return (System.currentTimeMillis() - get().getRequestTime());
    }

    public static String getToken() {
        return get().getToken();
    }

    public static void setToken(String token) {
        get().setToken(token);
    }

    public static Long getUid() {
        return get().getUid();
    }

    public static void setUid(Long uid) {
        get().setUid(uid);
    }

    public static String getClientVersion() {
        return get().getClientVersion();
    }

    public static void setClientVersion(String clientVersion) {
        get().setClientVersion(clientVersion);
    }

    public static String getUa() {
        return get().getUa();
    }

    public static void setUa(String ua) {
        get().setUa(ua);
    }

    public static String getClientType() {
        return get().getClientType();
    }

    public static void setClientType(String clientType) {
        get().setClientType(clientType);
    }

    public static String getClientAlias() {
        return get().getClientAlias();
    }

    public static void setClientAlias(String clientAlias) {
        get().setClientAlias(clientAlias);
    }

    public static String getDeviceCode() {
        return get().getDeviceCode();
    }

    public static void setDeviceCode(String deviceCode) {
        get().setDeviceCode(deviceCode);
    }

    public static String getSign() {
        return get().getSign();
    }

    public static void setSign(String sign) {
        get().setSign(sign);
    }

    public static String getSourceId() {
        return get().getSourceId();
    }

    public static void setSourceId(String sourceId) {
        get().setSourceId(sourceId);
    }
}
