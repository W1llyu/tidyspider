package com.will.tidyspider.core.downloader;

import com.will.tidyspider.core.entity.HttpConstant;
import com.will.tidyspider.core.entity.KeyValuePair;
import com.will.tidyspider.core.entity.Page;
import com.will.tidyspider.core.entity.Request;
import com.will.tidyspider.core.selector.PlainText;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by will on 02/01/2018.
 */
public class HttpClientDownloader implements Downloader {

    public Page download(Request request) {
        HttpHost httpHost = buildHttpHost(request.getUrl());
        if(httpHost == null)
            return new Page();
        HttpClientContext httpClientContext = HttpClientContext.create();
        HttpUriRequest httpUriRequest = buildHttpUriRequest(request, httpClientContext);
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLContext(buildSSLContext())
                    .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            CloseableHttpResponse response = httpClient.execute(httpHost, httpUriRequest, httpClientContext);
            return handleResponse(request, response);
        } catch (IOException e) {
            return new Page();
        }
    }

    private SSLContext buildSSLContext() {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] certificate, String authType) {
                return true;
            }
        };
        SSLContext sslContext = null;
        try{
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        }catch(Exception ignored) {
            // Handle error
        }
        return sslContext;
    }

    private HttpHost buildHttpHost(String url) {
        URI u;
        try{
            u = new URI(url);
        }catch (Exception e){
            return null;
        }
        HttpHost target;
        if(u.getScheme().equals("https")){
            target = new HttpHost(u.getHost(), 443, "https");
        }else {
            target = new HttpHost(u.getHost(), u.getPort(), "http");
        }
        return target;
    }

    private HttpUriRequest buildHttpUriRequest(Request request, HttpContext httpContext) {
        RequestBuilder requestBuilder = selectRequestMethod(request);
        requestBuilder.removeHeaders("User-Agent");
        requestBuilder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.2; rv:45.0) Gecko/20100101 Firefox/45.0");

        // set request's header
        for (Map.Entry<String, String> headerEntry : request.getHeaders().entrySet()) {
            requestBuilder.removeHeaders(headerEntry.getKey());
            requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        if(request.getTimeOut() != null) {
            requestConfigBuilder.setConnectionRequestTimeout(request.getTimeOut())
                    .setSocketTimeout(request.getTimeOut())
                    .setConnectTimeout(request.getTimeOut());
        }

        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    private RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();
        if(method == null || method.equalsIgnoreCase(HttpConstant.Method.GET))
            return RequestBuilder.get();
        else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            List<NameValuePair> pairs = new ArrayList<>();
            for(KeyValuePair pair: request.getPostBodyForm()) {
                pairs.add(new BasicNameValuePair(pair.getKey(), pair.getValue()));
            }
            String charset = request.getPostBodyCharset() == null ? "utf-8" : request.getPostBodyCharset();
            if(!pairs.isEmpty())
                requestBuilder.setEntity(new UrlEncodedFormEntity(pairs, Charset.forName(charset)));
            else if(request.getPostBodyText() != null)
                requestBuilder.setEntity(new StringEntity(request.getPostBodyText(), Charset.forName(charset)));
            else {
                byte[] bytes = request.getPostBodyBytes();
                if (bytes != null)
                {
                    requestBuilder.setEntity(new ByteArrayEntity(
                            bytes, ContentType.create(request.getPostBodyContentType(), charset)));
                }
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD))
            return RequestBuilder.head();
        else if (method.equalsIgnoreCase(HttpConstant.Method.PUT))
            return RequestBuilder.put();
        else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE))
            return RequestBuilder.delete();
        else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE))
            return RequestBuilder.trace();
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }

    protected Page handleResponse(Request request, HttpResponse httpResponse) throws IOException {
        Page page = new Page();
        page.setRawText(EntityUtils.toString(httpResponse.getEntity(), request.getResponseCharset()));
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return page;
    }
}
