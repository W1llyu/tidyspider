package com.will.tidyspider.core.collector;

import com.will.tidyspider.core.entity.Results;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by shangru_yu.
 */
public class ConsoleCollector implements Collector {
    @Override
    public void process(Results results){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(cal.getTime()) + " get page: " + results.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : results.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }
}
