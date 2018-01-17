package com.will.tidyspider.core.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.will.tidyspider.core.entity.Request;
import com.will.tidyspider.core.resources.JedisPoolProxy;
import com.will.tidyspider.core.resources.RunTimeConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by shangru_yu.
 */
public class RedisScheduler implements Scheduler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    //list, urls
    private static final String URL_QUEUE_PREFIX = "com.willysr.spider.urls_queue_priority_";
    //set, fetched url
    private static final String URL_FETCHED_PREFIX = "com.willysr.spider.urls_fetched_";
    // hmap, urls' meta data
    private static final String URL_META_KEYS = "com.willysr.spider.urls_meta";

    private static final int blockTimeOut = 60000;

    private final JedisPoolProxy poolProxy;

    private String taskName = "tidy_spider";
    // 是否支持重复任务
    private Boolean taskDup = true;

    public RedisScheduler() {
        this.poolProxy = JedisPoolProxy.getInstance();
        initConfig();
    }

    public boolean isDuplicate(Request request){
        Jedis jedis = getJedis();
        try {
            String key = URL_FETCHED_PREFIX + this.taskName;
            boolean isDuplicate = jedis.sismember(key, request.getUrl());
            if (!isDuplicate) {
                jedis.sadd(key, request.getUrl());
                Long ttl = jedis.ttl(key);
                if (ttl == -1) {
                    // set ttl to the fetched key
                    jedis.expire(key, 86400); // default one day
                }
            }
            return isDuplicate;
        } finally {
            poolProxy.recycleJedis(jedis);
        }
    }

    @Override
    public void push(Request request){
        if(taskDup){
            pushWhenNoDuplicate(request);
        } else{
            if(!isDuplicate(request)){
                pushWhenNoDuplicate(request);
            }
        }
    }

    protected void pushWhenNoDuplicate(Request request) {
        Jedis jedis = getJedis();
        try {
            jedis.rpush(URL_QUEUE_PREFIX + this.taskName + "_" + request.getPriority().toString(), request.getUrl());
            logger.debug("push request {} to priority {}", request.getUrl(), request.getPriority().toString());
            if (request.getExtras() != null) {
                String field = DigestUtils.shaHex(request.getUrl());
                String value = JSON.toJSONString(request);
                jedis.hset(URL_META_KEYS, field, value);
            }
        } finally {
            poolProxy.recycleJedis(jedis);
        }
    }

    @Override
    public Request poll(){
        Jedis jedis = getJedis();
        try {
            for(int i = 10; i >= 1; i--) {
                String key = URL_QUEUE_PREFIX + this.taskName + "_" + Request.Priority.fromValue(i).toString();
                String url;
                url = Math.random() >= 0.5 ? jedis.lpop(key) : jedis.rpop(key);
                if (null == url) {
                    continue;
                }
                String field = DigestUtils.shaHex(url);
                byte[] bytes = jedis.hget(URL_META_KEYS.getBytes(), field.getBytes());
                if (bytes != null) {
                    Request o = JSON.parseObject(new String(bytes), Request.class);
                    o.setPriority(Request.Priority.fromValue(i));
                    jedis.hdel(URL_META_KEYS.getBytes(), field.getBytes());
                    return o;
                }
                Request request = new Request(url);
                request.setPriority(Request.Priority.fromValue(i));
                return request;
            }
            return null;
        } finally {
            poolProxy.recycleJedis(jedis);
        }
    }

    private Jedis getJedis(){
        return poolProxy.getJedis();
    }

    private void initConfig() {
        JSONObject appConfig = RunTimeConfig.getInstance().getServerConfig().getAppConfig();
        if(appConfig.containsKey("name")) this.taskName = appConfig.getString("name");
        if(appConfig.containsKey("duplicate_task")) this.taskDup = appConfig.getBoolean("duplicate_task");
    }
}
