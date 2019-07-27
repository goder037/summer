package com.rocket.summer.framework.validation;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.ConfigurablePropertyAccessor;
import com.rocket.summer.framework.beans.PropertyAccessorUtils;
import com.rocket.summer.framework.beans.PropertyEditorRegistry;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.util.Assert;

import java.beans.PropertyEditor;

/**
 * Abstract base class for {@link BindingResult} implementations that work with
 * Spring's {@link com.rocket.summer.framework.beans.PropertyAccessor} mechanism.
 * Pre-implements field access through delegation to the corresponding
 * PropertyAccessor methods.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getPropertyAccessor()
 * @see com.rocket.summer.framework.beans.PropertyAccessor
 * @see com.rocket.summer.framework.beans.ConfigurablePropertyAccessor
 */
public abstract class AbstractPropertyBindingResult extends AbstractBindingResult {

    private ConversionService conversionService;
    /**
     * Create a new AbstractPropertyBindingResult instance.
     * @param objectName the name of the target object
     * @see DefaultMessageCodesResolver
     */
    protected AbstractPropertyBindingResult(String objectName) {
        super(objectName);
    }

    public void initConversion(ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
        if (getTarget() != null) {
            getPropertyAccessor().setConversionService(conversionService);
        }
    }
    /**
     * Provide the PropertyAccessor to work with, according to the
     * concrete strategy of access.
     * <p>Note that a PropertyAccessor used by a BindingResult should
     * always have its "extractOldValueForEditor" flag set to "true"
     * by default, since this is typically possible without side effects
     * for model objects that serve as data binding target.
     * @see ConfigurablePropertyAccessor#setExtractOldValueForEditor
     */
    public abstract ConfigurablePropertyAccessor getPropertyAccessor();

    /**
     * Returns the underlying PropertyAccessor.
     * @see #getPropertyAccessor()
     */
    @Override
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return getPropertyAccessor();
    }

    /**
     * Returns the canonical property name.
     * @see com.rocket.summer.framework.beans.PropertyAccessorUtils#canonicalPropertyName
     */
    @Override
    protected String canonicalFieldName(String field) {
        return PropertyAccessorUtils.canonicalPropertyName(field);
    }

    /**
     * Determines the field type from the property type.
     * @see #getPropertyAccessor()
     */
    @Override
    public Class<?> getFieldType(String field) {
        return getPropertyAccessor().getPropertyType(fixedField(field));
    }

    /**
     * Fetches the field value from the PropertyAccessor.
     * @see #getPropertyAccessor()
     */
    @Override
    protected Object getActualFieldValue(String field) {
        return getPropertyAccessor().getPropertyValue(field);
    }

    /**
     * Retrieve the custom PropertyEditor for the given field, if any.
     * @param fixedField the fully qualified field name
     * @return the custom PropertyEditor, or <code>null</code>
     */
    protected PropertyEditor getCustomEditor(String fixedField) {
        Class<?> targetType = getPropertyAccessor().getPropertyType(fixedField);
        PropertyEditor editor = getPropertyAccessor().findCustomEditor(targetType, fixedField);
        if (editor == null) {
            editor = BeanUtils.findEditorByConvention(targetType);
        }
        return editor;
    }

}
