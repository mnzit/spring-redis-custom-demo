package com.mnzit.spring.redis.redisdemo.config;

import com.mnzit.spring.redis.redisdemo.exception.IgnoreExceptionCacheErrorHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public CacheManager redisCacheManager() {

        RedisCacheConfiguration cacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(2000))
                        .disableCachingNullValues()
                        .serializeValuesWith(valuePair());

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    /**
     * Using GenericJackson2JsonRedisSerializer
     *
     * @return
     */
    private RedisSerializer genericJackson2JsonSerializer() {
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        return genericJackson2JsonRedisSerializer;
    }

    /**
     * Using GenericJackson2JsonRedisSerializer
     *
     * @return
     */
    private RedisSerializer jackson2JsonSerializer() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        return jackson2JsonRedisSerializer;
    }

    /**
     * Using JdkSerializationRedisSerializer
     *
     * @return
     */
    private RedisSerializer jdkSerializer() {
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        return jdkSerializationRedisSerializer;
    }

    /**
     * key Serialization method
     *
     * @return
     */
    private RedisSerializationContext.SerializationPair<String> keyPair() {
        RedisSerializationContext.SerializationPair<String> keyPair =
                RedisSerializationContext.SerializationPair.fromSerializer(keySerializer());
        return keyPair;
    }

    private RedisSerializationContext.SerializationPair<Object> valuePair() {
        RedisSerializationContext.SerializationPair<Object> valuePair =
                RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonSerializer());
        return valuePair;
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    /**
     * Add custom cache exception handling
     * Ignore exception when cache read / write exception
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new IgnoreExceptionCacheErrorHandler();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setExposeConnection(true);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        template.setKeySerializer(keySerializer());
        template.setHashKeySerializer(keySerializer());

        template.setValueSerializer(jdkSerializer());
        template.setHashValueSerializer(jdkSerializer());

        template.setStringSerializer(stringRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (object, method, objects) -> {

            // This will generate a unique key of the class name, the method name,
            // and all method parameters appended.

            StringBuilder sb = new StringBuilder();
            sb.append(object.getClass().getName());
            sb.append(method.getName());
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Getter
    private enum MyCaches {
        defaultCache(Duration.ofDays(1)),
        MyCaches(Duration.ofMinutes(10));

        MyCaches(Duration ttl) {
            this.ttl = ttl;
        }

        /**
         * Failure time
         */
        private Duration ttl = Duration.ofHours(1);
    }

}
