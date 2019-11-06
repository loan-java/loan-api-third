package com.mod.loan.util.caopanshou;

public class Constants {
    public static final String CRAWLER_GATEWAY_BASE_URL = "https://bxrisk.hsydh.com";
    public static final String CRAWLER_CREATE_URL = CRAWLER_GATEWAY_BASE_URL + "/crawler-gateway/crawlers/create";
    public static final String CRAWLER_OPERATE_URL = CRAWLER_GATEWAY_BASE_URL + "/crawler-gateway/crawlers/operate";
    public static final String CRAWLER_DATA_API_URL = CRAWLER_GATEWAY_BASE_URL + "/crawler-gateway/data_api";

    public static final String SUCCESS_CODE = "0000";

    public static final String CRAWLER_STATUS_Crawling = "Crawling";
    public static final String CRAWLER_STATUS_WaitAppendData = "WaitAppendData";
    public static final String CRAWLER_STATUS_Success = "Success";
    public static final String CRAWLER_STATUS_Failure = "Failure";

    public static final String APPEND_DATA_NAME_SMS_VERIFY_CODE = "sms_verify_code";
    public static final String APPEND_DATA_NAME_IMAGE_VERIFY_CODE = "image_verify_code";
    public static final String APPEND_DATA_NAME_IMAGE_QR_CODE = "image_qr_code";

    public static final String CRAWLER_OPERATE_METHOD_CrawlerGetInfo = "CrawlerGetInfo";
    public static final String CRAWLER_OPERATE_METHOD_CrawlerAppendData = "CrawlerAppendData";
    public static final String CRAWLER_OPERATE_METHOD_CrawlerGetData = "CrawlerGetData";
    public static final String CRAWLER_OPERATE_METHOD_CrawlerGetPdfData = "CrawlerGetPdfData";
    public static final String CRAWLER_OPERATE_METHOD_CrawlerGetOriginalData = "CrawlerGetOriginalData";
    public static final String CRAWLER_OPERATE_METHOD_CrawlerGetOriginalPdfData = "CrawlerGetOriginalPdfData";
    public static final String CRAWLER_OPERATE_METHOD_CrawlerGetLog = "CrawlerGetLog";


    // 以下字段用于爬虫接口
    public static final String crawlerType = "Operator";
    // 通话报告
    public static final String crawlerTypeOperatorReport = "OperatorReport";
    //  通话报告 高级[现用]
    public static final String crawlerTypeOperatorReport2 = "OperatorReport2";
	
    //信贷保镖
    public static final String crawlerTypeTongDun = "TongDun";
    //失信被执行人
    public static final String crawlerTypeCourtPerson = "CourtPerson";
    //QQ
    public static final String crawlerTypeIMQQ = "IMQQ";
    //美团
    public static final String crawlerTypeMeiTuan = "MeiTuan";
    //饿了么
    public static final String crawlerTypeEle = "Ele";
    //脉脉
    public static final String crawlerTypeMaiMai = "MaiMai";
    //新浪微博
    public static final String crawlerTypeSinaWeibo = "SinaWeibo";
    //淘宝
    public static final String crawlerTypeTaoBao = "TaoBao";
    //南宁公共资源
    public static final String crawlerTypeNNGGZY = "NNGGZY";
    //京东
    public static final String crawlerTypeJingDong = "JingDong";
    //京东报告
    public static final String crawlerTypeJingDongReport = "JingDongReport";
    //淘宝报告
    public static final String crawlerTypeTaoBaoReport = "TaoBaoReport";
    
}
