package com.mod.loan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constant {

    public final static String KUAI_QIAN_UID_PFX = "JSD";

    public static String ENVIROMENT;

    public static String OSS_STATIC_BUCKET_NAME;
    public static String OSS_STATIC_BUCKET_NAME_MOBILE;
    public static String OSS_ACCESSKEY_ID;
    public static String OSS_ACCESS_KEY_SECRET;

    public static String JWT_SERCETKEY;

    public static String baoFooKeyStorePath;
    public static String baoFooKeyStorePassword;
    public static String baoFooPubKeyPath;
    public static String baoFooRepayUrl;
    public static String baoFooSendSmsUrl;
    public static String baoFooBindSmsUrl;
    public static String baoFooMemberId;
    public static String baoFooTerminalId;
    public static String baoFooVersion;
    public static String baofooBankKeyPath;
    public static String baofooBankPubPath;
    public static String baofooBankKeyPassword;
    public static String baofooBankUrl;

    public static String JuHeCallBackUrl;

    public static String kuaiQianPriKeyPath;
    public static String kuaiQianKeyPassword;
    public static String kuaiQianJksPath;
    public static String kuaiQianRepayUrl;
    public static String kuaiQianSendSmsUrl;
    public static String kuaiQianBindSmsUrl;
    public static String kuaiQianMemberId;
    public static String kuaiQianTerminalId;
    public static String kuaiQianVersion;
    public static String merchant;

    @Value("${merchant}")
    public void setMerchant(String merchant) {
        Constant.merchant = merchant;
    }

    @Value("${kuaiqian.jks.path}")
    public void setKuaiQianJksPath(String kuaiQianJksPath) {
        Constant.kuaiQianJksPath = kuaiQianJksPath;
    }

    @Value("${kuaiqian.pri.key.path}")
    public void setKuaiQianPriKeyPath(String kuaiQianPriKeyPath) {
        Constant.kuaiQianPriKeyPath = kuaiQianPriKeyPath;
    }

    @Value("${kuaiqian.key.password}")
    public void setKuaiQianKeyPassword(String kuaiQianKeyPassword) {
        Constant.kuaiQianKeyPassword = kuaiQianKeyPassword;
    }

    @Value("${kuaiqian.repay.url}")
    public void setKuaiQianRepayUrl(String kuaiQianRepayUrl) {
        Constant.kuaiQianRepayUrl = kuaiQianRepayUrl;
    }

    @Value("${kuaiqian.send.sms.url}")
    public void setKuaiQianSendSmsUrl(String kuaiQianSendSmsUrl) {
        Constant.kuaiQianSendSmsUrl = kuaiQianSendSmsUrl;
    }

    @Value("${kuaiqian.bind.sms.url}")
    public void setKuaiQianBindSmsUrl(String kuaiQianBindSmsUrl) {
        Constant.kuaiQianBindSmsUrl = kuaiQianBindSmsUrl;
    }

    @Value("${kuaiqian.member.id}")
    public void setKuaiQianMemberId(String kuaiQianMemberId) {
        Constant.kuaiQianMemberId = kuaiQianMemberId;
    }

    @Value("${kuaiqian.terminal.id}")
    public void setKuaiQianTerminalId(String kuaiQianTerminalId) {
        Constant.kuaiQianTerminalId = kuaiQianTerminalId;
    }

    @Value("${kuaiqian.version}")
    public void setKuaiQianVersion(String kuaiQianVersion) {
        Constant.kuaiQianVersion = kuaiQianVersion;
    }


    @Value("${juhe.call.back.url}")
    public void setJuHeCallBackUrl(String juHeCallBackUrl) {
        JuHeCallBackUrl = juHeCallBackUrl;
    }


    @Value("${baofoo.bank.key.path}")
    public void setBaofooBankKeyPath(String baofooBankKeyPath) {
        Constant.baofooBankKeyPath = baofooBankKeyPath;
    }

    @Value("${baofoo.bank.pub.path}")
    public void setBaofooBankPubPath(String baofooBankPubPath) {
        Constant.baofooBankPubPath = baofooBankPubPath;
    }

    @Value("${baofoo.bank.key.password}")
    public void setBaofooBankKeyPassword(String baofooBankKeyPassword) {
        Constant.baofooBankKeyPassword = baofooBankKeyPassword;
    }

    @Value("${baofoo.bank.url}")
    public void setBaofooBankUrl(String baofooBankUrl) {
        Constant.baofooBankUrl = baofooBankUrl;
    }


    @Value("${baofoo.key.store.path}")
    public void setBaoFooKeyStorePath(String baoFooKeyStorePath) {
        Constant.baoFooKeyStorePath = baoFooKeyStorePath;
    }

    @Value("${baofoo.key.store.password}")
    public void setBaoFooKeyStorePassword(String baoFooKeyStorePassword) {
        Constant.baoFooKeyStorePassword = baoFooKeyStorePassword;
    }

    @Value("${baofoo.pub.key.path}")
    public void setBaoFooPubKeyPath(String baoFooPubKeyPath) {
        Constant.baoFooPubKeyPath = baoFooPubKeyPath;
    }

    @Value("${baofoo.repay.url}")
    public void setBaoFooRepayUrl(String baoFooRepayUrl) {
        Constant.baoFooRepayUrl = baoFooRepayUrl;
    }

    @Value("${baofoo.send.sms.url}")
    public void setBaoFooSendSmsUrl(String baoFooSendSmsUrl) {
        Constant.baoFooSendSmsUrl = baoFooSendSmsUrl;
    }

    @Value("${baofoo.bind.sms.url}")
    public void setBaoFooBindSmsUrl(String baoFooBindSmsUrl) {
        Constant.baoFooBindSmsUrl = baoFooBindSmsUrl;
    }

    @Value("${baofoo.member.id}")
    public void setBaoFooMemberId(String baoFooMemberId) {
        Constant.baoFooMemberId = baoFooMemberId;
    }

    @Value("${baofoo.terminal.id}")
    public void setBaoFooTerminalId(String baoFooTerminalId) {
        Constant.baoFooTerminalId = baoFooTerminalId;
    }

    @Value("${baofoo.version}")
    public void setBaoFooVersion(String baoFooVersion) {
        Constant.baoFooVersion = baoFooVersion;
    }


    @Value("${environment:}")
    public void setENVIROMENT(String environment) {
        Constant.ENVIROMENT = environment;
    }

    @Value("${oss.static.bucket.name:}")
    public void setOSS_STATIC_BUCKET_NAME(String oSS_STATIC_BUCKET_NAME) {
        OSS_STATIC_BUCKET_NAME = oSS_STATIC_BUCKET_NAME;
    }

    @Value("${oss.static.bucket.name.mobile:}")
    public void setOSS_STATIC_BUCKET_NAME_MOBILE(String oSS_STATIC_BUCKET_NAME_MOBILE) {
        OSS_STATIC_BUCKET_NAME_MOBILE = oSS_STATIC_BUCKET_NAME_MOBILE;
    }

    @Value("${oss.accesskey.id:}")
    public void setOSS_ACCESSKEY_ID(String oSS_ACCESSKEY_ID) {
        OSS_ACCESSKEY_ID = oSS_ACCESSKEY_ID;
    }

    @Value("${oss.accesskey.secret:}")
    public void setOSS_ACCESS_KEY_SECRET(String oSS_ACCESS_KEY_SECRET) {
        OSS_ACCESS_KEY_SECRET = oSS_ACCESS_KEY_SECRET;
    }

    @Value("${jwt.sercetKey:}")
    public void setJwtSercetkey(String jwtSercetkey) {
        JWT_SERCETKEY = jwtSercetkey;
    }
}
