package com.will.tidyspider.core.config.impl;

import com.will.tidyspider.core.config.TaskConfig;
import com.will.tidyspider.core.entity.SpiderTask;
import com.will.tidyspider.core.processor.PageProcessor;

/**
 * Created by will on 04/01/2018.
 */
public class SimpleTaskConfig implements TaskConfig {
    private SpiderTask spiderTask;

    public SimpleTaskConfig() {
        initSpiderTask();
    }

    @Override
    public SpiderTask fetchSpiderTask(String url) {
        return spiderTask;
    }

    public SimpleTaskConfig setUserAgent(String userAgent) {
        spiderTask.setUserAgent(userAgent);
        return this;
    }

    public SimpleTaskConfig setCharset(String charset) {
        spiderTask.setCharset(charset);
        return this;
    }

    public SimpleTaskConfig setRetryTimes(int retryTimes) {
        spiderTask.setRetryTimes(retryTimes);
        return this;
    }

    public SimpleTaskConfig setTimeOut(int timeOut) {
        spiderTask.setTimeOut(timeOut);
        return this;
    }

    public SimpleTaskConfig setProcessor(PageProcessor processor) {
        spiderTask.setProcessor(processor);
        return this;
    }

    private void initSpiderTask() {
        spiderTask = new SpiderTask("simple");
    }
}
