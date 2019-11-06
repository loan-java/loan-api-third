package com.mod.loan.util.caopanshou;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ReturnStringResponseHandler implements ResponseHandler<String> {

    public ReturnStringResponseHandler() {
    }

    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity, "UTF-8");
        return s;
    }
}
