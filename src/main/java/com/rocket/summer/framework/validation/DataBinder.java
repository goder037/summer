package com.rocket.summer.framework.validation;

import com.rocket.summer.framework.beans.*;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.PatternMatchUtils;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

/**
 * Binder that allows for setting property values onto a target object,
 * including support for validation and binding result analysis.
 * The binding process can be customized through specifying allowed fields,
 * required fields, custom editors, etc.
 *
 * <p>Note that there are potential security implications in failing to set an array
 * of allowed fields. In the case of HTTP form POST data for example, malicious clients
 * can attempt to subvert an application by supplying values for fields or properties
 * that do not exist on the form. In some cases this could lead to illegal data being
 * set on command objects <i>or their nested objects</i>. For this reason, it is
 * <b>highly recommended to specify the {@link #setAllowedFields allowedFields} property</b>
 * on the DataBinder.
 *
 * <p>The binding results can be examined via the {@link BindingResult} interface,
 * extending the {@link Errors} interface: see the {@link #getBindingResult()} method.
 * Missing fields and property access exceptions will be converted to {@link FieldError FieldErrors},
 * collected in the Errors instance, using the following error codes:
 *
 * <ul>
 * <li>Missing field error: "required"
 * <li>Type mismatch error: "typeMismatch"
 * <li>Method invocation error: "methodInvocation"
 * </ul>
 *
 * <p>By default, binding errors get resolved through the {@link BindingErrorProcessor}
 * strategy, processing for missing fields and property access exceptions: see the
 * {@link #setBindingErrorProcessor} method. You can override the default strategy
 * if needed, for example to generate different error codes.
 *
 * <p>Custom validation errors can be added afterwards. You will typically want to resolve
 * such error codes into proper user-visible error messages; this can be achieved through
 * resolving each error via a {@link com.rocket.summer.framework.context.MessageSource}, which is
 * able to resolve an {@link ObjectError}/{@link FieldError} through its
 * {@link com.rocket.summer.framework.context.MessageSource#getMessage(com.rocket.summer.framework.context.MessageSourceResolvable, java.util.Locale)}
 * method. The list of message codes can be customized through the {@link MessageCodesResolver}
 * strategy: see the {@link #setMessageCodesResolver} method. {@link DefaultMessageCodesResolver}'s
 * javadoc states details on the default resolution rules.
 *
 * <p>This generic data binder can be used in any kind of environment.
 * It is typically used by Spring web MVC controllers, via the web-specific
 * subclasses {@link com.rocket.summer.framework.web.bind.ServletRequestDataBinder}
 * and {@link com.rocket.summer.framework.web.portlet.bind.PortletRequestDataBinder}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see #setAllowedFields
 * @see #setRequiredFields
 * @see #registerCustomEditor
 * @see #setMessageCodesResolver
 * @see #setBindingErrorProcessor
 * @see #bind
 * @see #getBindingResult
 * @see DefaultMessageCodesResolver
 * @see DefaultBindingErrorProcessor
 * @see com.rocket.summer.framework.context.MessageSource
 * @see com.rocket.summer.framework.web.bind.ServletRequestDataBinder
 */
public class DataBinder implements PropertyEditorRegistry, TypeConverter {

    /** Default object name used for binding: "target" */
    public static final String DEFAULT_OBJECT_NAME = "target";

    /** Default limit for array and collection growing: 256 */
    public static final int DEFAULT_AUTO_GROW_COLLECTION_LIMIT = 256;

    private final Object target;

    private final String objectName;

    private Validator validator;

    private ConversionService conversionService;

    private SimpleTypeConverter typeConverter;

    private AbstractPropertyBindingResult bindingResult;

    private boolean autoGrowNestedPaths = true;

    private BindingErrorProcessor bindingErrorProcessor = new DefaultBindingErrorProcessor();

    private String[] disallowedFields;

    private String[] requiredFields;

    private String[] allowedFields;

    private boolean ignoreUnknownFields = true;

    private boolean ignoreInvalidFields = false;

    private int autoGrowCollectionLimit = DEFAULT_AUTO_GROW_COLLECTION_LIMIT;

    /**
     * We'll create a lot of DataBinder instances: Let's use a static logger.
     */
    protected static final Log logger = LogFactory.getLog(DataBinder.class);

    /**
     * Create a new DataBinder instance, with default object name.
     * @param target the target object to bind onto (or <code>null</code>
     * if the binder is just used to convert a plain parameter value)
     * @see #DEFAULT_OBJECT_NAME
     */
    public DataBinder(Object target) {
        this(target, DEFAULT_OBJECT_NAME);
    }

    /**
     * Set the strategy to use for processing binding errors, that is,
     * required field errors and <code>PropertyAccessException</code>s.
     * <p>Default is a DefaultBindingErrorProcessor.
     * @see DefaultBindingErrorProcessor
     */
    public void setBindingErrorProcessor(BindingErrorProcessor bindingErrorProcessor) {
        Assert.notNull(bindingErrorProcessor, "BindingErrorProcessor must not be null");
        this.bindingErrorProcessor = bindingErrorProcessor;
    }

    /**
     * Set the Validator to apply after each binding step.
     */
    public void setValidator(Validator validator) {
        if (validator != null && (getTarget() != null && !validator.supports(getTarget().getClass()))) {
            throw new IllegalStateException("Invalid target for Validator [" + validator + "]: " + getTarget());
        }
        this.validator = validator;
    }

    /**
     * Create a new DataBinder instance.
     * @param target the target object to bind onto (or <code>null</code>
     * if the binder is just used to convert a plain parameter value)
     * @param objectName the name of the target object
     */
    public DataBinder(Object target, String objectName) {
        this.target = target;
        this.objectName = objectName;
    }

    @Override
    public PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath) {
        return getPropertyEditorRegistry().findCustomEditor(requiredType, propertyPath);
    }

    /**
     * Return the underlying PropertyAccessor of this binder's BindingResult.
     */
    protected ConfigurablePropertyAccessor getPropertyAccessor() {
        return getInternalBindingResult().getPropertyAccessor();
    }

    /**
     * Return the wrapped target object.
     */
    public Object getTarget() {
        return this.target;
    }

    /**
     * Return the name of the bound object.
     */
    public String getObjectName() {
        return this.objectName;
    }

    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
        return getTypeConverter().convertIfNecessary(value, requiredType);
    }

    public <T> T convertIfNecessary(
            Object value, Class<T> requiredType, MethodParameter methodParam) throws TypeMismatchException {

        return getTypeConverter().convertIfNecessary(value, requiredType, methodParam);
    }

    /**
     * Return the underlying TypeConverter of this binder's BindingResult.
     */
    protected TypeConverter getTypeConverter() {
        if (getTarget() != null) {
            return getInternalBindingResult().getPropertyAccessor();
        }
        else {
            return getSimpleTypeConverter();
        }
    }

    /**
     * Close this DataBinder, which may result in throwing
     * a BindException if it encountered any errors.
     * @return the model Map, containing target object and Errors instance
     * @throws BindException if there were any errors in the bind operation
     * @see BindingResult#getModel()
     */
    public Map<?, ?> close() throws BindException {
        if (getBindingResult().hasErrors()) {
            throw new BindException(getBindingResult());
        }
        return getBindingResult().getModel();
    }

    /**
     * Return the internal BindingResult held by this DataBinder,
     * as AbstractPropertyBindingResult.
     */
    protected AbstractPropertyBindingResult getInternalBindingResult() {
        if (this.bindingResult == null) {
            initBeanPropertyAccess();
        }
        return this.bindingResult;
    }

    /**
     * Initialize standard JavaBean property access for this DataBinder.
     * <p>This is the default; an explicit call just leads to eager initialization.
     * @see #initDirectFieldAccess()
     */
    public void initBeanPropertyAccess() {
        Assert.state(this.bindingResult == null,
                "DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
        this.bindingResult = new BeanPropertyBindingResult(
                getTarget(), getObjectName(), isAutoGrowNestedPaths(), getAutoGrowCollectionLimit());
        if (this.conversionService != null) {
            this.bindingResult.initConversion(this.conversionService);
        }
    }

    /**
     * Return the current limit for array and collection auto-growing.
     */
    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }

    /**
     * Return whether "auto-growing" of nested paths has been activated.
     */
    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        getPropertyEditorRegistry().registerCustomEditor(requiredType, propertyEditor);
    }

    public void registerCustomEditor(Class<?> requiredType, String field, PropertyEditor propertyEditor) {
        getPropertyEditorRegistry().registerCustomEditor(requiredType, field, propertyEditor);
    }

    /**
     * Return the underlying TypeConverter of this binder's BindingResult.
     */
    protected PropertyEditorRegistry getPropertyEditorRegistry() {
        if (getTarget() != null) {
            return getInternalBindingResult().getPropertyAccessor();
        }
        else {
            return getSimpleTypeConverter();
        }
    }

    /**
     * Return this binder's underlying SimpleTypeConverter.
     */
    protected SimpleTypeConverter getSimpleTypeConverter() {
        if (this.typeConverter == null) {
            this.typeConverter = new SimpleTypeConverter();
            if (this.conversionService != null) {
                this.typeConverter.setConversionService(this.conversionService);
            }
        }
        return this.typeConverter;
    }

    /**
     * Actual implementation of the binding process, working with the
     * passed-in MutablePropertyValues instance.
     * @param mpvs the property values to bind,
     * as MutablePropertyValues instance
     * @see #checkAllowedFields
     * @see #checkRequiredFields
     * @see #applyPropertyValues
     */
    protected void doBind(MutablePropertyValues mpvs) {
        checkAllowedFields(mpvs);
        checkRequiredFields(mpvs);
        applyPropertyValues(mpvs);
    }

    /**
     * Return the fields that should be allowed for binding.
     * @return array of field names
     */
    public String[] getAllowedFields() {
        return this.allowedFields;
    }

    /**
     * Apply given property values to the target object.
     * <p>Default implementation applies all of the supplied property
     * values as bean property values. By default, unknown fields will
     * be ignored.
     * @param mpvs the property values to be bound (can be modified)
     * @see #getTarget
     * @see #getPropertyAccessor
     * @see #isIgnoreUnknownFields
     * @see #getBindingErrorProcessor
     * @see BindingErrorProcessor#processPropertyAccessException
     */
    protected void applyPropertyValues(MutablePropertyValues mpvs) {
        try {
            // Bind request parameters onto target object.
            getPropertyAccessor().setPropertyValues(mpvs, isIgnoreUnknownFields(), isIgnoreInvalidFields());
        }
        catch (PropertyBatchUpdateException ex) {
            // Use bind error processor to create FieldErrors.
            for (PropertyAccessException pae : ex.getPropertyAccessExceptions()) {
                getBindingErrorProcessor().processPropertyAccessException(pae, getInternalBindingResult());
            }
        }
    }

    /**
     * Return whether to ignore invalid fields when binding.
     */
    public boolean isIgnoreInvalidFields() {
        return this.ignoreInvalidFields;
    }

    /**
     * Return whether to ignore unknown fields when binding.
     */
    public boolean isIgnoreUnknownFields() {
        return this.ignoreUnknownFields;
    }

    /**
     * Return the strategy for processing binding errors.
     */
    public BindingErrorProcessor getBindingErrorProcessor() {
        return this.bindingErrorProcessor;
    }

    /**
     * Return if the given field is allowed for binding.
     * Invoked for each passed-in property value.
     * <p>The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches,
     * as well as direct equality, in the specified lists of allowed fields and
     * disallowed fields. A field matching a disallowed pattern will not be accepted
     * even if it also happens to match a pattern in the allowed list.
     * <p>Can be overridden in subclasses.
     * @param field the field to check
     * @return if the field is allowed
     * @see #setAllowedFields
     * @see #setDisallowedFields
     * @see com.rocket.summer.framework.util.PatternMatchUtils#simpleMatch(String, String)
     */
    protected boolean isAllowed(String field) {
        String[] allowed = getAllowedFields();
        String[] disallowed = getDisallowedFields();
        return ((ObjectUtils.isEmpty(allowed) || PatternMatchUtils.simpleMatch(allowed, field)) &&
                (ObjectUtils.isEmpty(disallowed) || !PatternMatchUtils.simpleMatch(disallowed, field)));
    }

    /**
     * Return the fields that should <i>not</i> be allowed for binding.
     * @return array of field names
     */
    public String[] getDisallowedFields() {
        return this.disallowedFields;
    }

    /**
     * Return the fields that are required for each binding process.
     * @return array of field names
     */
    public String[] getRequiredFields() {
        return this.requiredFields;
    }

    /**
     * Check the given property values against the required fields,
     * generating missing field errors where appropriate.
     * @param mpvs the property values to be bound (can be modified)
     * @see #getRequiredFields
     * @see #getBindingErrorProcessor
     * @see BindingErrorProcessor#processMissingFieldError
     */
    protected void checkRequiredFields(MutablePropertyValues mpvs) {
        String[] requiredFields = getRequiredFields();
        if (!ObjectUtils.isEmpty(requiredFields)) {
            Map<String, PropertyValue> propertyValues = new HashMap<String, PropertyValue>();
            PropertyValue[] pvs = mpvs.getPropertyValues();
            for (PropertyValue pv : pvs) {
                String canonicalName = PropertyAccessorUtils.canonicalPropertyName(pv.getName());
                propertyValues.put(canonicalName, pv);
            }
            for (String field : requiredFields) {
                PropertyValue pv = propertyValues.get(field);
                boolean empty = (pv == null || pv.getValue() == null);
                if (!empty) {
                    if (pv.getValue() instanceof String) {
                        empty = !StringUtils.hasText((String) pv.getValue());
                    }
                    else if (pv.getValue() instanceof String[]) {
                        String[] values = (String[]) pv.getValue();
                        empty = (values.length == 0 || !StringUtils.hasText(values[0]));
                    }
                }
                if (empty) {
                    // Use bind error processor to create FieldError.
                    getBindingErrorProcessor().processMissingFieldError(field, getInternalBindingResult());
                    // Remove property from property values to bind:
                    // It has already caused a field error with a rejected value.
                    if (pv != null) {
                        mpvs.removePropertyValue(pv);
                        propertyValues.remove(field);
                    }
                }
            }
        }
    }

    /**
     * Check the given property values against the allowed fields,
     * removing values for fields that are not allowed.
     * @param mpvs the property values to be bound (can be modified)
     * @see #getAllowedFields
     * @see #isAllowed(String)
     */
    protected void checkAllowedFields(MutablePropertyValues mpvs) {
        PropertyValue[] pvs = mpvs.getPropertyValues();
        for (PropertyValue pv : pvs) {
            String field = PropertyAccessorUtils.canonicalPropertyName(pv.getName());
            if (!isAllowed(field)) {
                mpvs.removePropertyValue(pv);
                getBindingResult().recordSuppressedField(field);
                if (logger.isDebugEnabled()) {
                    logger.debug("Field [" + field + "] has been removed from PropertyValues " +
                            "and will not be bound, because it has not been found in the list of allowed fields");
                }
            }
        }
    }

    /**
     * Return the BindingResult instance created by this DataBinder.
     * This allows for convenient access to the binding results after
     * a bind operation.
     * @return the BindingResult instance, to be treated as BindingResult
     * or as Errors instance (Errors is a super-interface of BindingResult)
     * @see Errors
     * @see #bind
     */
    public BindingResult getBindingResult() {
        return getInternalBindingResult();
    }

    /**
     * Specify a Spring 3.0 ConversionService to use for converting
     * property values, as an alternative to JavaBeans PropertyEditors.
     */
    public void setConversionService(ConversionService conversionService) {
        Assert.state(this.conversionService == null, "DataBinder is already initialized with ConversionService");
        this.conversionService = conversionService;
        if (this.bindingResult != null && conversionService != null) {
            this.bindingResult.initConversion(conversionService);
        }
    }

    /**
     * Invoke the specified Validator, if any, with the given validation hints.
     * <p>Note: Validation hints may get ignored by the actual target Validator.
     * @param validationHints one or more hint objects to be passed to a {@link SmartValidator}
     * @see #setValidator(Validator)
     * @see SmartValidator#validate(Object, Errors, Object...)
     */
    public void validate(Object... validationHints) {
        Validator validator = getValidator();
        if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
            ((SmartValidator) validator).validate(getTarget(), getBindingResult(), validationHints);
        }
        else if (validator != null) {
            validator.validate(getTarget(), getBindingResult());
        }
    }

    /**
     * Return the associated ConversionService, if any.
     */
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    /**
     * Return the Validator to apply after each binding step, if any.
     */
    public Validator getValidator() {
        return this.validator;
    }

    /**
     * Set whether this binder should attempt to "auto-grow" a nested path that contains a null value.
     * <p>If "true", a null path location will be populated with a default object value and traversed
     * instead of resulting in an exception. This flag also enables auto-growth of collection elements
     * when accessing an out-of-bounds index.
     * <p>Default is "true" on a standard DataBinder. Note that this feature is only supported
     * for bean property access (DataBinder's default mode), not for field access.
     * @see #initBeanPropertyAccess()
     * @see com.rocket.summer.framework.beans.BeanWrapper#setAutoGrowNestedPaths
     */
    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        Assert.state(this.bindingResult == null,
                "DataBinder is already initialized - call setAutoGrowNestedPaths before other configuration methods");
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    /**
     * Initialize direct field access for this DataBinder,
     * as alternative to the default bean property access.
     * @see #initBeanPropertyAccess()
     */
    public void initDirectFieldAccess() {
        Assert.state(this.bindingResult == null,
                "DataBinder is already initialized - call initDirectFieldAccess before other configuration methods");
        this.bindingResult = new DirectFieldBindingResult(getTarget(), getObjectName());
        if (this.conversionService != null) {
            this.bindingResult.initConversion(this.conversionService);
        }
    }

    /**
     * Set the strategy to use for resolving errors into message codes.
     * Applies the given strategy to the underlying errors holder.
     * <p>Default is a DefaultMessageCodesResolver.
     * @see BeanPropertyBindingResult#setMessageCodesResolver
     * @see DefaultMessageCodesResolver
     */
    public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
        getInternalBindingResult().setMessageCodesResolver(messageCodesResolver);
    }
}
