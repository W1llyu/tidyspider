package com.will.tidyspider.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by will on 2017/9/20.
 */
public class FastJsonUtil {
    public static JSONArray toJsonArray(Object list) {
        return JSON.parseArray(JSON.toJSONString(list));
    }

    public static JSONObject toJsonObject(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj));
    }
}
