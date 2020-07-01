package com.mnzit.spring.redis.redisdemo.service.impl;

import com.mnzit.spring.redis.redisdemo.service.RedisCacheService;
import com.mnzit.spring.redis.redisdemo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Service
public class RedisCacheServiceImpl implements RedisCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, Object, Object> hashOperations;

    @PostConstruct
    public void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void set(String cacheKey, Object cacheValue, long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(cacheKey, cacheValue, ttl, timeUnit);
    }

    @Override
    public void set(String cacheKey, Object cacheValue) {
        if (cacheValue == null) {
            return;
        }
        redisTemplate.opsForValue().set(cacheKey, cacheValue);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void hSet(String cacheName, Object cacheKey, Object cacheValue, long ttl, TimeUnit timeUnit) {
        hSet(cacheName, cacheKey, cacheValue);

        redisTemplate.expire(cacheName, ttl, timeUnit);
    }

    @Override
    public void hSet(String cacheName, Object cacheKey, Object cacheValue) {
        if (StringUtils.isNullOrEmpty(cacheName) || cacheKey == null) {
            throw new IllegalArgumentException("Cache name or cache key can not be null!");
        }
        hashOperations.put(cacheName, cacheKey, cacheValue);
    }

    @Override
    public void hSet(String[] cacheNames, Object cacheKey, Object cacheValue, long ttl, TimeUnit timeUnit) {
        hSet(cacheNames, cacheKey, cacheValue);

        for (String cacheName : cacheNames) {
            redisTemplate.expire(cacheName, ttl, timeUnit);
        }

    }

    @Override
    public void hSet(String[] cacheNames, Object cacheKey, Object cacheValue) {
        if (cacheNames == null || cacheNames.length == 0) {
            throw new IllegalArgumentException("Cache names list can not be null or empty for save operation!!");
        }

        for (String cacheName : cacheNames) {
            hashOperations.put(cacheName, cacheKey, cacheValue);
        }
    }

    @Override
    public <T> T hGet(String cacheName, Object cacheKey, Class<T> type) {
        if (StringUtils.isNullOrEmpty(cacheName) || cacheKey == null) {
            throw new IllegalArgumentException("Cache name or cache key can not be null!");
        }
        return (T) hashOperations.get(cacheName, cacheKey);
    }

    @Override
    public void expire(String cacheName, long ttl, TimeUnit timeUnit) {
        if (StringUtils.isNullOrEmpty(cacheName) ) {
            throw new IllegalArgumentException("Cache Key cannot be empty!");
        }
        redisTemplate.expire(cacheName, ttl, timeUnit);
    }

    @Override
    public Boolean hasKey(String key) {
        if (StringUtils.isNullOrEmpty(key) ) {
            throw new IllegalArgumentException("Cache Key cannot be empty!");
        }
        return redisTemplate.hasKey(key);
    }

    @Override
    public Boolean clearAll() {
        redisTemplate.getRequiredConnectionFactory().getConnection().serverCommands().flushAll();
        return true;
    }

    @Override
    public Boolean clear(String[] cacheNames) {
        for (String cacheName : cacheNames) {
            redisTemplate.delete(cacheName);
        }
        return true;
    }

    @Override
    public Boolean clear(String[] cacheNames, Object cacheKey) {
        if (cacheNames == null || cacheNames.length == 0) {
            throw new IllegalArgumentException(
                    "Cache names list can not be null or empty for save operation!!");
        }
        for (String cacheName : cacheNames) {
            if (StringUtils.isNullOrEmpty(cacheName)) {
                continue;
            }
            if (!redisTemplate.hasKey(cacheName)) {
                continue;
            }
            hashOperations.delete(cacheName, cacheKey);

        }
        return true;
    }

}
