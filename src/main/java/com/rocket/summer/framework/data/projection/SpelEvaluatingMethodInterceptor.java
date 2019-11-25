package com.rocket.summer.framework.data.projection;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.annotation.Value;
import com.rocket.summer.framework.context.expression.BeanFactoryResolver;
import com.rocket.summer.framework.context.expression.MapAccessor;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.Expression;
import com.rocket.summer.framework.expression.ParserContext;
import com.rocket.summer.framework.expression.common.TemplateParserContext;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;
import com.rocket.summer.framework.expression.spel.support.StandardEvaluationContext;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link MethodInterceptor} to invoke a SpEL expression to compute the method result. Will forward the resolution to a
 * delegate {@link MethodInterceptor} if no {@link Value} annotation is found.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @see 1.10
 */
class SpelEvaluatingMethodInterceptor implements MethodInterceptor {

    private static final ParserContext PARSER_CONTEXT = new TemplateParserContext();

    private final EvaluationContext evaluationContext;
    private final MethodInterceptor delegate;
    private final Map<Integer, Expression> expressions;

    /**
     * Creates a new {@link SpelEvaluatingMethodInterceptor} delegating to the given {@link MethodInterceptor} as fallback
     * and exposing the given target object via {@code target} to the SpEl expressions. If a {@link BeanFactory} is given,
     * bean references in SpEl expressions can be resolved as well.
     *
     * @param delegate must not be {@literal null}.
     * @param target must not be {@literal null}.
     * @param beanFactory can be {@literal null}.
     * @param parser must not be {@literal null}.
     * @param targetInterface must not be {@literal null}.
     */
    public SpelEvaluatingMethodInterceptor(MethodInterceptor delegate, Object target, BeanFactory beanFactory,
                                           SpelExpressionParser parser, Class<?> targetInterface) {

        Assert.notNull(delegate, "Delegate MethodInterceptor must not be null!");
        Assert.notNull(target, "Target object must not be null!");
        Assert.notNull(parser, "SpelExpressionParser must not be null!");
        Assert.notNull(targetInterface, "Target interface must not be null!");

        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(new TargetWrapper(target));

        if (target instanceof Map) {
            evaluationContext.addPropertyAccessor(new MapAccessor());
        }

        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }

        this.expressions = potentiallyCreateExpressionsForMethodsOnTargetInterface(parser, targetInterface);

        this.evaluationContext = evaluationContext;
        this.delegate = delegate;
    }

    /**
     * Eagerly parses {@link Expression} defined on {@link Value} annotations. Returns a map with
     * {@code method.hashCode()} as key and the parsed {@link Expression} or an {@link Collections#emptyMap()} if no
     * {@code Expressions} were found.
     *
     * @param parser must not be {@literal null}.
     * @param targetInterface must not be {@literal null}.
     * @return
     */
    private static Map<Integer, Expression> potentiallyCreateExpressionsForMethodsOnTargetInterface(
            SpelExpressionParser parser, Class<?> targetInterface) {

        Map<Integer, Expression> expressions = new HashMap<Integer, Expression>();

        for (Method method : targetInterface.getMethods()) {

            if (!method.isAnnotationPresent(Value.class)) {
                continue;
            }

            Value value = method.getAnnotation(Value.class);

            if (!StringUtils.hasText(value.value())) {
                throw new IllegalStateException(String.format("@Value annotation on %s contains empty expression!", method));
            }

            expressions.put(method.hashCode(), parser.parseExpression(value.value(), PARSER_CONTEXT));
        }

        return Collections.unmodifiableMap(expressions);
    }

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Expression expression = expressions.get(invocation.getMethod().hashCode());

        if (expression == null) {
            return delegate.invoke(invocation);
        }

        return expression.getValue(evaluationContext);
    }

    /**
     * Wrapper class to expose an object to the SpEL expression as {@code target}.
     *
     * @author Oliver Gierke
     */
    static class TargetWrapper {

        private final Object target;

        public TargetWrapper(Object target) {
            this.target = target;
        }

        /**
         * @return the target
         */
        public Object getTarget() {
            return target;
        }
    }
}
