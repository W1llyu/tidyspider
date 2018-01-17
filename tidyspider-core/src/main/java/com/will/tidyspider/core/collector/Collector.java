package com.will.tidyspider.core.collector;

import com.will.tidyspider.core.entity.Results;

/**
 * Created by shangru_yu.
 */
public interface Collector {
    void process(Results results);
}
