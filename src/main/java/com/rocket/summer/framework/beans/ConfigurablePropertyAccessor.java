package com.rocket.summer.framework.beans;

/**
 * Interface that encapsulates configuration methods for a PropertyAccessor.
 * Also extends the PropertyEditorRegistry interface, which defines methods
 * for PropertyEditor management.
 *
 * <p>Serves as base interface for {@link BeanWrapper}.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see BeanWrapper
 */
public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {

    /**
     * Set whether to extract the old property value when applying a
     * property editor to a new value for a property.
     */
    void setExtractOldValueForEditor(boolean extractOldValueForEditor);

    /**
     * Return whether to extract the old property value when applying a
     * property editor to a new value for a property.
     */
    boolean isExtractOldValueForEditor();

}

