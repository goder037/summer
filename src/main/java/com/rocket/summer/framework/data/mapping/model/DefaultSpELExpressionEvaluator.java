package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.data.mapping.PreferredConstructor.Parameter;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.Expression;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;

/**
 * {@link ParameterValueProvider} implementation that evaluates the {@link Parameter}s key against
 * {@link SpelExpressionParser} and {@link EvaluationContext}.
 *
 * @author Oliver Gierke
 */
public class DefaultSpELExpressionEvaluator implements SpELExpressionEvaluator {

    private final Object source;
    private final SpELContext factory;

    /**
     * @param parser
     * @param factory
     */
    public DefaultSpELExpressionEvaluator(Object source, SpELContext factory) {
        this.source = source;
        this.factory = factory;
    }

    /* (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.SpELExpressionEvaluator#evaluate(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public <T> T evaluate(String expression) {

        Expression parseExpression = factory.getParser().parseExpression(expression);
        return (T) parseExpression.getValue(factory.getEvaluationContext(source));
    }
}

