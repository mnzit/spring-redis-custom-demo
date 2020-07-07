package com.mnzit.spring.redis.redisdemo.aspect.cacheType;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.service.RedisCacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public abstract class RedisCache {
    @Autowired
    protected RedisCacheService redisCacheService;

    protected ProceedingJoinPoint joinPoint;
    protected Cacheable cacheable;
    protected String key;
    protected Method method;
    protected String methodName;

    public void initialize(ProceedingJoinPoint proceedingJoinPoint, Cacheable cacheable, String key, Method method, String methodName) {
        this.joinPoint = proceedingJoinPoint;
        this.cacheable = cacheable;
        this.key = key;
        this.method = method;
        this.methodName = methodName;
    }

    abstract public Object process() throws Throwable;
}
