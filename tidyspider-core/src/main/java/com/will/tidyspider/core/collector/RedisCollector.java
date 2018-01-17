package com.will.tidyspider.core.collector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.will.tidyspider.core.entity.Results;
import com.will.tidyspider.core.resources.JedisPoolProxy;
import com.will.tidyspider.core.resources.RunTimeConfig;
import redis.clients.jedis.Jedis;

/**
 * Created by shangru_yu.
 */
public class RedisCollector implements Collector {
    private final JedisPoolProxy poolProxy;
    private String taskName;

    private static final String SPIDER_RESULT_PREFIX = "com.willysr.spider.result_";

    public RedisCollector() {
        this.poolProxy = JedisPoolProxy.getInstance();
        this.taskName = RunTimeConfig.getInstance().getServerConfig().getAppConfig().getString("name");
    }

    private Jedis getJedis() {
        return poolProxy.getJedis();
    }

    @Override
    public void process(Results results){
        Jedis jedis = getJedis();
        try {
            String key;
            String value = JSON.toJSONString(results.getAll(), SerializerFeature.PrettyFormat);
            if (taskName != null) {
                key = SPIDER_RESULT_PREFIX + taskName + "_" + results.getTypeIdentifier();
            } else {
                key = SPIDER_RESULT_PREFIX + "#UNKNOWN_RESULT#_" + results.getTypeIdentifier();
            }
            long r = jedis.rpush(key, value);
        }
        finally {
            poolProxy.recycleJedis(jedis);
        }
    }
}
