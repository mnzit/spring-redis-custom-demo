package com.mnzit.spring.redis.redisdemo.parser;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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
        return context;
    }

    public static String getCacheKeyFromAnnotationKeyValue(StandardEvaluationContext context, String key) {
        Expression expression = expressionParser.parseExpression(key);
        return (String) expression.getValue(context);
    }

    public static Boolean parseCondition(StandardEvaluationContext context, String condition) {
        Expression expression = expressionParser.parseExpression(condition);
        return expression.getValue(context, Boolean.class);
    }
}
