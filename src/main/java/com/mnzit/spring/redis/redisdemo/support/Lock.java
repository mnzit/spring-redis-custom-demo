package com.mnzit.spring.redis.redisdemo.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public class Lock {

    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Return value after calling set
     */
    private static final String OK = "OK";

    /**
     * Default request lock timeout (MSMs)
     */
    private static final long TIME_OUT = 100;

    /**
     * The effective time of the default lock (s)
     */
    private static final int EXPIRE = 60;

    /**
     * Unlock lua script
     */
    private static final String UNLOCK_LUA;

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] "
                + "then "
                + " return redis.call(\"del\",KEYS[1]) "
                + "else "
                + " return 0 "
                + "end ";
    }

    /**
     * Key corresponding to lock flag
     */
    private String lockKey;

    /**
     * Key corresponding to the lock flag recorded to the log
     */
    private String lockKeyLog = "";

    /**
     * Lock corresponding value
     */
    private String lockValue;

    /**
     * Lock Effective Time (s)
     */
    private int expireTime = EXPIRE;

    /**
     * Request lock timeout (ms)
     */
    private long timeOut = TIME_OUT;

        /**
     * Lock Mark
     */
    private volatile boolean locked = false;

    private final Random random = new Random();

    /**
     * Use the default lock expiration time and request lock timeout
     *
     * @param redisTemplate Redis client
     * @param lockKey       key (Redis Key）
     */
    public Lock(RedisTemplate<String, Object> redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey + "_lock";
    }

    /**
     * Specify the lock expiration time using the default request lock timeout
     *
     * @param redisTemplate Redis client
     * @param lockKey       key (Redis Key）
     * @param expireTime    lock expiration time in seconds)
     */
    public Lock(RedisTemplate<String, Object> redisTemplate, String lockKey, int expireTime) {
        this(redisTemplate, lockKey);
        this.expireTime = expireTime;
    }

    /**
     * Specify the timeout for the requested lock using the default lock Expiration Time
     *
     * @param redisTemplate Redis client
     * @param lockKey       key (Redis Key）
     * @param timeOut       request lock timeout (in milliseconds)
     */
    public Lock(RedisTemplate<String, Object> redisTemplate, String lockKey, long timeOut) {
        this(redisTemplate, lockKey);
        this.timeOut = timeOut;
    }

    /**
     * The expiration time of the lock and the timeout time of the requested lock are both specified values
     *
     * @param redisTemplate Redis client
     * @param lockKey       key (Redis Key）
     * @param expireTime    lock expiration time in seconds)
     * @param timeOut       request lock timeout (in milliseconds)
     */
    public Lock(RedisTemplate<String, Object> redisTemplate, String lockKey, int expireTime, long timeOut) {
        this(redisTemplate, lockKey, expireTime);
        this.timeOut = timeOut;
    }

    /**
     * Try to get lock timeout back
     *
     * @return boolean
     */
    public boolean tryLock() {
        // Generate random key
        this.lockValue = UUID.randomUUID().toString();
        // Request lock timeout, nanoseconds
        long timeout = timeOut * 1000000;
        // System current time, nanoseconds
        long nowTime = System.nanoTime();
        while ((System.nanoTime() - nowTime) < timeout) {
            if (this.set(lockKey, lockValue, expireTime)) {
                locked = true;
                // Lock successfully ends the request
                return locked;
            }

            // Wait for some time for each request
            sleep(10, 50000);
        }
        return locked;
    }

    /**
     * Try to get the lock back immediately
     *
     * @return successfully get the lock
     */
    public boolean lock() {
        this.lockValue = UUID.randomUUID().toString();
        // If not present, add and set expiration time (in ms）
        locked = set(lockKey, lockValue, expireTime);
        return locked;
    }

    /**
     * Obtains locks in blocking mode
     *
     * @return successfully get the lock
     */
    public boolean lockBlock() {
        this.lockValue = UUID.randomUUID().toString();
        while (true) {
            // If not present, add and set expiration time (in ms）
            locked = set(lockKey, lockValue, expireTime);
            if (locked) {
                return locked;
            }
            // Wait for some time for each request
            sleep(10, 50000);
        }
    }

    /**
     * Unlock
     * <p>
     * You can make this lock more robust by modifying the following：
     * <p>
     * Instead of using a fixed string as the value of the key, set a long random string that cannot be guessed as a token.
     * Instead of using the DEL command to release the lock, send a Lua script that deletes the key only if the value passed in by the client matches the password string of the key.
     * These two changes prevent clients with expired locks from accidentally deleting existing locks.
     *
     * @return Boolean
     */
    public Boolean unlock() {
        // Only if the lock is successful and the lock is still valid to release the lock
        if (locked) {
            try {
                RedisScript<Long> script = RedisScript.of(UNLOCK_LUA, Long.class);
                List<String> keys = new ArrayList<>();
                keys.add(lockKey);
                Long result = redisTemplate.execute(script, keys, lockValue);
                if (result == 0 && !StringUtils.isEmpty(lockKeyLog)) {
                    log.debug("Redis distributed lock, failed to unlock {}!Unlock time: {}", lockKeyLog, System.currentTimeMillis());
                }

                locked = result == 0;
                return result == 1;
            } catch (Throwable e) {
                log.warn("Redis does not support the EVAL command, unlocks with demotion: {}", e.getMessage());
                String value = this.get(lockKey, String.class);
                if (lockValue.equals(value)) {
                    redisTemplate.delete(lockKey);
                    return true;
                }
                return false;
            }
        }

        return true;
    }

    /**
     * Override the set method of redisTemplate
     * <p>
     * The command set resource - name ANYSTRING NX ex max-lock - time is a simple way to implement locks in Redis.
     * <p>
     * The client executes the above command：
     * <p>
     * If the server returns OK, then this client gets the lock.
     * If the server returns NIL, then the client fails to acquire the lock and can try again later.
     *
     * @param key     lock Key
     * @param value   the value inside the lock
     * @param seconds past time (seconds）
     * @return String
     */
    private boolean set(final String key, final String value, final long seconds) {
        Assert.isTrue(!StringUtils.isEmpty(key), " key cannot be empty");
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
        if (!StringUtils.isEmpty(lockKeyLog) && Objects.nonNull(success) && success) {
            log.debug("Time to get lock：{}", lockKeyLog, System.currentTimeMillis());
        }
        return Objects.nonNull(success) && success;
    }

    /**
     * Get the value inside redis
     *
     * @param key    key
     * @param aClass class
     * @return T
     */
    private <T> T get(final String key, Class<T> aClass) {
        Assert.isTrue(!StringUtils.isEmpty(key), "key cannot be empty");
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * Get lock status
     *
     * @return boolean
     * @author yuhao.wang
     */
    public boolean isLock() {

        return locked;
    }

    /**
     * @ param millis
     * @ param nanos
     * @Title: seleep
     * @Description: thread wait time
     * @author yuhao.wang
     */
    private void sleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, random.nextInt(nanos));
        } catch (Exception e) {
            log.debug("get distributed lock hibernation interrupted:", e);
        }
    }

    public String getLockKeyLog() {
        return lockKeyLog;
    }

    public void setLockKeyLog(String lockKeyLog) {
        this.lockKeyLog = lockKeyLog;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
