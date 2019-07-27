package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.context.BeansException;

/**
 * Strategy interface for resolving a value through evaluating it
 * as an expression, if applicable.
 *
 * <p>A raw {@link com.rocket.summer.framework.beans.factory.BeanFactory} does not
 * contain a default implementation of this strategy. However,
 * {@link com.rocket.summer.framework.context.ApplicationContext} implementations
 * will provide expression support out of the box.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface BeanExpressionResolver {

    /**
     * Evaluate the given value as an expression, if applicable;
     * return the value as-is otherwise.
     * @param value the value to check
     * @param evalContext the evaluation context
     * @return the resolved value (potentially the given value as-is)
     * @throws BeansException if evaluation failed
     */
    Object evaluate(String value, BeanExpressionContext evalContext) throws BeansException;

}
