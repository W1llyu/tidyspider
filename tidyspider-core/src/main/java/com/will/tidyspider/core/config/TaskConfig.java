package com.will.tidyspider.core.config;

import com.will.tidyspider.core.entity.SpiderTask;

/**
 * Created by will on 04/01/2018.
 */
public interface TaskConfig {
    SpiderTask fetchSpiderTask(String url);
}
