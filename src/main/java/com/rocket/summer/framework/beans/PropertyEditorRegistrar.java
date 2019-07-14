package com.rocket.summer.framework.beans;

/**
 * Interface for strategies that register custom
 * {@link java.beans.PropertyEditor property editors} with a
 * {@link org.springframework.beans.PropertyEditorRegistry property editor registry}.
 *
 * <p>This is particularly useful when you need to use the same set of
 * property editors in several different situations: write a corresponding
 * registrar and reuse that in each case.
 *
 * @author Juergen Hoeller
 * @since 1.2.6
 * @see PropertyEditorRegistry
 * @see java.beans.PropertyEditor
 */
public interface PropertyEditorRegistrar {

    /**
     * Register custom {@link java.beans.PropertyEditor PropertyEditors} with
     * the given <code>PropertyEditorRegistry</code>.
     * <p>The passed-in registry will usually be a {@link BeanWrapper} or a
     * {@link org.springframework.validation.DataBinder DataBinder}.
     * <p>It is expected that implementations will create brand new
     * <code>PropertyEditors</code> instances for each invocation of this
     * method (since <code>PropertyEditors</code> are not threadsafe).
     * @param registry the <code>PropertyEditorRegistry</code> to register the
     * custom <code>PropertyEditors</code> with
     */
    void registerCustomEditors(PropertyEditorRegistry registry);

}
