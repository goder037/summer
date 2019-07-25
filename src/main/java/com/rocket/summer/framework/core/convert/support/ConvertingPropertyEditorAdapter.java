package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.util.Assert;

import java.beans.PropertyEditorSupport;

/**
 * Adapter that exposes a {@link java.beans.PropertyEditor} for any given
 * {@link org.springframework.core.convert.ConversionService} and specific target type.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ConvertingPropertyEditorAdapter extends PropertyEditorSupport {

    private final ConversionService conversionService;

    private final TypeDescriptor targetDescriptor;

    private final boolean canConvertToString;


    /**
     * Create a new ConvertingPropertyEditorAdapter for a given
     * {@link org.springframework.core.convert.ConversionService}
     * and the given target type.
     * @param conversionService the ConversionService to delegate to
     * @param targetDescriptor the target type to convert to
     */
    public ConvertingPropertyEditorAdapter(ConversionService conversionService, TypeDescriptor targetDescriptor) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        Assert.notNull(targetDescriptor, "TypeDescriptor must not be null");
        this.conversionService = conversionService;
        this.targetDescriptor = targetDescriptor;
        this.canConvertToString = conversionService.canConvert(this.targetDescriptor, TypeDescriptor.valueOf(String.class));
    }


    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(this.conversionService.convert(text, TypeDescriptor.valueOf(String.class), this.targetDescriptor));
    }

    @Override
    public String getAsText() {
        if (this.canConvertToString) {
            return (String) this.conversionService.convert(getValue(), this.targetDescriptor, TypeDescriptor.valueOf(String.class));
        }
        else {
            return null;
        }
    }

}

