package com.will.tidyspider.core.utils;

import com.will.tidyspider.core.Spider;
import com.will.tidyspider.core.config.ServerConfig;
import com.will.tidyspider.core.config.TaskConfig;
import com.will.tidyspider.core.config.impl.SimpleServerConfig;
import com.will.tidyspider.core.config.impl.SimpleTaskConfig;
import com.will.tidyspider.core.entity.Request;
import com.will.tidyspider.core.processor.PageProcessor;
import com.will.tidyspider.core.resources.RunTimeConfig;

/**
 * Created by will on 04/01/2018.
 */
public class SpiderUtil {
    public static void spiderEntryPoint(Request request, PageProcessor processor) {
        ServerConfig serverConfig = new SimpleServerConfig();
        TaskConfig taskConfig = new SimpleTaskConfig().setProcessor(processor);
        RunTimeConfig.getInstance().setServerConfig(serverConfig);
        RunTimeConfig.getInstance().setTaskConfig(taskConfig);
        Spider spider = new Spider();
        serverConfig.getScheduler().push(request);

        spider.once();
    }
}
