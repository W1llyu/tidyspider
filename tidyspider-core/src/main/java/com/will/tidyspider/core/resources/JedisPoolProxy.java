package com.will.tidyspider.core.resources;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Created by will on 03/01/2018.
 * Singleton
 */
public class JedisPoolProxy {
    private static JedisPoolProxy instance;

    private JedisPool pool;
    private String host = "localhost";
    private Integer port = 6379;
    private String password = "";
    private Integer dbIndex = 0;

    public static JedisPoolProxy getInstance() {
        if(instance == null) {
            synchronized (JedisPoolProxy.class) {
                if(instance == null) instance = new JedisPoolProxy();
            }
        }
        return instance;
    }

    public Jedis getJedis() {
        Jedis jedis = getJedisPool().getResource();
        jedis.select(dbIndex);
        return jedis;
    }

    public void recycleJedis(Jedis jedis) {
        getJedisPool().returnResource(jedis);
    }

    private JedisPoolProxy() {
        initConfig();
    }

    private JedisPool getJedisPool() {
        if(null == pool)
            this.pool = new JedisPool(
                    new JedisPoolConfig(), host, port, Protocol.DEFAULT_TIMEOUT, password.isEmpty() ? null : password);
        return pool;
    }

    private void initConfig() {
        JSONObject redisConfig = RunTimeConfig.getInstance().getServerConfig().getSystemConfig().getJSONObject("redis");
        if(redisConfig == null) return;
        if(redisConfig.containsKey("host")) this.host = redisConfig.getString("host");
        if(redisConfig.containsKey("port")) this.port = redisConfig.getInteger("port");
        if(redisConfig.containsKey("password")) this.password = redisConfig.getString("password");
        if(redisConfig.containsKey("db_index")) this.dbIndex = redisConfig.getInteger("db_index");
    }
}
