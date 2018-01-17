package com.will.tidyspider.core.downloader;

import com.will.tidyspider.core.entity.Page;
import com.will.tidyspider.core.entity.Request;

/**
 * Created by will on 02/01/2018.
 */
public interface Downloader {
    Page download(Request request);
}
