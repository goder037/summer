package com.rocket.summer.framework.core.convert;

import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Exception to be thrown when an actual type conversion attempt fails.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
public final class ConversionFailedException extends ConversionException {

    private final TypeDescriptor sourceType;

    private final TypeDescriptor targetType;

    private final Object value;


    /**
     * Create a new conversion exception.
     * @param sourceType the value's original type
     * @param targetType the value's target type
     * @param value the value we tried to convert
     * @param cause the cause of the conversion failure
     */
    public ConversionFailedException(TypeDescriptor sourceType, TypeDescriptor targetType, Object value, Throwable cause) {
        super("Failed to convert from type " + sourceType + " to type " + targetType + " for value '" + ObjectUtils.nullSafeToString(value) + "'", cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.value = value;
    }


    /**
     * Return the source type we tried to convert the value from.
     */
    public TypeDescriptor getSourceType() {
        return this.sourceType;
    }

    /**
     * Return the target type we tried to convert the value to.
     */
    public TypeDescriptor getTargetType() {
        return this.targetType;
    }

    /**
     * Return the offending value.
     */
    public Object getValue() {
        return this.value;
    }

}