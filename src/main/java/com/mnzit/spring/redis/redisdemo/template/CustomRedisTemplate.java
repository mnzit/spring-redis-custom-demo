package com.mnzit.spring.redis.redisdemo.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
public class CustomRedisTemplate<K, V> extends RedisTemplate<K, V> {

    @Override
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
        log.info("Äction : {}", action);
        try {
            return super.execute(action, exposeConnection, pipeline);
        } catch (Throwable redis) {
            log.error("Exception : {}", redis.getMessage());
            return null;
        }
    }
}