package com.rocket.summer.framework.data.mapping.model;

/**
 * SPI for components that can evaluate Spring EL expressions.
 *
 * @author Oliver Gierke
 */
public interface SpELExpressionEvaluator {

    /**
     * Evaluates the given expression.
     *
     * @param expression
     * @return
     */
    <T> T evaluate(String expression);
}

