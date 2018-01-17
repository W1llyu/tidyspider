package com.will.tidyspider.samples.csdn;

import com.will.tidyspider.core.entity.Request;
import com.will.tidyspider.core.utils.SpiderUtil;
import org.junit.Test;

/**
 * Created by will on 17/01/2018.
 */
public class TestCsdnListPageProcessor {
    @Test
    public void testProcess() {
        SpiderUtil.spiderEntryPoint(new Request("https://www.csdn.net/"), new CsdnListPageProcessor());
    }
}
