package com.example.authorize.weixin.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisAskDataException;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.Set;

import static com.example.authorize.weixin.consts.TokenManageConsts.DEFAULT_VALUE;
import static com.example.authorize.weixin.consts.TokenManageConsts.perfix;

@Service
public class RedisService implements ExpireKey {
	
	private static Logger logger = LoggerFactory.getLogger(RedisService.class);

	@Autowired
	RedisConfig redisConfig;
	private JedisPool pool;

	public RedisService() {
	}

	public RedisService(JedisPool pool) {
		this.pool = pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	private Jedis getJedis() {
		return redisConfig.getJedisPool().getResource();
	}

	private void closeJedis(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	@Override
	public boolean add(String key, int expire) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.setex( key, expire, DEFAULT_VALUE);
			return true;
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	@Override
	public boolean add(String key) {
		return add(key, DEFAULT_EXPIRE);
	}

	@Override
	public boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.exists(key);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}


	/**
	 * 加缓存锁
	 *
	 * @param key
	 * @param expireSeconds
	 * @return true是加锁成功，false是加锁失败说明锁已存在
	 */
	public boolean lock(String key, int expireSeconds) {
		Jedis jedis = null;
		boolean res;
		try {
			jedis = getJedis();
			Long setnxResult = jedis.setnx(key, "1");
			res = setnxResult != null && setnxResult > 0;
			if (res && expireSeconds > 0) {
				jedis.expire(key, expireSeconds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return res;
	}

	/**
	 * 去掉缓存锁
	 *
	 * @param key
	 * @return
	 */
	public boolean releaseLock(String key) {
		return delete(key) > 0;
	}

	public String get(String key)throws Exception{
		String result =null;
		Jedis jedis =null;
		try{
			jedis=pool.getResource();
			result= jedis.get(key);
		}catch (Exception e){
			logger.error("",e);
			throw new Exception("redis 获取数据失败!");
		}finally {
			if(jedis != null){
				jedis.close();
			}
		}
		return result;
	}


	/**
	 * 存储单个字符串
	 *
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public void set(String key, String value, int seconds) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.set(key, value);
			if (seconds > 0) {
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
	}


	/**
	 * 如果不存在，则 SET
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public Long setnx(String key, String value) {
		Jedis jedis = null;
		Long res = null;
		try {
			jedis = getJedis();
			res = jedis.setnx(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return res;
	}

	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public String getset(String key, String value, int seconds) {
		String oldValue = "";
		Jedis jedis = null;
		try {
			jedis = getJedis();
			oldValue = jedis.getSet(key, value);
			if (seconds > 0) {
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 获取数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return oldValue;
	}


	/**
	 * 将值存储到set中
	 *
	 * @param second
	 * @param key
	 * @param value
	 */
	public void sadd(int second, String key, String... value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.sadd(key, value);
			if (second > 0) {
				jedis.expire(key, second);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 将值存储到set中
	 *
	 * @param key
	 */
	public Set<String> smembers(String key) {
		Jedis jedis = null;
		Set<String> set = null;
		try {
			jedis = getJedis();
			set = jedis.smembers(key);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return set;
	}

	/**
	 * 将值存储到set中 (不过期)
	 *
	 * @param key
	 * @param value
	 */
	public void sadd(String key, String... value) {
		sadd(-1, key, value);
	}

	/**
	 * 判断是否在集合中
	 */
	public Long isSadd(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.sadd(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeJedis(jedis);
		}
		return 0L;
	}

	/**
	 * 判断是否在集合中,不在集合中则添加,并设置过期时间
	 */
	public Long isSaddTime(String key, String value, int second) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return this.saddL(second, key, value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeJedis(jedis);
		}
		return 0L;
	}

	/**
	 * 将值存储到set中
	 *
	 * @param second
	 * @param key
	 * @param value
	 */
	public Long saddL(int second, String key, String... value) {
		Long lg = 0l;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			lg = jedis.sadd(key, value);
			if (second > 0) {
				jedis.expire(key, second);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return lg;
	}

	/**
	 * 存储单个字符串 （不过期）
	 *
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		set(key, value, -1);
	}

	/**
	 * 删除值
	 *
	 * @param keys
	 * @return
	 */
	public long delete(String... keys) {
		long value = -1;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			value = jedis.del(keys);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 删除数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return value;
	}

	/**
	 * 设置过期时间
	 *
	 * @param key
	 * @param seconds
	 */
	public void setExpires(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			if (seconds > 0) {
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 设置过期时间错误!", e);
		} finally {
			closeJedis(jedis);
		}

	}

	/**
	 * 向队列中插入数据
	 *
	 * @param key
	 * @param strings
	 * @return
	 */
	public long lpush(String key, final String... strings) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.lpush(key, strings);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}

	public String ltrim(String key, long start, long end) {
		String result = "";
		Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.ltrim(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}

	public long lLength(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.llen(key);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
	}

	public List<String> lRange(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.lrange(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
	}

	public void clearKey(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis 存储数据错误!", e);
		} finally {
			closeJedis(jedis);
		}
	}

	public long rpush(String key, String... values) {
		Jedis jedis = null;
		long result = -1;
		try {
			jedis = getJedis();
			result = jedis.rpush(key, values);
		} catch (Exception e) {
			throw e;
		} finally {
			closeJedis(jedis);
		}
		return result;
	}

	public String rename(String key, String newKey) {
		Jedis jedis = null;
		String result = "";
		try {
			jedis = getJedis();
			result = jedis.rename(key, newKey);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis rename 错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}

	public long lrem(String key, long count, String values) {
		Jedis jedis = null;
		long result = -1;
		try {
			jedis = getJedis();
			result = jedis.lrem(key, count, values);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis rpush 错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}


	public String lpop(String key) {
		Jedis jedis = null;
		String result = "";
		try {
			jedis = getJedis();
			result = jedis.lpop(key);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis lpop 错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}

	public String rpop(String key) {
		Jedis jedis = null;
		String result = "";
		try {
			jedis = getJedis();
			result = jedis.rpop(key);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis rpop 错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}


	public long llen(String key) {
		Jedis jedis = null;
		long result = -1;
		try {
			jedis = getJedis();
			result = jedis.llen(key);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JedisConnectionException("redis llen 错误!", e);
		} finally {
			closeJedis(jedis);
		}
		return result;
	}

}
