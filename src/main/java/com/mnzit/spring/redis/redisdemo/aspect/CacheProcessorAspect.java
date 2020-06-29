package com.mnzit.spring.redis.redisdemo.aspect;

import com.mnzit.spring.redis.redisdemo.annotation.Cacheable;
import com.mnzit.spring.redis.redisdemo.service.CacheService;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@Slf4j
@Aspect
@Component
public class CacheProcessorAspect {

    @Autowired
    private CacheService cacheService;

    @Pointcut("@annotation(cacheable)")
    public void cacheablePointCut(Cacheable cacheable) {
    }

    //    @Around("@annotation(com.mnzit.spring.redis.redisdemo.annotation.Cacheable)")
    @Around("cacheablePointCut(cacheable)")
    public Object cacheable(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        Method method = getCurrentMethod(joinPoint);

//        Cacheable cacheable = getAnnotation(method, Cacheable.class);

        StandardEvaluationContext standardEvaluationContext = CacheExpressionParser.getContextContainingArguments(joinPoint);

        String key = CacheExpressionParser.getCacheKeyFromAnnotationKeyValue(standardEvaluationContext, cacheable.cacheValue());

        // call cache service to get the value for the given key if not execute  method to get the return object to be cached
        Object returnObject = cacheService.cacheGet(key, method.getReturnType());
        if (returnObject != null) {
            return returnObject;
        }
        // execute method to get the return object
        returnObject = joinPoint.proceed();

        if (cacheable.eternal()) {
            cacheService.cachePut(key, returnObject);
        } else {
            cacheService.cachePut(key, returnObject, cacheable.ttl(), cacheable.timeUnit());
        }

        return returnObject;
    }

    private Method getCurrentMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    private <T extends Annotation> T getAnnotation(JoinPoint proceedingJoinPoint,
                                                   Class<T> annotationClass) throws NoSuchMethodException {

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        if (method.getDeclaringClass().isInterface()) {
            method = proceedingJoinPoint.getTarget().getClass().getDeclaredMethod(methodName,
                    method.getParameterTypes());
        }
        return method.getAnnotation(annotationClass);
    }
}
