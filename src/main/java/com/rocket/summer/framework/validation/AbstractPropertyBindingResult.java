package com.rocket.summer.framework.validation;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.ConfigurablePropertyAccessor;
import com.rocket.summer.framework.beans.PropertyAccessorUtils;
import com.rocket.summer.framework.beans.PropertyEditorRegistry;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.support.ConvertingPropertyEditorAdapter;
import com.rocket.summer.framework.util.Assert;

import java.beans.PropertyEditor;

/**
 * Abstract base class for {@link BindingResult} implementations that work with
 * Spring's {@link org.springframework.beans.PropertyAccessor} mechanism.
 * Pre-implements field access through delegation to the corresponding
 * PropertyAccessor methods.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getPropertyAccessor()
 * @see org.springframework.beans.PropertyAccessor
 * @see org.springframework.beans.ConfigurablePropertyAccessor
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
     * Returns the underlying PropertyAccessor.
     * @see #getPropertyAccessor()
     */
    @Override
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return getPropertyAccessor();
    }

    /**
     * Returns the canonical property name.
     * @see org.springframework.beans.PropertyAccessorUtils#canonicalPropertyName
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
     * Formats the field value based on registered PropertyEditors.
     * @see #getCustomEditor
     */
    @Override
    protected Object formatFieldValue(String field, Object value) {
        String fixedField = fixedField(field);
        // Try custom editor...
        PropertyEditor customEditor = getCustomEditor(fixedField);
        if (customEditor != null) {
            customEditor.setValue(value);
            String textValue = customEditor.getAsText();
            // If the PropertyEditor returned null, there is no appropriate
            // text representation for this value: only use it if non-null.
            if (textValue != null) {
                return textValue;
            }
        }
        if (this.conversionService != null) {
            // Try custom converter...
            TypeDescriptor fieldDesc = getPropertyAccessor().getPropertyTypeDescriptor(fixedField);
            TypeDescriptor strDesc = TypeDescriptor.valueOf(String.class);
            if (fieldDesc != null && this.conversionService.canConvert(fieldDesc, strDesc)) {
                return this.conversionService.convert(value, fieldDesc, strDesc);
            }
        }
        return value;
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

    /**
     * This implementation exposes a PropertyEditor adapter for a Formatter,
     * if applicable.
     */
    @Override
    public PropertyEditor findEditor(String field, Class<?> valueType) {
        Class<?> valueTypeForLookup = valueType;
        if (valueTypeForLookup == null) {
            valueTypeForLookup = getFieldType(field);
        }
        PropertyEditor editor = super.findEditor(field, valueTypeForLookup);
        if (editor == null && this.conversionService != null) {
            TypeDescriptor td = null;
            if (field != null) {
                TypeDescriptor ptd = getPropertyAccessor().getPropertyTypeDescriptor(fixedField(field));
                if (valueType == null || valueType.isAssignableFrom(ptd.getType())) {
                    td = ptd;
                }
            }
            if (td == null) {
                td = TypeDescriptor.valueOf(valueTypeForLookup);
            }
            if (this.conversionService.canConvert(TypeDescriptor.valueOf(String.class), td)) {
                editor = new ConvertingPropertyEditorAdapter(this.conversionService, td);
            }
        }
        return editor;
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

}
