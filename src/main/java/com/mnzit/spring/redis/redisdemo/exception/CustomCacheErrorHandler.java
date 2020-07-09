package com.mnzit.spring.redis.redisdemo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
public class CustomCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception,
                                    Cache cache, Object key) {
        log.error("Error in REDIS GET operation for KEY: " + key, exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache,
                                    Object key, Object value) {
        log.error("Error in REDIS PUT operation for KEY: " + key, exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache,
                                      Object key) {
        log.error("Error in REDIS EVICT operation for KEY: " + key, exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Error in REDIS CLEAR operation ", exception);
    }

}
