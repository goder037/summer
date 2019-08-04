package com.rocket.summer.framework.expression.spel.support;

import com.rocket.summer.framework.core.convert.ConversionException;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.support.DefaultConversionService;
import com.rocket.summer.framework.expression.TypeConverter;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;
import com.rocket.summer.framework.util.Assert;

/**
 * Default implementation of the {@link TypeConverter} interface,
 * delegating to a core Spring {@link ConversionService}.
 *
 * @author Juergen Hoeller
 * @author Andy Clement
 * @since 3.0
 * @see com.rocket.summer.framework.core.convert.ConversionService
 */
public class StandardTypeConverter implements TypeConverter {

    private final ConversionService conversionService;


    /**
     * Create a StandardTypeConverter for the default ConversionService.
     */
    public StandardTypeConverter() {
        this.conversionService = DefaultConversionService.getSharedInstance();
    }

    /**
     * Create a StandardTypeConverter for the given ConversionService.
     * @param conversionService the ConversionService to delegate to
     */
    public StandardTypeConverter(ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
    }


    @Override
    public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.canConvert(sourceType, targetType);
    }

    @Override
    public Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            return this.conversionService.convert(value, sourceType, targetType);
        }
        catch (ConversionException ex) {
            throw new SpelEvaluationException(ex, SpelMessage.TYPE_CONVERSION_ERROR,
                    (sourceType != null ? sourceType.toString() : (value != null ? value.getClass().getName() : "null")),
                    targetType.toString());
        }
    }

}

