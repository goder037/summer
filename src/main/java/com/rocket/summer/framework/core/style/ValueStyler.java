package com.rocket.summer.framework.core.style;

/**
 * Strategy that encapsulates value String styling algorithms
 * according to Spring conventions.
 *
 * @author Keith Donald
 * @since 1.2.2
 */
public interface ValueStyler {

    /**
     * Style the given value, returning a String representation.
     * @param value the Object value to style
     * @return the styled String
     */
    String style(Object value);

}