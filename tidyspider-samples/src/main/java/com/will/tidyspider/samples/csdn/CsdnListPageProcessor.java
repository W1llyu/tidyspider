package com.will.tidyspider.samples.csdn;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.will.tidyspider.core.entity.Page;
import com.will.tidyspider.core.processor.PageProcessor;

import java.util.List;

import static com.will.tidyspider.core.selector.Selectors.xpath;

/**
 * Created by will on 17/01/2018.
 */
public class CsdnListPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        List<String> articleTmp = page.getHtml().xpath("//div[@class='csdn-tracking-statistics']").all();
        JSONArray articles = new JSONArray();
        for(String tmp: articleTmp) {
            articles.add(new JSONObject() {{
                put("title", xpath("//allText()").select(tmp).trim());
                put("link", xpath("//a/@href").select(tmp).trim());
            }});
        }
        page.putField("list", articles);
    }
}
