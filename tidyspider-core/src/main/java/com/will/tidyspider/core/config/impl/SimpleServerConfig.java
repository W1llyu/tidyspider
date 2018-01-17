package com.will.tidyspider.core.config.impl;

import com.alibaba.fastjson.JSONObject;
import com.will.tidyspider.core.collector.Collector;
import com.will.tidyspider.core.collector.ConsoleCollector;
import com.will.tidyspider.core.config.ServerConfig;
import com.will.tidyspider.core.downloader.Downloader;
import com.will.tidyspider.core.downloader.OKHTTPDownloader;
import com.will.tidyspider.core.scheduler.RedisScheduler;
import com.will.tidyspider.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by will on 04/01/2018.
 */
public class SimpleServerConfig implements ServerConfig {
    private Scheduler scheduler;

    private Downloader downloader;

    private List<Collector> collectors = new ArrayList<>();

    @Override
    public int getConcurrency() {
        return 10;
    }

    @Override
    public int getSleepTime() {
        return 5;
    }

    @Override
    public Scheduler getScheduler() {
        if(scheduler == null)
            scheduler = new RedisScheduler();
        return scheduler;
    }

    @Override
    public Downloader getDownloader() {
        if(downloader == null)
            downloader = new OKHTTPDownloader();
        return downloader;
    }

    @Override
    public List<Collector> getCollectors() {
        if(collectors == null || collectors.isEmpty())
            collectors = new ArrayList<Collector>() {{
               add(new ConsoleCollector());
            }};
        return collectors;
    }

    @Override
    public JSONObject getComponentsConfig() {
        return new JSONObject();
    }

    @Override
    public JSONObject getSystemConfig() {
        return new JSONObject();
    }

    @Override
    public JSONObject getAppConfig() {
        return new JSONObject();
    }
}
