package com.mnzit.spring.redis.redisdemo.aspect;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.aspect.cacheType.RedisCache;
import com.mnzit.spring.redis.redisdemo.enums.Type;
import com.mnzit.spring.redis.redisdemo.factory.RedisCacheTypeFactory;
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
    private RedisCacheTypeFactory redisCacheTypeFactory;

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

                RedisCache redisCache = redisCacheTypeFactory.getCacheType(type);

                redisCache.initialize(joinPoint, cacheable, key, method, methodName);

                object = redisCache.process();

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
}
