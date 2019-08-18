package com.rocket.summer.framework.aop.support;

import java.io.Serializable;

/**
 * Abstract superclass for expression pointcuts,
 * offering location and expression properties.
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @since 2.0
 * @see #setLocation
 * @see #setExpression
 */
@SuppressWarnings("serial")
public abstract class AbstractExpressionPointcut implements ExpressionPointcut, Serializable {

    private String location;

    private String expression;


    /**
     * Set the location for debugging.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Return location information about the pointcut expression
     * if available. This is useful in debugging.
     * @return location information as a human-readable String,
     * or {@code null} if none is available
     */
    public String getLocation() {
        return this.location;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        try {
            onSetExpression(expression);
        }
        catch (IllegalArgumentException ex) {
            // Fill in location information if possible.
            if (this.location != null) {
                throw new IllegalArgumentException("Invalid expression at location [" + this.location + "]: " + ex);
            }
            else {
                throw ex;
            }
        }
    }

    /**
     * Called when a new pointcut expression is set.
     * The expression should be parsed at this point if possible.
     * <p>This implementation is empty.
     * @param expression expression to set
     * @throws IllegalArgumentException if the expression is invalid
     * @see #setExpression
     */
    protected void onSetExpression(String expression) throws IllegalArgumentException {
    }

    /**
     * Return this pointcut's expression.
     */
    @Override
    public String getExpression() {
        return this.expression;
    }

}

