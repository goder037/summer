package com.rocket.summer.framework.boot.bind;

import com.rocket.summer.framework.beans.NotWritablePropertyException;

/**
 * A custom {@link NotWritablePropertyException} that is thrown when a failure occurs
 * during relaxed binding.
 *
 * @author Andy Wilkinson
 * @since 1.3.0
 * @see RelaxedDataBinder
 */
public class RelaxedBindingNotWritablePropertyException
        extends NotWritablePropertyException {

    private final String message;

    private final PropertyOrigin propertyOrigin;

    RelaxedBindingNotWritablePropertyException(NotWritablePropertyException ex,
                                               PropertyOrigin propertyOrigin) {
        super(ex.getBeanClass(), ex.getPropertyName());
        this.propertyOrigin = propertyOrigin;
        this.message = "Failed to bind '" + propertyOrigin.getName() + "' from '"
                + propertyOrigin.getSource().getName() + "' to '" + ex.getPropertyName()
                + "' property on '" + ex.getBeanClass().getName() + "'";
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public PropertyOrigin getPropertyOrigin() {
        return this.propertyOrigin;
    }

}

