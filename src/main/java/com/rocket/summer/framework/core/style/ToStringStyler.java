package com.rocket.summer.framework.core.style;

/**
 * A strategy interface for pretty-printing {@code toString()} methods.
 * Encapsulates the print algorithms; some other object such as a builder
 * should provide the workflow.
 *
 * @author Keith Donald
 * @since 1.2.2
 */
public interface ToStringStyler {

    /**
     * Style a {@code toString()}'ed object before its fields are styled.
     * @param buffer the buffer to print to
     * @param obj the object to style
     */
    void styleStart(StringBuilder buffer, Object obj);

    /**
     * Style a {@code toString()}'ed object after it's fields are styled.
     * @param buffer the buffer to print to
     * @param obj the object to style
     */
    void styleEnd(StringBuilder buffer, Object obj);

    /**
     * Style a field value as a string.
     * @param buffer the buffer to print to
     * @param fieldName the he name of the field
     * @param value the field value
     */
    void styleField(StringBuilder buffer, String fieldName, Object value);

    /**
     * Style the given value.
     * @param buffer the buffer to print to
     * @param value the field value
     */
    void styleValue(StringBuilder buffer, Object value);

    /**
     * Style the field separator.
     * @param buffer buffer to print to
     */
    void styleFieldSeparator(StringBuilder buffer);

}

