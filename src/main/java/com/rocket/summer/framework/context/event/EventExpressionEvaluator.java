package com.rocket.summer.framework.context.event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.context.event.ApplicationEvent;
import com.rocket.summer.framework.context.expression.AnnotatedElementKey;
import com.rocket.summer.framework.context.expression.BeanFactoryResolver;
import com.rocket.summer.framework.context.expression.CachedExpressionEvaluator;
import com.rocket.summer.framework.context.expression.MethodBasedEvaluationContext;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.Expression;

/**
 * Utility class handling the SpEL expression parsing. Meant to be used
 * as a reusable, thread-safe component.
 *
 * @author Stephane Nicoll
 * @since 4.2
 * @see CachedExpressionEvaluator
 */
class EventExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<ExpressionKey, Expression>(64);

    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<AnnotatedElementKey, Method>(64);


    /**
     * Create the suitable {@link EvaluationContext} for the specified event handling
     * on the specified method.
     */
    public EvaluationContext createEvaluationContext(ApplicationEvent event, Class<?> targetClass,
                                                     Method method, Object[] args, BeanFactory beanFactory) {

        Method targetMethod = getTargetMethod(targetClass, method);
        EventExpressionRootObject root = new EventExpressionRootObject(event, args);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(
                root, targetMethod, args, getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    /**
     * Specify if the condition defined by the specified expression matches.
     */
    public boolean condition(String conditionExpression,
                             AnnotatedElementKey elementKey, EvaluationContext evalContext) {

        return getExpression(this.conditionCache, elementKey, conditionExpression).getValue(
                evalContext, boolean.class);
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        Method targetMethod = this.targetMethodCache.get(methodKey);
        if (targetMethod == null) {
            targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            this.targetMethodCache.put(methodKey, targetMethod);
        }
        return targetMethod;
    }

}
