package com.mnzit.spring.redis.redisdemo.annotation;

import com.mnzit.spring.redis.redisdemo.enums.Type;
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

    String cacheName() default "";

    String hashName() default "";

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
    long ttl() default 1;

    /**
     * The type of data stored.
     *
     * @return type of data
     */
    Type type() default Type.STRING;
}
