package com.mod.loan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constant {

    public final static String KUAI_QIAN_UID_PFX = "JSD";

    public static  String FILE_VISIT_HOST;
    public static  String FILE_SAVE_PATH;

    public static String ENVIROMENT;

    public static String OSS_STATIC_BUCKET_NAME;
    public static String OSS_STATIC_BUCKET_NAME_MOBILE;
    public static String OSS_ACCESSKEY_ID;
    public static String OSS_ENDPOINT_IN;
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

    public static String rongZeRequestAppId;
    public static String rongZeCallbackUrl;
    public static String rongZeQueryUrl;
    public static String rongZePublicKey;
    public static String rongZeOrgPrivateKey;


    public static String bengBengRequestAppId;
    public static String bengBengCallbackUrl;
    public static String bengBengQueryUrl;
    public static String bengBengPublicKey;
    public static String bengBengOrgPrivateKey;


    public static String sysDomainHost; //系统域名


    public static String companyRongZeName;//公司名称
    public static String companyBengBengName;//公司名称

    //畅捷支付
//    public static String chanpayPartnerId;
    public static String chanpayMerchantNo;
    public static String chanpayApiGateway;
    public static String chanpayPublicKey;
    public static String chanpayOrgPrivateKey;

    //指针A
    public static String typeaMemberId;
    public static String typeaTerminalId;
    public static String typeaVersions;
    public static String typeaUrl;
    public static String typeaPfxName;
    public static String typeaPfxPwd;
    public static String typeaCerName;

    // 操盘手配置
    public static String CAO_PAN_SHOU_APP_ID;
    public static String CAO_PAN_SHOU_APP_SECRET;

    @Value("${cao.pan.shou.app.id:}")
    public void setCaoPanShouAppId(String caoPanShouAppId) {
        Constant.CAO_PAN_SHOU_APP_ID = caoPanShouAppId;
    }
    @Value("${cao.pan.shou.app.secret:}")
    public void setCaoPanShouAppSecret(String caoPanShouAppSecret) {
        Constant.CAO_PAN_SHOU_APP_SECRET = caoPanShouAppSecret;
    }

    @Value("${file.visit.host:}")
    public void setFileVisitHost(String fileVisitHost) {
        Constant.FILE_VISIT_HOST = fileVisitHost;
    }

    @Value("${file.save.path:}")
    public void setFileSavePath(String fileSavePath) {
        Constant.FILE_SAVE_PATH = fileSavePath;
    }

    @Value("${typea.member.id:}")
    public void setTypaMemberId(String typeaMemberId) {
        Constant.typeaMemberId = typeaMemberId;
    }

    @Value("${typea.terminal.id:}")
    public void setTypaTerminalId(String typeaTerminalId) {
        Constant.typeaTerminalId = typeaTerminalId;
    }

    @Value("${typea.versions:}")
    public void setTypaVersions(String typeaVersions) {
        Constant.typeaVersions = typeaVersions;
    }

    @Value("${typea.url:}")
    public void setTypaUrl(String typeaUrl) {
        Constant.typeaUrl = typeaUrl;
    }

    @Value("${typea.pfx.name:}")
    public void setTypaPfxName(String typeaPfxName) {
        Constant.typeaPfxName = typeaPfxName;
    }

    @Value("${typea.pfx.pwd:}")
    public void setTypaPfxPwd(String typeaPfxPwd) {
        Constant.typeaPfxPwd = typeaPfxPwd;
    }

    @Value("${typea.cer.name:}")
    public void setTypaCerName(String typeaCerName) {
        Constant.typeaCerName = typeaCerName;
    }


    //    @Value("${chanpay.partner.id}")
//    public void setChanpayPartnerId(String chanpayPartnerId) {
//        Constant.chanpayPartnerId = chanpayPartnerId;
//    }

    @Value("${chanpay.merchant.no:}")
    public void setChanpayMerchantNo(String chanpayMerchantNo) {
        Constant.chanpayMerchantNo = chanpayMerchantNo;
    }

    @Value("${chanpay.api.gateway:}")
    public void setChanpayApiGateway(String chanpayApiGateway) {
        Constant.chanpayApiGateway = chanpayApiGateway;
    }

    @Value("${chanpay.rsa.public.key:}")
    public void setChanpayPublicKey(String chanpayPublicKey) {
        Constant.chanpayPublicKey = chanpayPublicKey;
    }

    @Value("${chanpay.org.rsa.private.key:}")
    public void setChanpayOrgPrivateKey(String chanpayOrgPrivateKey) {
        Constant.chanpayOrgPrivateKey = chanpayOrgPrivateKey;
    }

    public  String getCompanyBengBengName() {
        return companyBengBengName;
    }

    @Value("${company.bengbengname:}")
    public  void setCompanyBengBengName(String companyBengBengName) {
        Constant.companyBengBengName = companyBengBengName;
    }

    @Value("${company.rongzename:}")
    public void setCompanyRongZeName(String companyRongZeName) {
        Constant.companyRongZeName = companyRongZeName;
    }

    @Value("${oss.endpoint.in:}")
    public void setOssEndpointIn(String ossEndpointIn) {
        Constant.OSS_ENDPOINT_IN = ossEndpointIn;
    }

    @Value("${sys.domain.host:}")
    public void setSysDomainHost(String sysDomainHost) {
        Constant.sysDomainHost = sysDomainHost;
    }

    @Value("${rongze.request.app.id:}")
    public void setRongZeRequestAppId(String rongZeRequestAppId) {
        Constant.rongZeRequestAppId = rongZeRequestAppId;
    }

    @Value("${rongze.callback.url:}")
    public void setRongZeCallbackUrl(String rongZeCallbackUrl) {
        Constant.rongZeCallbackUrl = rongZeCallbackUrl;
    }

    @Value("${rongze.query.url:}")
    public void setRongZeQueryUrl(String rongZeQueryUrl) {
        Constant.rongZeQueryUrl = rongZeQueryUrl;
    }

    @Value("${rongze.org.rsa.private.key:}")
    public void setRongZeOrgPrivateKey(String rongZeOrgPrivateKey) {
        Constant.rongZeOrgPrivateKey = rongZeOrgPrivateKey;
    }


    @Value("${rongze.rsa.public.key:}")
    public void setRongZePublicKey(String rongZePublicKey) {
        Constant.rongZePublicKey = rongZePublicKey;
    }

    @Value("${merchant:}")
    public void setMerchant(String merchant) {
        Constant.merchant = merchant;
    }

    @Value("${kuaiqian.jks.path:}")
    public void setKuaiQianJksPath(String kuaiQianJksPath) {
        Constant.kuaiQianJksPath = kuaiQianJksPath;
    }

    @Value("${kuaiqian.pri.key.path:}")
    public void setKuaiQianPriKeyPath(String kuaiQianPriKeyPath) {
        Constant.kuaiQianPriKeyPath = kuaiQianPriKeyPath;
    }

    @Value("${kuaiqian.key.password:}")
    public void setKuaiQianKeyPassword(String kuaiQianKeyPassword) {
        Constant.kuaiQianKeyPassword = kuaiQianKeyPassword;
    }

    @Value("${kuaiqian.repay.url:}")
    public void setKuaiQianRepayUrl(String kuaiQianRepayUrl) {
        Constant.kuaiQianRepayUrl = kuaiQianRepayUrl;
    }

    @Value("${kuaiqian.send.sms.url:}")
    public void setKuaiQianSendSmsUrl(String kuaiQianSendSmsUrl) {
        Constant.kuaiQianSendSmsUrl = kuaiQianSendSmsUrl;
    }

    @Value("${kuaiqian.bind.sms.url:}")
    public void setKuaiQianBindSmsUrl(String kuaiQianBindSmsUrl) {
        Constant.kuaiQianBindSmsUrl = kuaiQianBindSmsUrl;
    }

    @Value("${kuaiqian.member.id:}")
    public void setKuaiQianMemberId(String kuaiQianMemberId) {
        Constant.kuaiQianMemberId = kuaiQianMemberId;
    }

    @Value("${kuaiqian.terminal.id:}")
    public void setKuaiQianTerminalId(String kuaiQianTerminalId) {
        Constant.kuaiQianTerminalId = kuaiQianTerminalId;
    }

    @Value("${kuaiqian.version:}")
    public void setKuaiQianVersion(String kuaiQianVersion) {
        Constant.kuaiQianVersion = kuaiQianVersion;
    }


    @Value("${juhe.call.back.url:}")
    public void setJuHeCallBackUrl(String juHeCallBackUrl) {
        JuHeCallBackUrl = juHeCallBackUrl;
    }

    @Value("${baofoo.key.store.path:}")
    public void setBaoFooKeyStorePath(String baoFooKeyStorePath) {
        Constant.baoFooKeyStorePath = baoFooKeyStorePath;
    }

    @Value("${baofoo.key.store.password:}")
    public void setBaoFooKeyStorePassword(String baoFooKeyStorePassword) {
        Constant.baoFooKeyStorePassword = baoFooKeyStorePassword;
    }

    @Value("${baofoo.pub.key.path:}")
    public void setBaoFooPubKeyPath(String baoFooPubKeyPath) {
        Constant.baoFooPubKeyPath = baoFooPubKeyPath;
    }

    @Value("${baofoo.repay.url:}")
    public void setBaoFooRepayUrl(String baoFooRepayUrl) {
        Constant.baoFooRepayUrl = baoFooRepayUrl;
    }

    @Value("${baofoo.send.sms.url:}")
    public void setBaoFooSendSmsUrl(String baoFooSendSmsUrl) {
        Constant.baoFooSendSmsUrl = baoFooSendSmsUrl;
    }

    @Value("${baofoo.bind.sms.url:}")
    public void setBaoFooBindSmsUrl(String baoFooBindSmsUrl) {
        Constant.baoFooBindSmsUrl = baoFooBindSmsUrl;
    }

    @Value("${baofoo.member.id:}")
    public void setBaoFooMemberId(String baoFooMemberId) {
        Constant.baoFooMemberId = baoFooMemberId;
    }

    @Value("${baofoo.terminal.id:}")
    public void setBaoFooTerminalId(String baoFooTerminalId) {
        Constant.baoFooTerminalId = baoFooTerminalId;
    }

    @Value("${baofoo.version:}")
    public void setBaoFooVersion(String baoFooVersion) {
        Constant.baoFooVersion = baoFooVersion;
    }


    @Value("${environment:}")
    public void setENVIROMENT(String environment) {
        Constant.ENVIROMENT = environment;
    }

    @Value("${oss.static.bucket.name:}")
    public void setOSS_STATIC_BUCKET_NAME(String oSS_STATIC_BUCKET_NAME) {
        Constant.OSS_STATIC_BUCKET_NAME = oSS_STATIC_BUCKET_NAME;
    }

    @Value("${oss.static.bucket.name.mobile:}")
    public void setOSS_STATIC_BUCKET_NAME_MOBILE(String oSS_STATIC_BUCKET_NAME_MOBILE) {
        Constant.OSS_STATIC_BUCKET_NAME_MOBILE = oSS_STATIC_BUCKET_NAME_MOBILE;
    }

    @Value("${oss.accesskey.id:}")
    public void setOSS_ACCESSKEY_ID(String oSS_ACCESSKEY_ID) {
        Constant.OSS_ACCESSKEY_ID = oSS_ACCESSKEY_ID;
    }

    @Value("${oss.accesskey.secret:}")
    public void setOSS_ACCESS_KEY_SECRET(String oSS_ACCESS_KEY_SECRET) {
        Constant.OSS_ACCESS_KEY_SECRET = oSS_ACCESS_KEY_SECRET;
    }

    @Value("${jwt.sercetKey:}")
    public void setJwtSercetkey(String jwtSercetkey) {
        JWT_SERCETKEY = jwtSercetkey;
    }


    @Value("${bengbeng.request.app.id:}")
    public void setBengBengRequestAppId(String bengBengRequestAppId) {
        Constant.bengBengRequestAppId = bengBengRequestAppId;
    }

    @Value("${bengbeng.callback.url:}")
    public void setBengBengCallbackUrl(String bengBengCallbackUrl) {
        Constant.bengBengCallbackUrl = bengBengCallbackUrl;
    }

    @Value("${bengbeng.query.url:}")
    public void setBengBengQueryUrl(String bengBengQueryUrl) {
        Constant.bengBengQueryUrl = bengBengQueryUrl;
    }

    @Value("${bengbeng.rsa.public.key:}")
    public void setBengBengPublicKey(String bengBengPublicKey) {
        Constant.bengBengPublicKey = bengBengPublicKey;
    }

    @Value("${bengbeng.org.rsa.private.key:}")
    public void setBengBengOrgPrivateKey(String bengBengOrgPrivateKey) {
        Constant.bengBengOrgPrivateKey = bengBengOrgPrivateKey;
    }


}
