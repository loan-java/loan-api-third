package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.service.CaoPanShouService;
import com.mod.loan.util.caopanshou.Constants;
import com.mod.loan.util.caopanshou.HttpService;
import com.mod.loan.util.caopanshou.SignUtil;
import lombok.extern.log4j.Log4j;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操盘手Service
 *
 * @author yutian
 */
@Log4j
@Service
public class CaoPanShouServiceImpl implements CaoPanShouService {

    @Override
    public ResultMessage pullUserMobileAuth(String identityNo, String identityName, String password, String mobile) {
        String TAG = "CaoPanShouServiceImpl.pullUserMobileAuth: name=" + identityName + "&mobile=" + mobile;
        HttpService httpservice = HttpService.newInstance();
        String extParam = null; //扩展参数-一般是紧急联系人
        JSONObject createJson = requestMobileCrawler(httpservice, mobile, password, identityName, identityNo, extParam);
        String crawlerId = createJson.getString("crawlerId");
        String crawlerToken = createJson.getString("crawlerToken");
        if (!Constants.SUCCESS_CODE.equals(createJson.getString("code"))) {
            log.error(TAG + ", 创建爬虫失败：" + createJson.toJSONString());
            return new ResultMessage(ResponseEnum.M3014);
        }
        while (true) {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                log.error(TAG + ", sleep error", e);
            }
            JSONObject operate = operateCrawler(httpservice, Constants.CRAWLER_OPERATE_METHOD_CrawlerGetInfo, crawlerId, crawlerToken);
            if (!Constants.SUCCESS_CODE.equals(operate.getString("code"))) {
                log.error(TAG + ", 获取爬虫信息失败：" + operate.getString("message"));
                return new ResultMessage(ResponseEnum.M3014);
            }
            JSONObject data = operate.getJSONObject("data");
            String status = data.getString("status");
            String message = data.getString("message");
            if (Constants.CRAWLER_STATUS_Success.equals(status)) {
                log.error(TAG + ", 数据爬取成功");
                // 可获取爬取结果
                JSONObject result = operateCrawler(httpservice, Constants.CRAWLER_OPERATE_METHOD_CrawlerGetData, crawlerId, crawlerToken);

                return new ResultMessage(ResponseEnum.M2000);
            } else if (Constants.CRAWLER_STATUS_Failure.equals(status)) {
                log.error(TAG + ", 数据爬取失败：" + message);
                return new ResultMessage(ResponseEnum.M3014);
            } else if (Constants.CRAWLER_STATUS_Crawling.equals(status)) {
                // do nothing
                // 等待五秒后再次尝试
            } else if (Constants.CRAWLER_STATUS_WaitAppendData.equals(status)) {
                log.error(TAG + ", 数据爬取需要追加算失败：" + message);
                return new ResultMessage(ResponseEnum.M3014);
            }
        }
    }

    public JSONObject operateCrawler(HttpService httpService, String method,
                                     String crawlerId, String crawlerToken) {
        return operateCrawler(httpService, method, crawlerId, crawlerToken, null);
    }

    public JSONObject operateCrawler(HttpService httpService, String method,
                                     String crawlerId, String crawlerToken, String appendData) {
        System.out.println("Try to operate crawler: method=" + method);

        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("crawlerId", crawlerId);
        params.put("crawlerToken", crawlerToken);
        if (appendData != null) {
            params.put("appendData", appendData);
        }
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        params.forEach((k, v) -> {
            nvps.add(new BasicNameValuePair(k, v));
        });

        HttpPost post = new HttpPost(Constants.CRAWLER_OPERATE_URL);
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String content = httpService.execAndReturnString(post);
        System.out.println(">> operateCrawler :" + content);
        return JSON.parseObject(content);
    }

    /**
     * 第一次调用,请求接口
     *
     * @param httpservice
     * @param mobile
     * @param password
     * @param identityName
     * @param identityNo
     * @return
     */
    public JSONObject requestMobileCrawler(HttpService httpservice, String mobile, String password, String identityName, String identityNo, String extraParam) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appId", Constant.CAO_PAN_SHOU_APP_ID);
        params.put("crawlerType", Constants.crawlerTypeOperatorReport2);
        params.put("username", mobile);
        params.put("password", password);
        params.put("identityName", identityName);
        params.put("identityNo", identityNo);
        params.put("extraParam", extraParam);
        params.put("timestamp",
                String.valueOf(System.currentTimeMillis() / 1000));
        String sign = SignUtil.sign(params, Constant.CAO_PAN_SHOU_APP_SECRET);
        params.put("sign", sign);
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        params.forEach((k, v) -> {
            nvps.add(new BasicNameValuePair(k, v));
        });

        HttpPost post = new HttpPost(Constants.CRAWLER_CREATE_URL);
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String content = httpservice.execAndReturnString(post);
        System.out.println(content);
        // {"code":"0000","crawlerId":"2019100819123010709472","crawlerToken":"QnXmYV"}
        return JSON.parseObject(content);
    }
}
