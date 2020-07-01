package com.mnzit.spring.redis.redisdemo.config;

import com.mnzit.spring.redis.redisdemo.properties.RedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@Configuration
public class RedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    private JedisPoolConfig jedisPoolConfig() {
        RedisProperties.RedisPoolConfigProperties poolConfig = redisProperties.getPool();

        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        /**
         * The maximum number of connections that can be allocated from this pool.
         */
        jedisPoolConfig.setMaxTotal(poolConfig.getMaxTotal());

        /**
         *  The maximum number of connections that can remain idle in the pool, without extra ones being released.
         */
        jedisPoolConfig.setMaxIdle(poolConfig.getMaxIdle());

        /**
         *  The maximum number of milliseconds that the caller needs to wait when no connection is available.
         */
        jedisPoolConfig.setMaxWaitMillis(poolConfig.getMaxWaitMillis());

        /**
         *  The minimum number of connections that can remain idle in the pool, without extra ones being created.
         */
        jedisPoolConfig.setMinIdle(poolConfig.getMinIdle());

        /**
         *  The minimum amount of time an object may sit idle in the pool before it is eligible for eviction by the idle object evictor (if any).
         */
        jedisPoolConfig.setMinEvictableIdleTimeMillis(poolConfig.getMinEvictableIdleTimeMillis());

        /**
         * 	The number of objects to examine during each run of the idle object evictor thread (if any).
         */
        jedisPoolConfig.setNumTestsPerEvictionRun(poolConfig.getNumTestsPerEvictionRun());

        /**
         * Whether the connections will be validated by using the ping command before they are borrowed from the pool.
         * If the connection turns out to be invalid, it will be removed from the pool.
         */
        jedisPoolConfig.setTestOnBorrow(poolConfig.getTestOnBorrow());

        /**
         * Whether connections will be validated using the ping command before they are returned to the pool.
         * If the connection turns out to be invalid, it will be removed from the pool.
         */
        jedisPoolConfig.setTestOnReturn(poolConfig.getTestOnReturn());

        /**
         * 	Whether to enable the idle resource detection.
         */
        jedisPoolConfig.setTestWhileIdle(poolConfig.getTestWhileIdle());

        /**
         * 	The number of milliseconds to sleep between runs of the idle object evictor thread.
         */
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(poolConfig.getTimeBetweenEvictionRunsMillis());

        /**
         *  Whether the caller has to wait when the resource pool is exhausted. The following maxWaitMillis takes effect only when this value is true.
         */
        jedisPoolConfig.setBlockWhenExhausted(poolConfig.getBlockedWhenExhausted());

        return jedisPoolConfig;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfig.setHostName(redisProperties.getHost());
        redisStandaloneConfig.setPort(redisProperties.getPort());
        redisStandaloneConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisConfigurationBuilder = JedisClientConfiguration.builder();

        jedisConfigurationBuilder
                // Configure a connection timeout.
                .connectTimeout(Duration.ofMillis(redisProperties.getConnectionTimeout()))
                // Configure a read timeout.
                .readTimeout(Duration.ofMillis(redisProperties.getReadTimeout()))
                // Enable connection-pooling.
                .usePooling()
                .poolConfig(jedisPoolConfig());

        return new JedisConnectionFactory(redisStandaloneConfig, jedisConfigurationBuilder.build());
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
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
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

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
