package com.will.tidyspider.core.scheduler;

import com.will.tidyspider.core.entity.Request;

/**
 * Created by shangru_yu.
 */
public interface Scheduler {
    void push(Request request);

    Request poll();
}
