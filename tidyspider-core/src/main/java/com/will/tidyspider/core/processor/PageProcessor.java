package com.will.tidyspider.core.processor;

import com.will.tidyspider.core.entity.Page;

/**
 * Created by shangru_yu.
 */
public interface PageProcessor {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page
     */
    void process(Page page);
}
