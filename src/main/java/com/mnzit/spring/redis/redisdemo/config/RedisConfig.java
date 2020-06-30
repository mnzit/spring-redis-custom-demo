package com.mnzit.spring.redis.redisdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@EnableCaching
@Configuration
public class RedisConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory);
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

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setExposeConnection(true);

        template.setKeySerializer(keySerializer());
        template.setHashKeySerializer(keySerializer());

        template.setValueSerializer(jdkSerializer());
        template.setHashValueSerializer(jdkSerializer());

        template.setStringSerializer(keySerializer());
        template.afterPropertiesSet();

        return template;
    }
}
