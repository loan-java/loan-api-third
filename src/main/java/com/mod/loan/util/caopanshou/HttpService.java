package com.mod.loan.util.caopanshou;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class HttpService {

    private CloseableHttpClient httpClient;

    private HttpService() {
    }

    public static HttpService newInstance() {
        HttpService impl = new HttpService();
        impl.httpClient = HttpClients.custom().build();
        return impl;
    }

    public String execAndReturnString(HttpRequestBase request) {
        try {
        	RequestConfig config = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
			request.setConfig(config);
            return httpClient.execute(request, new ReturnStringResponseHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
