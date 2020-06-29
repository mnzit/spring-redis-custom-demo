package com.mnzit.spring.redis.redisdemo.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Component
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    /**
     * The name of this Cache instance, optional.
     * If you do not specify, It will auto generate one.
     * The name is used to display statistics information and as part of key prefix when using a remote cache.
     *
     * @return the name of the cache
     */
    String cacheName() default "";

    /**
     * Specify the key by expression script, optional. If not specified,
     * use all parameters of the target method and keyConvertor to generate one.
     *
     * @return an expression script which specifies key
     */
    String cacheValue() default "";

    /**
     * Expression script used for conditioning the method caching, the cache is not
     * used when evaluation result is false.
     * Evaluation occurs before real method invocation.
     */
    String condition() default "";

    /**
     * set eternal to make the cache permanent
     */
    boolean eternal() default false;

    /**
     * Specify the time unit of expire.
     *
     * @return the time unit of expire time
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * The expire time.
     *
     * @return the expire time
     */
    int ttl() default 1;
}
