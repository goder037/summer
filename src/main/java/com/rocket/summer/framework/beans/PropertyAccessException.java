package com.rocket.summer.framework.beans;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.ErrorCoded;

import java.beans.PropertyChangeEvent;


/**
 * Superclass for exceptions related to a property access,
 * such as type mismatch or invocation target exception.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class PropertyAccessException extends BeansException implements ErrorCoded {

    private transient PropertyChangeEvent propertyChangeEvent;


    /**
     * Create a new PropertyAccessException.
     *
     * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
     * @param msg                 the detail message
     * @param cause               the root cause
     */
    public PropertyAccessException(PropertyChangeEvent propertyChangeEvent, String msg, Throwable cause) {
        super(msg, cause);
        this.propertyChangeEvent = propertyChangeEvent;
    }

    /**
     * Create a new PropertyAccessException without PropertyChangeEvent.
     *
     * @param msg   the detail message
     * @param cause the root cause
     */
    public PropertyAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }


    /**
     * Return the PropertyChangeEvent that resulted in the problem.
     * <p>May be <code>null</code>; only available if an actual bean property
     * was affected.
     */
    public PropertyChangeEvent getPropertyChangeEvent() {
        return this.propertyChangeEvent;
    }

    /**
     * Return the name of the affected property, if available.
     */
    public String getPropertyName() {
        return (this.propertyChangeEvent != null ? this.propertyChangeEvent.getPropertyName() : null);
    }

    /**
     * Return the affected value that was about to be set, if any.
     */
    public Object getValue() {
        return (this.propertyChangeEvent != null ? this.propertyChangeEvent.getNewValue() : null);
    }


}
