package com.rocket.summer.framework.context.expression;

import java.util.Map;

import com.rocket.summer.framework.core.DefaultParameterNameDiscoverer;
import com.rocket.summer.framework.core.ParameterNameDiscoverer;
import com.rocket.summer.framework.expression.Expression;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Shared utility class used to evaluate and cache SpEL expressions that
 * are defined on {@link java.lang.reflect.AnnotatedElement}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 * @see AnnotatedElementKey
 */
public abstract class CachedExpressionEvaluator {

    private final SpelExpressionParser parser;

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();


    /**
     * Create a new instance with the specified {@link SpelExpressionParser}.
     */
    protected CachedExpressionEvaluator(SpelExpressionParser parser) {
        Assert.notNull(parser, "SpelExpressionParser must not be null");
        this.parser = parser;
    }

    /**
     * Create a new instance with a default {@link SpelExpressionParser}.
     */
    protected CachedExpressionEvaluator() {
        this(new SpelExpressionParser());
    }


    /**
     * Return the {@link SpelExpressionParser} to use.
     */
    protected SpelExpressionParser getParser() {
        return this.parser;
    }

    /**
     * Return a shared parameter name discoverer which caches data internally.
     * @since 4.3
     */
    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }


    /**
     * Return the {@link Expression} for the specified SpEL value
     * <p>Parse the expression if it hasn't been already.
     * @param cache the cache to use
     * @param elementKey the element on which the expression is defined
     * @param expression the expression to parse
     */
    protected Expression getExpression(Map<ExpressionKey, Expression> cache,
                                       AnnotatedElementKey elementKey, String expression) {

        ExpressionKey expressionKey = createKey(elementKey, expression);
        Expression expr = cache.get(expressionKey);
        if (expr == null) {
            expr = getParser().parseExpression(expression);
            cache.put(expressionKey, expr);
        }
        return expr;
    }

    private ExpressionKey createKey(AnnotatedElementKey elementKey, String expression) {
        return new ExpressionKey(elementKey, expression);
    }


    protected static class ExpressionKey implements Comparable<ExpressionKey> {

        private final AnnotatedElementKey element;

        private final String expression;

        protected ExpressionKey(AnnotatedElementKey element, String expression) {
            this.element = element;
            this.expression = expression;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ExpressionKey)) {
                return false;
            }
            ExpressionKey otherKey = (ExpressionKey) other;
            return (this.element.equals(otherKey.element) &&
                    ObjectUtils.nullSafeEquals(this.expression, otherKey.expression));
        }

        @Override
        public int hashCode() {
            return this.element.hashCode() + (this.expression != null ? this.expression.hashCode() * 29 : 0);
        }

        @Override
        public String toString() {
            return this.element + (this.expression != null ? " with expression \"" + this.expression : "\"");
        }

        @Override
        public int compareTo(ExpressionKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0 && this.expression != null) {
                result = this.expression.compareTo(other.expression);
            }
            return result;
        }
    }

}