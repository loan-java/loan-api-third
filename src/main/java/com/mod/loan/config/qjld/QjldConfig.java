package com.mod.loan.config.qjld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * loan-pay 2019/4/20 huijin.shuailijie Init
 */
@Component
public class QjldConfig {
    @Value("${qjld.member.id}")
    private String qjldMemberId;

    @Value("${qjld.terminal.id}")
    private String qjldTerminalId;

    @Value("${qjld.key.path}")
    private String qjldKeyPath;

    @Value("${qjld.key.pwd}")
    private String qjldKeyPwd;


    @Value("${qjld.pub.key.path}")
    private String qjldPubKeyPath;


    @Value("${qjld.policy.url}")
    private String qjldPolicyUrl;


    @Value("${qjld.callback.url}")
    private String qjldCallBackUrl;


    @Value("${qjld.hetian.url}")
    private String qjldHeTianUrl;

    @Value("${qjld.type}")
    private String qjldType;


    @Value("${qjld.product.type}")
    private String qjldProductType;


    @Value("${qjld.version}")
    private String qjldVersion;

    public String getQjldMemberId() {
        return qjldMemberId;
    }

    public void setQjldMemberId(String qjldMemberId) {
        this.qjldMemberId = qjldMemberId;
    }

    public String getQjldTerminalId() {
        return qjldTerminalId;
    }

    public void setQjldTerminalId(String qjldTerminalId) {
        this.qjldTerminalId = qjldTerminalId;
    }

    public String getQjldKeyPath() {
        return qjldKeyPath;
    }

    public void setQjldKeyPath(String qjldKeyPath) {
        this.qjldKeyPath = qjldKeyPath;
    }

    public String getQjldKeyPwd() {
        return qjldKeyPwd;
    }

    public void setQjldKeyPwd(String qjldKeyPwd) {
        this.qjldKeyPwd = qjldKeyPwd;
    }


    public String getQjldPolicyUrl() {
        return qjldPolicyUrl;
    }

    public void setQjldPolicyUrl(String qjldPolicyUrl) {
        this.qjldPolicyUrl = qjldPolicyUrl;
    }

    public String getQjldPubKeyPath() {
        return qjldPubKeyPath;
    }

    public void setQjldPubKeyPath(String qjldPubKeyPath) {
        this.qjldPubKeyPath = qjldPubKeyPath;
    }

    public String getQjldVersion() {
        return qjldVersion;
    }

    public void setQjldVersion(String qjldVersion) {
        this.qjldVersion = qjldVersion;
    }

    public String getQjldType() {
        return qjldType;
    }

    public void setQjldType(String qjldType) {
        this.qjldType = qjldType;
    }

    public String getQjldCallBackUrl() {
        return qjldCallBackUrl;
    }

    public void setQjldCallBackUrl(String qjldCallBackUrl) {
        this.qjldCallBackUrl = qjldCallBackUrl;
    }

    public String getQjldHeTianUrl() {
        return qjldHeTianUrl;
    }

    public void setQjldHeTianUrl(String qjldHeTianUrl) {
        this.qjldHeTianUrl = qjldHeTianUrl;
    }

    public String getQjldProductType() {
        return qjldProductType;
    }

    public void setQjldProductType(String qjldProductType) {
        this.qjldProductType = qjldProductType;
    }
}
