package com.rocket.summer.framework.core.convert;

/**
 * A service interface for type conversion. This is the entry point into the convert system.
 * Call {@link #convert(Object, Class)} to perform a thread-safe type conversion using this system.
 *
 * @author Keith Donald
 * @since 3.0
 */
public interface ConversionService {

    /**
     * Returns true if objects of sourceType can be converted to targetType.
     * @param sourceType the source type to convert from (required)
     * @param targetType the target type to convert to (required)
     * @return true if a conversion can be performed, false if not
     */
    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    /**
     * Convert the source to targetType.
     * @param source the source object to convert (may be null)
     * @param targetType the target type to convert to (required)
     * @return the converted object, an instance of targetType
     * @throws ConversionException if an exception occurred
     */
    <T> T convert(Object source, Class<T> targetType);

    /**
     * Returns true if objects of sourceType can be converted to the targetType.
     * The TypeDescriptors provide additional context about the field locations where conversion would occur, often object property locations.
     * This flavor of the canConvert operation exists mainly for use by a general purpose data mapping framework, and not for use by user code.
     * @param sourceType context about the source type to convert from (required)
     * @param targetType context about the target type to convert to (required)
     * @return true if a conversion can be performed between the source and target types, false if not
     */
    boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

    /**
     * Convert the source to targetType.
     * The TypeDescriptors provide additional context about the field locations where conversion will occur, often object property locations.
     * This flavor of the convert operation exists mainly for use by a general purpose data mapping framework, and not for use by user code.
     * @param source the source object to convert (may be null)
     * @param sourceType context about the source type converting from (required)
     * @param targetType context about the target type to convert to (required)
     * @return the converted object, an instance of {@link TypeDescriptor#getObjectType() targetType}</code>
     * @throws ConversionException if an exception occurred
     */
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

}
