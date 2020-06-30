package com.mnzit.spring.redis.redisdemo.aspect;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.enums.Type;
import com.mnzit.spring.redis.redisdemo.parser.RedisCacheExpressionParser;
import com.mnzit.spring.redis.redisdemo.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.redis.cache.enabled:true}")
    private boolean cacheEnabled;

    @Autowired
    private RedisCacheService redisCacheService;

    @Pointcut("@annotation(cacheable)")
    public void cacheablePointCut(Cacheable cacheable) {
    }

    @Around("cacheablePointCut(cacheable)")
    public Object cacheable(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        Object returnObject = null;
        if (cacheEnabled) {

            Method method = getCurrentMethod(joinPoint);

            Type type = cacheable.type();

            StandardEvaluationContext standardEvaluationContext = RedisCacheExpressionParser.getContextContainingArguments(joinPoint);

            Boolean condition = true;

            if (!cacheable.condition().equals("")) {
                condition = RedisCacheExpressionParser.parseCondition(standardEvaluationContext, cacheable.condition());

                if (!condition) {
                    returnObject = joinPoint.proceed();
                }
            }

            if (condition) {
                String key = RedisCacheExpressionParser.getCacheKeyFromAnnotationKeyValue(standardEvaluationContext, cacheable.cacheName());

                // FOR NORMAL STRING
                if (type.name().equals(Type.STRING.name())) {
                    returnObject = redisCacheService.get(key, method.getReturnType());

                    if (returnObject != null) {
                        log.debug("### Cache: {}", returnObject);
                        return returnObject;
                    }

                    // execute method to get the return object
                    returnObject = joinPoint.proceed();

                    if (cacheable.eternal()) {
                        log.debug("### Caching :- Key: {}, Object:{}", key, returnObject);
                        redisCacheService.set(key, returnObject);
                    } else {
                        log.debug("### Caching :- Key: {}, Object:{}, TTL: {}", key, returnObject, cacheable.ttl());
                        redisCacheService.set(key, returnObject, cacheable.ttl(), cacheable.timeUnit());
                    }
                    // FOR HASH
                } else if (type.name().equals(Type.HASHMAP.name())) {
                    returnObject = redisCacheService.hGet(cacheable.hashName(), key);

                    if (returnObject != null) {
                        log.debug("### Cache: {}", returnObject);
                        return returnObject;
                    }

                    // execute method to get the return object
                    returnObject = joinPoint.proceed();

                    if (cacheable.eternal()) {
                        log.debug("### Caching :- Hashname: {}, Key: {}, Object:{}", cacheable.hashName(), key, returnObject);
                        redisCacheService.hSet(cacheable.hashName(), key, returnObject);
                    } else {
                        log.debug("### Caching :- Hashname: {}, Key: {}, Object:{}, TTL: {}", cacheable.hashName(), key, returnObject, cacheable.ttl());
                        redisCacheService.hSet(cacheable.hashName(), key, returnObject, cacheable.ttl(), cacheable.timeUnit());
                    }
                }
            } else {
                returnObject = joinPoint.proceed();
            }
        } else {
            returnObject = joinPoint.proceed();
        }

        return returnObject;
    }

    private Method getCurrentMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
