package com.mnzit.spring.redis.redisdemo.aspect;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.enums.Type;
import com.mnzit.spring.redis.redisdemo.parser.RedisCacheExpressionParser;
import com.mnzit.spring.redis.redisdemo.properties.RedisProperties;
import com.mnzit.spring.redis.redisdemo.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
@Aspect
@Component
public class RedisCacheProcessorAspect {

    @Autowired
    private RedisProperties redisProperties;
    @Autowired
    private RedisCacheService redisCacheService;

    @Pointcut("@annotation(cacheable)")
    public void cacheablePointCut(Cacheable cacheable) {
    }

    @Around("cacheablePointCut(cacheable)")
    public Object cacheableAdvice(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        log.debug("Cacheable Advice called...");

        Object object = null;

        if (redisProperties.getEnabled()) {
            Method method = getCurrentMethod(joinPoint);
            String methodName = method.getName();
            Type type = cacheable.type();

            StandardEvaluationContext standardEvaluationContext = RedisCacheExpressionParser.getContextContainingArguments(joinPoint);

            Boolean condition = true;

            if (!cacheable.condition().equals("")) {
                condition = RedisCacheExpressionParser.parseCondition(standardEvaluationContext, cacheable.condition());

                if (!condition) {
                    object = joinPoint.proceed();
                }
            }

            /**
             * if Condition is true then cache else don't cache
             */
            if (condition) {
                String key = RedisCacheExpressionParser.getCacheKeyFromAnnotationKeyValue(standardEvaluationContext, cacheable.cacheName());

                if (type.name().equals(Type.STRING.name())) {

                    object = string(cacheable, joinPoint, key, method, methodName);

                } else if (type.name().equals(Type.HASHMAP.name())) {

                    object = hashes(cacheable, joinPoint, key, method, methodName);

                } else {
                    throw new IllegalArgumentException("Type not supported");
                }

            } else {
                // execute method to get the return object
                object = joinPoint.proceed();

                log.debug("%%%%%%% Fetched from database : {}", object);
            }
        } else {
            // execute method to get the return object
            object = joinPoint.proceed();

            log.debug("%%%%%%% Fetched from database : {}", object);
        }

        return object;
    }

    private Method getCurrentMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private Object hashes(Cacheable cacheable, ProceedingJoinPoint joinPoint, String key, Method method, String methodName) throws Throwable {
        Object object = null;

        try {
            object = redisCacheService.hGet(cacheable.identifier(), key, method.getReturnType());
        } catch (Exception e) {
            log.error("[cachebreak] Error while retreiving key {}:{} {} error : {}", cacheable.identifier(), key, methodName, e.getMessage());

            object = joinPoint.proceed();
            log.debug("%%%%%%% Fetched from database : {}", object);
            return object;
        }

        if (object == null) {
            // execute method to get the return object
            object = joinPoint.proceed();

            if (object != null) {
                log.debug("[cachestore] Caching [Hashname: {}, key : {}, value: {}]", cacheable.identifier(), key, object);
            }

            log.debug("%%%%%%% Fetched from database : {}", object);

            try {
                redisCacheService.hSet(cacheable.identifier(), key, object);

                if (!cacheable.eternal()) {

                    redisCacheService.expire(cacheable.identifier(), cacheable.ttl(), cacheable.timeUnit());

                }

            } catch (Exception e) {
                log.error("Exception while setting {} {}", key, methodName);
            }

        } else {
            log.debug("%%%%%%% Data obtained from redis cache {} {} {}", cacheable.identifier(), key, methodName);
        }

        return object;
    }

    /**
     * For storing Objects in String KeyPair Format
     *
     * @param cacheable
     * @param joinPoint
     * @param key
     * @param method
     * @return
     * @throws Throwable
     */
    private Object string(Cacheable cacheable, ProceedingJoinPoint joinPoint, String key, Method method, String methodName) throws Throwable {
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
