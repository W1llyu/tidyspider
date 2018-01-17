package com.will.tidyspider.core.config;

import com.alibaba.fastjson.JSONObject;
import com.will.tidyspider.core.collector.Collector;
import com.will.tidyspider.core.downloader.Downloader;
import com.will.tidyspider.core.scheduler.Scheduler;

import java.util.List;

/**
 * Created by will on 04/01/2018.
 */
public interface ServerConfig {
    int getConcurrency();

    int getSleepTime();

    Scheduler getScheduler();

    Downloader getDownloader();

    List<Collector> getCollectors();

    JSONObject getAppConfig();

    JSONObject getSystemConfig();

    JSONObject getComponentsConfig();
}
