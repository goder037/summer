package com.rocket.summer.framework.beans;

import com.rocket.summer.framework.context.BeansException;

import java.beans.PropertyDescriptor;

/**
 * The central interface of Spring's low-level JavaBeans infrastructure.
 *
 * <p>Typically not used directly but rather implicitly via a
 * {@link org.springframework.beans.factory.BeanFactory} or a
 * {@link org.springframework.validation.DataBinder}.
 *
 * <p>Provides operations to analyze and manipulate standard JavaBeans:
 * the ability to get and set property values (individually or in bulk),
 * get property descriptors, and query the readability/writability of properties.
 *
 * <p>This interface supports <b>nested properties</b> enabling the setting
 * of properties on subproperties to an unlimited depth.
 *
 * <p>A BeanWrapper's default for the "extractOldValueForEditor" setting
 * is "false", to avoid side effects caused by getter method invocations.
 * Turn this to "true" to expose present property values to custom editors.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13 April 2001
 * @see PropertyAccessor
 * @see PropertyEditorRegistry
 * @see PropertyAccessorFactory#forBeanPropertyAccess
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.validation.BeanPropertyBindingResult
 * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
 */
public interface BeanWrapper extends ConfigurablePropertyAccessor {

    /**
     * Return the bean instance wrapped by this object, if any.
     * @return the bean instance, or <code>null</code> if none set
     */
    Object getWrappedInstance();

    /**
     * Return the type of the wrapped JavaBean object.
     * @return the type of the wrapped bean instance,
     * or <code>null</code> if no wrapped object has been set
     */
    Class getWrappedClass();

    /**
     * Obtain the PropertyDescriptors for the wrapped object
     * (as determined by standard JavaBeans introspection).
     * @return the PropertyDescriptors for the wrapped object
     */
    PropertyDescriptor[] getPropertyDescriptors();

    /**
     * Obtain the property descriptor for a specific property
     * of the wrapped object.
     * @param propertyName the property to obtain the descriptor for
     * (may be a nested path, but no indexed/mapped property)
     * @return the property descriptor for the specified property
     * @throws InvalidPropertyException if there is no such property
     */
    PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException;

    /**
     * Set whether this BeanWrapper should attempt to "auto-grow" a nested path that contains a null value.
     * <p>If "true", a null path location will be populated with a default object value and traversed
     * instead of resulting in a {@link NullValueInNestedPathException}. Turning this flag on also
     * enables auto-growth of collection elements when accessing an out-of-bounds index.
     * <p>Default is "false" on a plain BeanWrapper.
     */
    void setAutoGrowNestedPaths(boolean autoGrowNestedPaths);

    /**
     * Return whether "auto-growing" of nested paths has been activated.
     */
    boolean isAutoGrowNestedPaths();

    /**
     * Specify a limit for array and collection auto-growing.
     * <p>Default is unlimited on a plain BeanWrapper.
     */
    void setAutoGrowCollectionLimit(int autoGrowCollectionLimit);

    /**
     * Return the limit for array and collection auto-growing.
     */
    int getAutoGrowCollectionLimit();

}