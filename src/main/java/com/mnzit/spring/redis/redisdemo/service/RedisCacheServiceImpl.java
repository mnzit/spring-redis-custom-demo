package com.mnzit.spring.redis.redisdemo.service;

import com.mnzit.spring.redis.redisdemo.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
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
    public Object get(String cacheName, Object cacheKey) {
        if (StringUtils.isNullOrEmpty(cacheName) || cacheKey == null) {
            throw new IllegalArgumentException("Cache name or cache key can not be null!");
        }
        return hashOperations.get(cacheName, cacheKey);
    }

    @Override
    public Object set(String[] cacheNames, Object cacheKey, Object cacheValue) {
        if (cacheNames == null || cacheNames.length == 0) {
            throw new IllegalArgumentException(
                    "Cache names list can not be null or empty for save operation!!");
        }

        for (String cacheName : cacheNames) {
            hashOperations.put(cacheName, cacheKey, cacheValue);
        }
        return true;
    }

    @Override
    public Object clear(String[] cacheNames, Object cacheKey) {

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

    @Override
    public Object clear(String[] cacheNames) {
        for (String cacheName : cacheNames) {
            redisTemplate.delete(cacheName);
        }
        return true;
    }

    @Override
    public Boolean clearAll() {
        redisTemplate.getRequiredConnectionFactory().getConnection().serverCommands().flushAll();
        return true;
    }
}
