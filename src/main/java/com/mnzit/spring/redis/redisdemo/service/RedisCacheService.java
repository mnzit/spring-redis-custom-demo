package com.mnzit.spring.redis.redisdemo.service;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public interface RedisCacheService {

    Object get(String cacheName, Object cacheKey);

    Object set(String[] cacheNames, Object cacheKey, Object cacheValue);

    Object clear(String[] cacheNames, Object cacheKey);

    Object clear(String[] cacheNames);

    Boolean clearAll();
}
