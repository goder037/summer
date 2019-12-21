package com.rocket.summer.framework.core.annotation;

import com.rocket.summer.framework.context.NestedRuntimeException;

/**
 * Thrown by {@link AnnotationUtils} and <em>synthesized annotations</em>
 * if an annotation is improperly configured.
 *
 * @author Sam Brannen
 * @since 4.2
 * @see AnnotationUtils
 * @see SynthesizedAnnotation
 */
public class AnnotationConfigurationException extends NestedRuntimeException {

    /**
     * Construct a new {@code AnnotationConfigurationException} with the
     * supplied message.
     * @param message the detail message
     */
    public AnnotationConfigurationException(String message) {
        super(message);
    }

    /**
     * Construct a new {@code AnnotationConfigurationException} with the
     * supplied message and cause.
     * @param message the detail message
     * @param cause the root cause
     */
    public AnnotationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
