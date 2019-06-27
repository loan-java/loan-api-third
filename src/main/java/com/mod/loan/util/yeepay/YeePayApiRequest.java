package com.mod.loan.util.yeepay;

import com.yeepay.shade.com.google.common.collect.Maps;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liujianjian
 * @date 2019/6/15 21:00
 */
public class YeePayApiRequest {

    /**
     * 鉴权绑卡请求
     */
    public static StringResultDTO bindCardRequest(String requestno, String identityid,
                                              String cardno, String idcardno, String username, String phone) throws Exception {
        String merchantno = Config.getInstance().getValue("merchantno");
        String idcardtype = "ID";
        String authtype = "COMMON_FOUR";

        Map<String, String> map = new HashMap<String, String>();
        map.put("merchantno", merchantno);
        map.put("requestno", requestno);
        map.put("identityid", identityid);
        map.put("identitytype", "USER_ID");
        map.put("cardno", cardno);
        map.put("idcardno", idcardno);
        map.put("idcardtype", idcardtype);
        map.put("username", username);
        map.put("phone", phone);
        map.put("issms", "true");
        map.put("requesttime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("authtype", authtype);

        String authbindcardreqUri = Config.getInstance().getValue("authbindcardreqUri");
        return YeepayUtil.yeepayYOP(map, authbindcardreqUri);
    }

    /**
     * 鉴权绑卡确认
     */
    public static StringResultDTO bindCardConfirm(String requestno, String validatecode) throws Exception {
        String merchantno = Config.getInstance().getValue("merchantno");

        Map<String, String> map = Maps.newHashMap();
        map.put("merchantno", merchantno);
        map.put("requestno", requestno);
        map.put("validatecode", validatecode);

        String authbindcardconfirmUri = Config.getInstance().getValue("authbindcardconfirmUri");

        return YeepayUtil.yeepayYOP(map, authbindcardconfirmUri);
    }

    /**
     * 绑卡支付请求
     */
    public static StringResultDTO cardPayRequest(String requestno, String identityid, String cardtop,
                                             String cardlast, String amount, String productname, String terminalno, boolean issms) throws Exception {
        String unibindcardpayUri = Config.getInstance().getValue("unibindcardpayUri");
        String merchantno = Config.getInstance().getValue("merchantno");

        Map<String, String> map = new HashMap<>();
        map.put("merchantno", merchantno);
        map.put("requestno", requestno);
        map.put("issms", String.valueOf(issms));
        map.put("identityid", identityid);
        map.put("identitytype", "USER_ID");
        map.put("cardtop", cardtop);
        map.put("cardlast", cardlast);
        map.put("amount", amount);
//        map.put("advicesmstype", advicesmstype);
//        map.put("avaliabletime", avaliabletime);
        map.put("productname", productname);
//        map.put("callbackurl", callbackUrl);
        map.put("requesttime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("terminalno", terminalno); //协议支付： SQKKSCENEKJ010 代扣： SQKKSCENE10 商户需开通对应协议支付/代扣权限
//        map.put("remark", remark);
//        map.put("extinfos", extinfos);
//        map.put("dividecallbackurl", dividecallbackurl);
//        map.put("dividejstr", dividejstr);

        return YeepayUtil.yeepayYOP(map, unibindcardpayUri);
    }

    //支付查询
    public StringResultDTO queryPayResult(String yborderid) throws Exception {
        String merchantno = Config.getInstance().getValue("merchantno");
//        String requestno=format(request.getParameter("requestno"));
//        String yborderid=format(request.getParameter("yborderid"));

        Map<String, String> map = new HashMap<String, String>();
        map.put("merchantno", merchantno);
//        map.put("requestno", requestno);
        map.put("yborderid", yborderid);

        String bindcardpayqueryUri = Config.getInstance().getValue("bindcardpayqueryUri");
        return YeepayUtil.yeepayYOP(map, bindcardpayqueryUri);
    }

}
