package com.rocket.summer.framework.core.convert;

/**
 * Thrown when a suitable converter could not be found in a conversion service.
 *
 * @author Keith Donald
 * @since 3.0
 */
@SuppressWarnings("serial")
public final class ConverterNotFoundException extends ConversionException {

    private final TypeDescriptor sourceType;

    private final TypeDescriptor targetType;


    /**
     * Creates a new conversion executor not found exception.
     * @param sourceType the source type requested to convert from
     * @param targetType the target type requested to convert to
     * @param message a descriptive message
     */
    public ConverterNotFoundException(TypeDescriptor sourceType, TypeDescriptor targetType) {
        super("No converter found capable of converting from '" + sourceType.getName() +
                "' to '" + targetType.getName() + "'");
        this.sourceType = sourceType;
        this.targetType = targetType;
    }


    /**
     * Returns the source type that was requested to convert from.
     */
    public TypeDescriptor getSourceType() {
        return this.sourceType;
    }

    /**
     * Returns the target type that was requested to convert to.
     */
    public TypeDescriptor getTargetType() {
        return this.targetType;
    }

}
