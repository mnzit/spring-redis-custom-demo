package com.mnzit.spring.redis.redisdemo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    private Boolean enabled;

    private String host;

    private int port;

    private String password;

    private int database;

    private int connectionTimeout;

    private int readTimeout;

    private RedisPoolConfigProperties pool;

    @Data
    public static class RedisPoolConfigProperties {

        private int maxTotal;

        private int maxIdle;

        private int minIdle;

        private int maxWaitMillis;

        private Boolean testOnBorrow;

        private Boolean testOnReturn;

        private Boolean testWhileIdle;

        private Boolean blockedWhenExhausted;

        private int numTestsPerEvictionRun;

        private int timeBetweenEvictionRunsMillis;

        private int minEvictableIdleTimeMillis;
    }
}