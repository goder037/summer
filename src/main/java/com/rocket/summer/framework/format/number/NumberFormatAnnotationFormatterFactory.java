package com.rocket.summer.framework.format.number;

import com.rocket.summer.framework.context.EmbeddedValueResolverAware;
import com.rocket.summer.framework.format.AnnotationFormatterFactory;
import com.rocket.summer.framework.format.Formatter;
import com.rocket.summer.framework.format.Parser;
import com.rocket.summer.framework.format.Printer;
import com.rocket.summer.framework.format.annotation.NumberFormat;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.util.StringValueResolver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Formats fields annotated with the {@link NumberFormat} annotation.
 *
 * @author Keith Donald
 * @since 3.0
 * @see NumberFormat
 */
public class NumberFormatAnnotationFormatterFactory
        implements AnnotationFormatterFactory<NumberFormat>, EmbeddedValueResolverAware {

    private final Set<Class<?>> fieldTypes;

    private StringValueResolver embeddedValueResolver;


    public NumberFormatAnnotationFormatterFactory() {
        Set<Class<?>> rawFieldTypes = new HashSet<Class<?>>(7);
        rawFieldTypes.add(Short.class);
        rawFieldTypes.add(Integer.class);
        rawFieldTypes.add(Long.class);
        rawFieldTypes.add(Float.class);
        rawFieldTypes.add(Double.class);
        rawFieldTypes.add(BigDecimal.class);
        rawFieldTypes.add(BigInteger.class);
        this.fieldTypes = Collections.unmodifiableSet(rawFieldTypes);
    }

    public final Set<Class<?>> getFieldTypes() {
        return this.fieldTypes;
    }


    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    protected String resolveEmbeddedValue(String value) {
        return (this.embeddedValueResolver != null ? this.embeddedValueResolver.resolveStringValue(value) : value);
    }


    public Printer<Number> getPrinter(NumberFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation);
    }

    public Parser<Number> getParser(NumberFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation);
    }


    private Formatter<Number> configureFormatterFrom(NumberFormat annotation) {
        if (StringUtils.hasLength(annotation.pattern())) {
            return new NumberFormatter(resolveEmbeddedValue(annotation.pattern()));
        }
        else {
            NumberFormat.Style style = annotation.style();
            if (style == NumberFormat.Style.PERCENT) {
                return new PercentFormatter();
            }
            else if (style == NumberFormat.Style.CURRENCY) {
                return new CurrencyFormatter();
            }
            else {
                return new NumberFormatter();
            }
        }
    }

}