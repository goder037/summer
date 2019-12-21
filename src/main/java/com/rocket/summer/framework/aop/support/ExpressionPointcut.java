package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.Pointcut;

/**
 * Interface to be implemented by pointcuts that use String expressions.
 *
 * @author Rob Harrop
 * @since 2.0
 */
public interface ExpressionPointcut extends Pointcut {

    /**
     * Return the String expression for this pointcut.
     */
    String getExpression();

}

