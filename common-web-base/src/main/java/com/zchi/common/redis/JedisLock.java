package com.zchi.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Random;

/**
 * 实现AutoCloseable，简化释放锁的操作
 */
public class JedisLock implements AutoCloseable{

    private static final Logger logger = LoggerFactory.getLogger(JedisLock.class);

    /**
     * 毫秒与毫微秒的换算单位 1毫秒 = 1000000毫微秒
     */
    public static final long MILLI_NANO_CONVERSION = 1000 * 1000L;
    /**
     * 默认超时时间（毫秒）
     */
    public static final long DEFAULT_TIME_OUT = 1000;
    public static final Random RANDOM = new Random();
    /**
     * 锁的超时时间（秒），过期删除
     */
    public static final int EXPIRE = 1;

    @Autowired private StringRedisTemplate stringRedisTemplate;
    @Autowired @Qualifier("setnxAndExpire") private RedisScript<Long> setnxAndExpireScript;

    private String key;
    // 锁状态标志
    public boolean locked = false;

    public JedisLock() {}
    /**
     * This creates a RedisLock
     *
     * @param key key
     */
    public JedisLock(String key) {
        this.key = key;
    }

    /**
     * @param timeout 超时时间
     * @return 成功或失败标志
     */
    public boolean lock(long timeout) {
        return this.lock(timeout, EXPIRE);
    }

    /**
     * @param timeout 超时时间
     * @param expire  锁的超时时间（秒），过期删除
     * @return 成功或失败标志
     */
    public boolean lock(long timeout, int expire) {
        Assert.notNull(this.key,"[Assertion failed] - this argument is required; it must not be null");
        long nano = System.nanoTime();
        timeout *= MILLI_NANO_CONVERSION;
        try {
            while ((System.nanoTime() - nano) < timeout) {
                Long result = stringRedisTemplate
                    .execute(setnxAndExpireScript, Arrays.asList(key), String.valueOf(expire));

                logger.info("result is: " + result);
                if (result != null && result == 1L) {
                    this.locked = true;
                    break;
                }
                // 短暂休眠，避免出现活锁
                Thread.sleep(3, RANDOM.nextInt(500));
            }
        } catch (Exception e) {
            logger.error("redis lock error", e);
        }
        return this.locked;
    }

    /**
     * @return 成功或失败标志
     */
    public boolean lock() {
        return lock(DEFAULT_TIME_OUT);
    }

    /**
     * 解锁
     */
    public void unlock() {
        try {
            if (this.locked) {
                stringRedisTemplate.delete(this.key);
                this.locked = false;
            }
        } catch (Exception e) {
            logger.error("jedis unlock error", e);
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override public void close() throws Exception {
        this.unlock();
    }
}
