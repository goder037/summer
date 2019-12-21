package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.beans.ConversionNotSupportedException;
import com.rocket.summer.framework.core.MethodParameter;

/**
 * A ConversionNotSupportedException raised while resolving a method argument.
 * Provides access to the target {@link com.rocket.summer.framework.core.MethodParameter
 * MethodParameter}.
 *
 * @author Rossen Stoyanchev
 * @since 4.2
 */
@SuppressWarnings("serial")
public class MethodArgumentConversionNotSupportedException extends ConversionNotSupportedException {

    private final String name;

    private final MethodParameter parameter;


    public MethodArgumentConversionNotSupportedException(Object value, Class<?> requiredType,
                                                         String name, MethodParameter param, Throwable cause) {

        super(value, requiredType, cause);
        this.name = name;
        this.parameter = param;
    }


    /**
     * Return the name of the method argument.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the target method parameter.
     */
    public MethodParameter getParameter() {
        return this.parameter;
    }

}

