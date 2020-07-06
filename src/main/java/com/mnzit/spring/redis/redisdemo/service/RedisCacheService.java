package com.mnzit.spring.redis.redisdemo.service;

import java.util.concurrent.TimeUnit;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public interface RedisCacheService {

    void set(String cacheKey, Object cacheValue, long ttl, TimeUnit timeUnit);

    void set(String cacheKey, Object cacheValue);

    <T> T get(String key, Class<T> type);

    void hSet(String cacheName, Object cacheKey, Object cacheValue, long ttl, TimeUnit timeUnit);

    void hSet(String cacheName, Object cacheKey, Object cacheValue);

    <T> T hGet(String cacheName, Object cacheKey, Class<T> type);

    void expire(String key, long ttl, TimeUnit timeUnit);

    Boolean hasKey(String key);

    Boolean clearAll();

    Boolean clear(String[] cacheNames);

    Boolean clear(String[] cacheNames, Object cacheKey);
}
