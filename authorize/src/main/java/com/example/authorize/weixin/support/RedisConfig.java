package com.example.authorize.weixin.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PreDestroy;

@Component
public class RedisConfig {
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

    public RedisConfig() {
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
    @PreDestroy
    public void preDestroy(){
        if (jedisPool != null) {
            jedisPool.destroy();
        }
    }
}
