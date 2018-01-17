package com.will.tidyspider.core.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.will.tidyspider.core.processor.PageProcessor;
import com.will.tidyspider.core.selector.Html;
import com.will.tidyspider.core.selector.Selectable;
import com.will.tidyspider.core.utils.UrlUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shangru_yu.
 */
public class Page {
    private Request request;

    private List<Results> results = new ArrayList<Results>();

    private Html html;

    private JSONObject json;

    private String rawText;

    private Selectable url;

    private int statusCode;

    private boolean needCycleRetry;

    private final List<Request> targetRequests = new ArrayList<Request>();

    public Page() {
        results.add(new Results());
    }

    public Page setSkip(boolean skip) {
        results.get(0).setSkip(skip);
        return this;
    }

    public Page setSkip(boolean skip, int index) {
        Results item = getResultAtIndex(index);
        if (item != null) {
            item.setSkip(skip);
        }
        return this;
    }

    /**
     * store extract results
     *
     * @param key
     * @param field
     */
    public void putField(String key, Object field) {
        results.get(0).put(key, field);
    }

    public void putField(String key, Object field, int index) {
        Results item = getResultAtIndex(index);
        if (item != null) {
            item.put(key, field);
        }
    }

    public Request addDownloadJob(String url, PageProcessor processor) {
        Request request = new Request(url);
        request.setDownloadJob(true);
        request.setPageprocesserName(processor.getClass().getCanonicalName());
        this.addTargetRequest(request);
        return request;
    }

    // TBD:
    public String downloadImageToImageServer(String url) {
        return "";
    }

    private Results getResultAtIndex(int index) {
        if (index >= 0 && index < 1024) {
            if ((index+1) > results.size()) {
                while (results.size() < (index+1)) {
                    Results item = new Results();
                    item.setRequest(results.get(0).getRequest());
                    results.add(item);
                }
            }
            return results.get(index);
        }
        return null;
    }

    /**
     * get html content of page
     *
     * @return html
     */
    public Html getHtml() {
        if (html == null) {
            html = new Html(UrlUtil.fixAllRelativeHrefs(rawText, request.getUrl()));
        }
        return html;
    }

    /**
     * get json content of page
     *
     * @return JSONObject
     * @since 0.5.0
     */
    public JSONObject getJson() {
        if (json == null) {
            try {
                json = JSON.parseObject(rawText);
            } catch (Exception ignored) {

            }
        }
        return json;
    }

    /**
     * @param html
     * @deprecated since 0.4.0
     * The html is parse just when first time of calling {@link #getHtml()}, so use {@link #setRawText(String)} instead.
     */
    public void setHtml(Html html) {
        this.html = html;
    }

    public List<Request> getTargetRequests() {
        return targetRequests;
    }

    /**
     * add urls to fetch
     *
     * @param requests
     */
    public void addTargetRequests(List<String> requests) {
        synchronized (targetRequests) {
            for (String s : requests) {
                if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                    continue;
                }
                s = UrlUtil.canonicalizeUrl(s, url.toString());
                targetRequests.add(new Request(s).setPriority(this.request.getPriority()).
                        setCookieString(this.request.getCookisString()));
            }
        }
    }

    /**
     * add urls to fetch
     *
     * @param requests
     */
    public void addTargetRequests(List<String> requests, Request.Priority priority) {
        synchronized (targetRequests) {
            for (String s : requests) {
                if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                    continue;
                }
                s = UrlUtil.canonicalizeUrl(s, url.toString());
                targetRequests.add(new Request(s).setPriority(priority).
                        setCookieString(this.request.getCookisString()));
            }
        }
    }

    /**
     * add url to fetch
     *
     * @param requestString
     */
    public void addTargetRequest(String requestString) {
        if (StringUtils.isBlank(requestString) || requestString.equals("#")) {
            return;
        }
        synchronized (targetRequests) {
            requestString = UrlUtil.canonicalizeUrl(requestString, url.toString());
            targetRequests.add(new Request(requestString).setPriority(this.request.getPriority()).
                    setCookieString(this.request.getCookisString()));
        }
    }

    /**
     * add requests to fetch
     *
     * @param request
     */
    public void addTargetRequest(Request request) {
        synchronized (targetRequests) {
            targetRequests.add(request.setPriority(request.getPriority()).
                    setCookieString(this.request.getCookisString()));
        }
    }

    /**
     * get url of current page
     *
     * @return url of current page
     */
    public Selectable getUrl() {
        return url;
    }

    public void setUrl(Selectable url) {
        this.url = url;
    }

    /**
     * get request of current page
     *
     * @return request
     */
    public Request getRequest() {
        return request;
    }

    public boolean isNeedCycleRetry() {
        return needCycleRetry;
    }

    public void setNeedCycleRetry(boolean needCycleRetry) {
        this.needCycleRetry = needCycleRetry;
    }

    public void setRequest(Request request) {
        this.request = request;
        for (Results result : this.results) {
            result.setRequest(request);
        }
    }

    public Results[] getResultItems() {
        return results.toArray(new Results[results.size()]);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRawText() {
        return rawText;
    }

    public Page setRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    public void reDownloadUrl() {
        addTargetRequest(this.getRequest());
        setNeedCycleRetry(true);
        "".substring(2);
    }

    @Override
    public String toString() {
        return "Page{" +
                "request=" + request +
                ", resultItems=" + results +
                ", rawText='" + rawText + '\'' +
                ", url=" + url +
                ", statusCode=" + statusCode +
                ", targetRequests=" + targetRequests +
                '}';
    }
}
