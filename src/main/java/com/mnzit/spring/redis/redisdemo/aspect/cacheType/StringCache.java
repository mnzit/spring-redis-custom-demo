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
@Qualifier("stringCache")
public class StringCache extends RedisCache{

    @Override
    public Object process() throws Throwable {
        Object object = null;

        try {
            object = redisCacheService.get(key, method.getReturnType());
        } catch (Exception e) {
            log.error("[cachebreak] Error while retreiving key : {} {} error : {}", key, methodName, e.getMessage());

            object = joinPoint.proceed();
            log.debug("%%%%%%% Fetched from database : {}", object);
            return object;
        }

        if (object == null) {
            // execute method to get the return object
            object = joinPoint.proceed();
            log.debug("%%%%%%% Fetched from database : {}", object);

            // cache the object if database return object
            if (object != null) {
                log.debug("[cachestore] Caching [String : {}, key : {}, value: {}]", key, object);

                try {
                    if (cacheable.eternal()) {
                        redisCacheService.set(key, object);
                    } else {
                        redisCacheService.set(key, object, cacheable.ttl(), cacheable.timeUnit());
                    }
                } catch (Exception e) {
                    log.error("Exception while setting {} {}", key, methodName);
                }

            }
        } else {
            log.debug("%%%%%%% Data obtained from redis cache {} {}", key, methodName);
        }
        return object;
    }
}
