package com.will.tidyspider.core.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shangru_yu.
 */
public class Request implements Serializable{
    public enum Priority {
        One(1), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9), Ten(10);

        private Priority(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }

        public static Priority fromValue(int value) {
            for (Priority status : Priority.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            //default value
            return One;
        }
    }

    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times_";
    private static final String REQUEST_RETRY_TIMES = "_request_retry_times";
    private static final String POST_BODY_TEXT = "_post_body_name_value_text_";
    private static final String POST_BODY_FORM = "_post_body_name_value_pairs_";
    private static final String POST_BODY_BYTES = "_post_body_raw_bytes_";
    private static final String POST_BODY_CONTENT_TYPE = "_post_body_content_type_";
    private static final String POST_BODY_CHARSET = "_post_body_charset_";

    private static final String COOKIE_STRING = "_http.cookies_";
    private static final String PAGEPROCESSOR_NAME = "_http.async.page.processer.name_";
    private static final String DOWNLOAD_JOB = "_http.async.downloadjop_";
    private static final String AUTHIMAGE_JOB = "_http.async.authimage_";

    private Map<String, String> headers = new HashMap<String, String>();

    private String responseCharset = "UTF-8";

    private Boolean needProxy = false;

    private Integer timeOut = null;

    private Integer responseType = 1;

    private String url;

    private String method = HttpConstant.Method.GET;

    /**
     * Store additional information in extras.
     */
    private Map<String, Object> extras;

    private Priority priority = Priority.One;

    public Request() {
    }

    public Request(String url) {
        this.url = url;
    }

    public String getResponseCharset(){
        return responseCharset;
    }

    public Request setResponseCharset(String charset){
        this.responseCharset = charset;
        return this;
    }

    public Boolean getNeedProxy(){
        return needProxy;
    }

    public Request setNeedProxy(Boolean needProxy){
        this.needProxy = needProxy;
        return this;
    }

    public Integer getTimeOut(){
        return timeOut;
    }

    public Request setTimeOut(Integer timeOut){
        this.timeOut = timeOut;
        return this;
    }

    public Integer getResponseType(){
        return responseType;
    }

    public Request setResponseType(Integer responseType){
        this.responseType = responseType;
        return this;
    }

    public Priority getPriority() {
        return priority;
    }

    public Request setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    private List<HashMap<String, Object>> handleJSONArray(String key, JSONArray jsonArray){
        List list = new ArrayList();
        for (Object object : jsonArray) {
            if (object instanceof String) {
                list.add(object);
            } else if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                if (key.equals(POST_BODY_FORM)) {
                    list.add(new KeyValuePair(jsonObject.getString("key"), jsonObject.getString("value")));
                } else {
                    HashMap map = new HashMap<String, Object>();
                    for (Map.Entry entry : jsonObject.entrySet()) {
                        if (entry.getValue() instanceof JSONArray) {
                            map.put(entry.getKey(), handleJSONArray(key, (JSONArray) entry.getValue()));
                        } else {
                            map.put(entry.getKey(), entry.getValue());
                        }
                    }
                    list.add(map);
                }
            }
        }
        return list;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        if (this.extras == null) {
            this.extras = new HashMap<String, Object>();
        } else {
            this.extras.clear();
        }
        for (Map.Entry<String, Object> entry : extras.entrySet()) {
            if (entry.getValue() instanceof JSONArray) {
                List list = handleJSONArray(entry.getKey(), (JSONArray)entry.getValue());
                this.extras.put(entry.getKey(), list);
            }
            else {
                this.extras.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public Object getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    public Request putExtra(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
        }
        extras.put(key, value);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Request addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Request setCookieString(String cookie) {
        return setCookieString(cookie, false);
    }

    public Request setCookieString(String cookie, boolean clear) {
        if (cookie == null || cookie.length() == 0) {
            return this;
        }
        cookie = cookie.replaceAll("; path=/", "");
        String old = (String) this.getExtra(COOKIE_STRING);
        if (old == null || clear) {
            this.putExtra(COOKIE_STRING, cookie);
        } else {
            this.putExtra(COOKIE_STRING, old + "; " + cookie);
        }
        return this;
    }

    public String getCookisString() {
        return (String) this.getExtra(COOKIE_STRING);
    }

    public void setPostBodyBytes(byte[] bytes, String content_type, String charset_name) {
        if (bytes != null && bytes.length != 0 && content_type != null && charset_name != null)
        {
            this.putExtra(POST_BODY_BYTES, Base64.encodeBase64String(bytes));
            this.putExtra(POST_BODY_CONTENT_TYPE, content_type);
            this.putExtra(POST_BODY_CHARSET, charset_name);
        }
    }

    public String getPageprocesserName() {
        return (String) this.getExtra(PAGEPROCESSOR_NAME);
    }

    public void setPageprocesserName(String pageClassName) {
        if (pageClassName != null && pageClassName.length() != 0) {
            this.putExtra(PAGEPROCESSOR_NAME, pageClassName);
        }
    }

    public boolean isDownloadJob() {
        Boolean ret = (Boolean) this.getExtra(DOWNLOAD_JOB);
        if (ret == null) {
            return false;
        }
        return ret.booleanValue();
    }

    public void setDownloadJob(boolean bo) {
        this.putExtra(DOWNLOAD_JOB, bo);
    }

    public boolean isAuthImageJob() {
        Boolean ret = (Boolean) this.getExtra(AUTHIMAGE_JOB);
        if (ret == null) {
            return false;
        }
        return ret.booleanValue();
    }

    public void setPostBodyText(String text, String charset_name){
        if(null != text && null != charset_name){
            this.putExtra(POST_BODY_TEXT, text);
            this.putExtra(POST_BODY_CHARSET, charset_name);
        }
    }

    public String getPostBodyText(){
        return (String) this.getExtra(POST_BODY_TEXT);
    }

    public void setPostBodyForm(List<KeyValuePair> nameValuesPairs, String charset_name)
    {
        if (nameValuesPairs != null && nameValuesPairs.size() != 0 && charset_name != null)
        {
            this.putExtra(POST_BODY_FORM, nameValuesPairs);
            this.putExtra(POST_BODY_CHARSET, charset_name);
        }
    }

    public List<KeyValuePair> getPostBodyForm()
    {
        Object ret = this.getExtra(POST_BODY_FORM);
        if (ret instanceof List)
        {
            return new ArrayList<>((List<KeyValuePair>) ret);
        }
        return null;
    }

    public byte[] getPostBodyBytes()
    {
        String bs = (String)this.getExtra(POST_BODY_BYTES);
        if (bs != null) {
            return Base64.decodeBase64(bs);
        }
        return null;
    }

    public String getPostBodyContentType()
    {
        return (String)this.getExtra(POST_BODY_CONTENT_TYPE);
    }

    public String getPostBodyCharset()
    {
        return (String)this.getExtra(POST_BODY_CHARSET);
    }

    public Integer getRetryTimes() {
        if(this.getExtra(REQUEST_RETRY_TIMES) == null)
            setRetryTimes(0);
        return (Integer) this.getExtra(REQUEST_RETRY_TIMES);
    }

    public void setRetryTimes(Integer retryTimes) {
        this.putExtra(REQUEST_RETRY_TIMES, retryTimes);
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", extras=" + extras +
                "}";
    }
}
