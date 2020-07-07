package com.mnzit.spring.redis.redisdemo.aspect.cacheType;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
@Component
@Qualifier("hashMapCache")
public class HashMapCache extends RedisCache {

    @Override
    public Object process() throws Throwable {
        Object object = null;

        try {
            object = redisCacheService.hGet(cacheable.identifier(), key, method.getReturnType());
        } catch (Exception e) {
            log.error("[cachebreak] Error while retreiving key {}:{} {} error : {}", cacheable.identifier(), key, methodName, e.getMessage());

            object = joinPoint.proceed();
            log.debug("%%%%%%% Fetched from database : {}", object);
            return object;
        }

        if (object == null) {
            // execute method to get the return object
            object = joinPoint.proceed();

            // cache the object if database return object
            if (object != null) {
                log.debug("[cachestore] Caching [Hashname: {}, key : {}, value: {}]", cacheable.identifier(), key, object);

                log.debug("%%%%%%% Fetched from database : {}", object);

                try {
                    redisCacheService.hSet(cacheable.identifier(), key, object);
                    if (!cacheable.eternal()) {
                        redisCacheService.expire(cacheable.identifier(), cacheable.ttl(), cacheable.timeUnit());
                    }
                } catch (Exception e) {
                    log.error("Exception while setting {} {}", key, methodName);
                }

            }

        } else {
            log.debug("%%%%%%% Data obtained from redis cache {} {} {}", cacheable.identifier(), key, methodName);
        }

        return object;
    }
}
