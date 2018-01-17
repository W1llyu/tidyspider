package com.will.tidyspider.core.entity;

import com.will.tidyspider.core.processor.PageProcessor;

/**
 * Created by will on 03/01/2018.
 */
public class SpiderTask {
    private String name;
    private String userAgent = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
    private String charset = "UTF-8";
    private int retryTimes = 5;
    private int timeOut = 30000;
    private boolean needProxy = false;
    private Integer[] acceptStatusCode = {200};
    private PageProcessor processor = null;
    private String processorName = null;

    public SpiderTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public SpiderTask setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public SpiderTask setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public SpiderTask setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public SpiderTask setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public boolean getNeedProxy() {
        return needProxy;
    }

    public void setNeedProxy(boolean needProxy) {
        this.needProxy = needProxy;
    }

    public Integer[] getAcceptStatusCode() {
        return acceptStatusCode;
    }

    public void setAcceptStatusCode(Integer[] acceptStatusCode) {
        this.acceptStatusCode = acceptStatusCode;
    }

    public PageProcessor getProcessor() {
        return processor;
    }

    public SpiderTask setProcessor(String processorClass) {
        this.processorName = processorClass;
        try {
            Class c = Class.forName(processorClass);
            this.processor = (PageProcessor) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            this.processor = null;
        }
        return this;
    }

    public SpiderTask setProcessor(PageProcessor processor) {
        this.processor = processor;
        this.processorName = processor.getClass().getName();
        return this;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }
}
