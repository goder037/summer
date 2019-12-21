package com.rocket.summer.framework.format.support;

import com.rocket.summer.framework.core.convert.support.DefaultConversionService;
import com.rocket.summer.framework.format.AnnotationFormatterFactory;
import com.rocket.summer.framework.format.FormatterRegistry;
import com.rocket.summer.framework.format.Parser;
import com.rocket.summer.framework.format.Printer;
import com.rocket.summer.framework.format.annotation.DateTimeFormat;
import com.rocket.summer.framework.format.number.NumberFormatAnnotationFormatterFactory;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringValueResolver;

import java.util.*;

/**
 * A specialization of {@link FormattingConversionService} configured by default with
 * converters and formatters appropriate for most applications.
 *
 * <p>Designed for direct instantiation but also exposes the static {@link #addDefaultFormatters}
 * utility method for ad hoc use against any {@code FormatterRegistry} instance, just
 * as {@code DefaultConversionService} exposes its own
 * {@link DefaultConversionService#addDefaultConverters addDefaultConverters} method.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class DefaultFormattingConversionService extends FormattingConversionService {

    private static final boolean jodaTimePresent = ClassUtils.isPresent(
            "org.joda.time.LocalDate", DefaultFormattingConversionService.class.getClassLoader());

    /**
     * Create a new {@code DefaultFormattingConversionService} with the set of
     * {@linkplain DefaultConversionService#addDefaultConverters default converters} and
     * {@linkplain #addDefaultFormatters default formatters}.
     */
    public DefaultFormattingConversionService() {
        this(null, true);
    }

    /**
     * Create a new {@code DefaultFormattingConversionService} with the set of
     * {@linkplain DefaultConversionService#addDefaultConverters default converters} and,
     * based on the value of {@code registerDefaultFormatters}, the set of
     * {@linkplain #addDefaultFormatters default formatters}.
     * @param registerDefaultFormatters whether to register default formatters
     */
    public DefaultFormattingConversionService(boolean registerDefaultFormatters) {
        this(null, registerDefaultFormatters);
    }

    /**
     * Create a new {@code DefaultFormattingConversionService} with the set of
     * {@linkplain DefaultConversionService#addDefaultConverters default converters} and,
     * based on the value of {@code registerDefaultFormatters}, the set of
     * {@linkplain #addDefaultFormatters default formatters}
     * @param embeddedValueResolver delegated to {@link #setEmbeddedValueResolver(StringValueResolver)}
     * prior to calling {@link #addDefaultFormatters}.
     * @param registerDefaultFormatters whether to register default formatters
     */
    public DefaultFormattingConversionService(StringValueResolver embeddedValueResolver, boolean registerDefaultFormatters) {
        this.setEmbeddedValueResolver(embeddedValueResolver);
        DefaultConversionService.addDefaultConverters(this);
        if (registerDefaultFormatters) {
            addDefaultFormatters(this);
        }
    }

    /**
     * Add formatters appropriate for most environments, including number formatters and a Joda-Time
     * date formatter if Joda-Time is present on the classpath.
     * @param formatterRegistry the service to register default formatters against
     */
    public static void addDefaultFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
//        if (jodaTimePresent) {
//            new JodaTimeFormatterRegistrar().registerFormatters(formatterRegistry);
//        } else {
//            formatterRegistry.addFormatterForFieldAnnotation(new NoJodaDateTimeFormatAnnotationFormatterFactory());
//        }
        formatterRegistry.addFormatterForFieldAnnotation(new NoJodaDateTimeFormatAnnotationFormatterFactory());
    }


    /**
     * Dummy AnnotationFormatterFactory that simply fails if @DateTimeFormat is being used
     * without the JodaTime library being present.
     */
    private static final class NoJodaDateTimeFormatAnnotationFormatterFactory
            implements AnnotationFormatterFactory<DateTimeFormat> {

        private final Set<Class<?>> fieldTypes;

        public NoJodaDateTimeFormatAnnotationFormatterFactory() {
            Set<Class<?>> rawFieldTypes = new HashSet<Class<?>>(4);
            rawFieldTypes.add(Date.class);
            rawFieldTypes.add(Calendar.class);
            rawFieldTypes.add(Long.class);
            this.fieldTypes = Collections.unmodifiableSet(rawFieldTypes);
        }

        public Set<Class<?>> getFieldTypes() {
            return this.fieldTypes;
        }

        public Printer<?> getPrinter(DateTimeFormat annotation, Class<?> fieldType) {
            throw new IllegalStateException("JodaTime library not available - @DateTimeFormat not supported");
        }

        public Parser<?> getParser(DateTimeFormat annotation, Class<?> fieldType) {
            throw new IllegalStateException("JodaTime library not available - @DateTimeFormat not supported");
        }
    }

}

