package com.will.tidyspider.core.downloader;

import com.will.tidyspider.core.entity.HttpConstant;
import com.will.tidyspider.core.entity.KeyValuePair;
import com.will.tidyspider.core.entity.Page;
import com.will.tidyspider.core.entity.Request;
import com.will.tidyspider.core.selector.PlainText;
import okhttp3.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by will on 02/01/2018.
 */
public class OKHTTPDownloader implements Downloader {
    public Page download(Request request) {
        Response response = getHTTPResponse(buildRequest(request), request.getTimeOut());
        try {
            return generatePage(request, response);
        } catch (IOException e) {
            LoggerFactory.getLogger(this.getClass())
                    .error("Process request {} error {} {}", request.getUrl(), e, e.getStackTrace()[0].toString());
            return new Page();
        }
    }

    private okhttp3.Request buildRequest(Request request) {
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(request.getUrl());
        for (Map.Entry entry: request.getHeaders().entrySet())
            requestBuilder.header((String) entry.getKey(), (String) entry.getValue());
        if(request.getMethod().equals(HttpConstant.Method.POST))
            requestBuilder.post(buildBody(request));
        return requestBuilder.build();
    }

    private RequestBody buildBody(Request request) {
        if(request.getPostBodyForm() != null && !request.getPostBodyForm().isEmpty()) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (KeyValuePair pair: request.getPostBodyForm())
                formBodyBuilder.add(pair.getKey(), pair.getValue());
            return formBodyBuilder.build();
        } else {
            String charset = request.getPostBodyCharset();
            MediaType JSON = MediaType.parse(
                    String.format("application/json; charset=%s", charset == null ? "utf-8" : charset));
            return RequestBody.create(JSON, request.getPostBodyText());
        }
    }

    private Response getHTTPResponse(okhttp3.Request request, Integer timeOut) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        if(timeOut != null)
            okHttpClientBuilder.readTimeout(new Long(timeOut), TimeUnit.SECONDS)
                    .writeTimeout(new Long(timeOut), TimeUnit.SECONDS)
                    .connectTimeout(new Long(timeOut), TimeUnit.SECONDS);
        OkHttpClient okHttpClient = okHttpClientBuilder.build();
        Call call = okHttpClient.newCall(request);
        try {
            return call.execute();
        } catch (IOException e) {
            return null;
        }
    }

    private Page generatePage(Request request, Response response) throws IOException {
        Page page = new Page();
        page.setRawText(response.body().string());
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(response.code());
        return page;
    }
}
