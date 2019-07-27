package com.rocket.summer.framework.web.bind.support;

import com.rocket.summer.framework.beans.PropertyEditorRegistrar;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.validation.BindingErrorProcessor;
import com.rocket.summer.framework.validation.MessageCodesResolver;
import com.rocket.summer.framework.validation.Validator;
import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.context.request.WebRequest;

/**
 * Convenient {@link WebBindingInitializer} for declarative configuration
 * in a Spring application context. Allows for reusing pre-configured
 * initializers with multiple controller/handlers.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see #setDirectFieldAccess
 * @see #setMessageCodesResolver
 * @see #setBindingErrorProcessor
 * @see #setValidator(Validator)
 * @see #setConversionService(ConversionService)
 * @see #setPropertyEditorRegistrar
 */
public class ConfigurableWebBindingInitializer implements WebBindingInitializer {

    private boolean autoGrowNestedPaths = true;

    private boolean directFieldAccess = false;

    private MessageCodesResolver messageCodesResolver;

    private BindingErrorProcessor bindingErrorProcessor;

    private Validator validator;

    private ConversionService conversionService;

    private PropertyEditorRegistrar[] propertyEditorRegistrars;


    /**
     * Set whether a binder should attempt to "auto-grow" a nested path that contains a null value.
     * <p>If "true", a null path location will be populated with a default object value and traversed
     * instead of resulting in an exception. This flag also enables auto-growth of collection elements
     * when accessing an out-of-bounds index.
     * <p>Default is "true" on a standard DataBinder. Note that this feature is only supported
     * for bean property access (DataBinder's default mode), not for field access.
     * @see #initBeanPropertyAccess()
     * @see com.rocket.summer.framework.validation.DataBinder#setAutoGrowNestedPaths
     */
    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    /**
     * Return whether a binder should attempt to "auto-grow" a nested path that contains a null value.
     */
    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    /**
     * Set whether to use direct field access instead of bean property access.
     * <p>Default is <code>false</code>, using bean property access.
     * Switch this to <code>true</code> in order to enforce direct field access.
     * @see com.rocket.summer.framework.validation.DataBinder#initDirectFieldAccess()
     * @see com.rocket.summer.framework.validation.DataBinder#initBeanPropertyAccess()
     */
    public final void setDirectFieldAccess(boolean directFieldAccess) {
        this.directFieldAccess = directFieldAccess;
    }

    /**
     * Return whether to use direct field access instead of bean property access.
     */
    public boolean isDirectFieldAccess() {
        return directFieldAccess;
    }

    /**
     * Set the strategy to use for resolving errors into message codes.
     * Applies the given strategy to all data binders used by this controller.
     * <p>Default is <code>null</code>, i.e. using the default strategy of
     * the data binder.
     * @see com.rocket.summer.framework.validation.DataBinder#setMessageCodesResolver
     */
    public final void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
        this.messageCodesResolver = messageCodesResolver;
    }

    /**
     * Return the strategy to use for resolving errors into message codes.
     */
    public final MessageCodesResolver getMessageCodesResolver() {
        return this.messageCodesResolver;
    }

    /**
     * Set the strategy to use for processing binding errors, that is,
     * required field errors and <code>PropertyAccessException</code>s.
     * <p>Default is <code>null</code>, that is, using the default strategy
     * of the data binder.
     * @see com.rocket.summer.framework.validation.DataBinder#setBindingErrorProcessor
     */
    public final void setBindingErrorProcessor(BindingErrorProcessor bindingErrorProcessor) {
        this.bindingErrorProcessor = bindingErrorProcessor;
    }

    /**
     * Return the strategy to use for processing binding errors.
     */
    public final BindingErrorProcessor getBindingErrorProcessor() {
        return this.bindingErrorProcessor;
    }

    /**
     * Set the Validator to apply after each binding step.
     */
    public final void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * Return the Validator to apply after each binding step, if any.
     */
    public final Validator getValidator() {
        return this.validator;
    }

    /**
     * Specify a ConversionService which will apply to every DataBinder.
     * @since 3.0
     */
    public final void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Return the ConversionService which will apply to every DataBinder.
     */
    public final ConversionService getConversionService() {
        return this.conversionService;
    }

    /**
     * Specify a single PropertyEditorRegistrar to be applied to every DataBinder.
     */
    public final void setPropertyEditorRegistrar(PropertyEditorRegistrar propertyEditorRegistrar) {
        this.propertyEditorRegistrars = new PropertyEditorRegistrar[] {propertyEditorRegistrar};
    }

    /**
     * Specify multiple PropertyEditorRegistrars to be applied to every DataBinder.
     */
    public final void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
        this.propertyEditorRegistrars = propertyEditorRegistrars;
    }

    /**
     * Return the PropertyEditorRegistrars to be applied to every DataBinder.
     */
    public final PropertyEditorRegistrar[] getPropertyEditorRegistrars() {
        return this.propertyEditorRegistrars;
    }

    public void initBinder(WebDataBinder binder, WebRequest request) {
        binder.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
        if (this.directFieldAccess) {
            binder.initDirectFieldAccess();
        }
        if (this.messageCodesResolver != null) {
            binder.setMessageCodesResolver(this.messageCodesResolver);
        }
        if (this.bindingErrorProcessor != null) {
            binder.setBindingErrorProcessor(this.bindingErrorProcessor);
        }
        if (this.validator != null && binder.getTarget() != null &&
                this.validator.supports(binder.getTarget().getClass())) {
            binder.setValidator(this.validator);
        }
        if (this.conversionService != null) {
            binder.setConversionService(this.conversionService);
        }
        if (this.propertyEditorRegistrars != null) {
            for (PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
                propertyEditorRegistrar.registerCustomEditors(binder);
            }
        }
    }

}

