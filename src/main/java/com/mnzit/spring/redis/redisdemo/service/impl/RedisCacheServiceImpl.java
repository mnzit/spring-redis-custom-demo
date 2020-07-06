package com.mnzit.spring.redis.redisdemo.service.impl;

import com.mnzit.spring.redis.redisdemo.service.RedisCacheService;
import com.mnzit.spring.redis.redisdemo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
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
    public void set(@NotNull String cacheKey, @NotNull Object cacheValue, @NotNull long ttl, @NotNull TimeUnit timeUnit) {
        checkIfEmptyKey(cacheKey);
        redisTemplate.opsForValue().set(cacheKey, cacheValue, ttl, timeUnit);
    }

    @Override
    public void set(@NotNull String cacheKey, @NotNull Object cacheValue) {
        checkIfEmptyKey(cacheKey);
        redisTemplate.opsForValue().set(cacheKey, cacheValue);
    }

    @Override
    public <T> T get(@NotNull String cacheKey, @NotNull Class<T> type) {
        checkIfEmptyKey(cacheKey);
        return (T) redisTemplate.opsForValue().get(cacheKey);
    }

    @Override
    public void hSet(@NotNull String cacheKey, @NotNull Object hashKey, @NotNull Object cacheValue, @NotNull long ttl, @NotNull TimeUnit timeUnit) {
        checkIfEmptyKey(cacheKey);
        hSet(cacheKey, hashKey, cacheValue);

        redisTemplate.expire(cacheKey, ttl, timeUnit);
    }

    @Override
    public void hSet(@NotNull String cacheKey, @NotNull Object hashKey, @NotNull Object cacheValue) {
        checkIfEmptyKey(cacheKey);
        hashOperations.put(cacheKey, hashKey, cacheValue);
    }

    @Override
    public <T> T hGet(@NotNull String cacheKey, @NotNull Object hashKey, @NotNull Class<T> type) {
        checkIfEmptyKey(cacheKey);
        return (T) hashOperations.get(cacheKey, hashKey);
    }

    @Override
    public void expire(@NotNull String cacheKey, @NotNull long ttl, @NotNull TimeUnit timeUnit) {
        checkIfEmptyKey(cacheKey);
        redisTemplate.expire(cacheKey, ttl, timeUnit);
    }

    @Override
    public Boolean hasKey(@NotNull String cacheKey) {
        checkIfEmptyKey(cacheKey);
        return redisTemplate.hasKey(cacheKey);
    }

    @Override
    public Boolean clearAll() {
        redisTemplate.getRequiredConnectionFactory().getConnection().serverCommands().flushAll();
        return true;
    }

    @Override
    public Boolean clear(@NotNull String[] cacheKeys) {
        if (cacheKeys.length == 0) {
            throw new IllegalArgumentException("Cache keys list can not be empty for save operation!!");
        }
        for (String cacheName : cacheKeys) {
            redisTemplate.delete(cacheName);
        }
        return true;
    }

    @Override
    public Boolean clear(@NotNull String[] cacheKeys, @NotNull Object cacheKey) {
        if (cacheKeys.length == 0) {
            throw new IllegalArgumentException("Cache keys list can not be empty for save operation!!");
        }
        for (String cacheName : cacheKeys) {
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

    private void checkIfEmptyKey(String cacheKey) {
        if (cacheKey.isEmpty()) {
            throw new IllegalArgumentException("Cache Key cannot be empty!");
        }
    }

}
