package com.example.authorize.weixin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by bj_yangsong on 2017/8/31.
 * <p>
 * redis:
 * ip: 127.0.0.1
 * port: 6379
 * password: xuezhe1314521
 * timeout: 3000
 * database: 0
 * pool:
 * maxTotal: 300
 * maxIdle: 300
 * minIdle: 20
 * maxWaitMillis: 30000
 * testOnBorrow: false
 * testOnReturn: false
 * testWhileIdle: false
 */
@Component
public class RedisUtils {
    @Value("${spring.redis.pool.maxTotal}")
    private int maxTotal;
    @Value("${spring.redis.pool.maxIdle}")
    private int maxIdle;
    @Value("${spring.redis.pool.minIdle}")
    private int minIdle;
    @Value("${spring.redis.pool.maxWaitMillis}")
    private int maxWaitMillis;
    @Value("${spring.redis.pool.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${spring.redis.pool.testOnReturn}")
    private boolean testOnReturn;
    @Value("${spring.redis.pool.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${spring.redis.ip}")
    private String ip;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int database;
    private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    private volatile JedisPool jedisPool = null;

    public RedisUtils() {
    }

    public JedisPool getJedisPool() {
        if (jedisPool == null) {
            synchronized (this) {
                if (jedisPool == null) {
                    jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
                    jedisPoolConfig.setMaxTotal(maxTotal);
                    jedisPoolConfig.setMinIdle(minIdle);
                    jedisPoolConfig.setMaxIdle(maxIdle);
                    jedisPoolConfig.setTestOnBorrow(true);
                    jedisPoolConfig.setTestOnReturn(false);
                    jedisPoolConfig.setTestWhileIdle(testWhileIdle);
                    jedisPool = new JedisPool(jedisPoolConfig, ip, port, timeout, password, database);
                }
            }
        }
        return jedisPool;
    }
}
