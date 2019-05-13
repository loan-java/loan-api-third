package com.mod.loan.util.kuaiqian.entity;

/**
 * @author cen
 * @project mgwCore
 * @description:发送参数对象
 * @create_time:Jun 22, 2009
 * @modify_time:Jun 22, 2009
 */
public class MerchantInfo {
    /**
     * 商户号
     */
    private String merchantId;
    /**
     * 登录密码
     */
    private String password;
    /**
     * tr1发送xml数据
     */
    private String xml;
    /**
     * 发送url
     */
    private String url;
    /**
     * 证书密码
     */
    private String certPass;
    /**
     * 证书路径
     */
    private String certPath;
    /**
     * 超时时间(单位秒)
     */
    private int timeOut;

    //提交地址
    private String domainName;
    //提交端口
    private String sslPort;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getSslPort() {
        return sslPort;
    }

    public void setSslPort(String sslPort) {
        this.sslPort = sslPort;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getUrl() {
        return this.domainName + ":" + this.sslPort + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCertPass() {
        return certPass;
    }

    public void setCertPass(String certPass) {
        this.certPass = certPass;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = Integer.parseInt(timeOut);
    }


}
