package com.mnzit.spring.redis.redisdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
public class CustomRedisTemplate<K, V> extends RedisTemplate<K, V> {

    @Override
    public <T> T execute(RedisCallback<T> action) {
        try {
            return super.execute(action);
        } catch (Throwable redis) {
            log.error(redis.getMessage());
            return null;
        }
    }

    @Override
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
        try {
            return super.execute(action, false);
        } catch (Throwable redis) {
            log.error(redis.getMessage());
            return null;
        }
    }

    @Override
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
        try {
            return super.execute(action, exposeConnection, pipeline);
        } catch (Throwable redis) {
            log.error(redis.getMessage());
            return null;
        }
    }

    @Override
    public <T> T execute(final RedisScript<T> script, final List<K> keys, final Object... args) {
        try {
            return super.execute(script, keys, args);
        } catch (final Throwable t) {
            log.error("Error executing cache operation: {}", t.getMessage());
            return null;
        }
    }

    @Override
    public <T> T execute(final RedisScript<T> script, final RedisSerializer<?> argsSerializer, final RedisSerializer<T> resultSerializer, final List<K> keys, final Object... args) {
        try {
            return super.execute(script, argsSerializer, resultSerializer, keys, args);
        } catch (final Throwable t) {
            log.warn("Error executing cache operation: {}", t.getMessage());
            return null;
        }
    }

    @Override
    public <T> T execute(SessionCallback<T> session) {
        try {
            return super.execute(session);
        } catch (final Throwable redis) {
            log.error(redis.getMessage());
            return null;
        }
    }
}

