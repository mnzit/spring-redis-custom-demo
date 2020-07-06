package com.mnzit.spring.redis.redisdemo.parser;

import com.mnzit.spring.redis.redisdemo.parser.expression.CacheExpressionRootObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public class RedisCacheExpressionParser {

    private final static ExpressionParser expressionParser = new SpelExpressionParser();

    public static StandardEvaluationContext getContextContainingArguments(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        CacheExpressionRootObject cacheExpressionRootObject = new CacheExpressionRootObject(method, args, joinPoint.getTarget(), joinPoint.getTarget().getClass());
        context.setRootObject(cacheExpressionRootObject);
        return context;
    }

    public static String getCacheKeyFromAnnotationKeyValue(StandardEvaluationContext context, String key) {
        Expression expression = expressionParser.parseExpression(key);
        return expression.getValue(context, String.class);
    }

    public static Boolean parseCondition(StandardEvaluationContext context, String condition) {
        Expression expression = expressionParser.parseExpression(condition);
        return expression.getValue(context, Boolean.class);
    }
}
