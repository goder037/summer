package com.rocket.summer.framework.format.support;

import com.rocket.summer.framework.context.EmbeddedValueResolverAware;
import com.rocket.summer.framework.context.i18n.LocaleContextHolder;
import com.rocket.summer.framework.core.GenericTypeResolver;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.ConditionalGenericConverter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;
import com.rocket.summer.framework.core.convert.support.GenericConversionService;
import com.rocket.summer.framework.format.*;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.util.StringValueResolver;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link com.rocket.summer.framework.core.convert.ConversionService} implementation
 * designed to be configured as a {@link FormatterRegistry}.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
public class FormattingConversionService extends GenericConversionService
        implements FormatterRegistry, EmbeddedValueResolverAware {

    private StringValueResolver embeddedValueResolver;

    private final Map<AnnotationConverterKey, GenericConverter> cachedPrinters =
            new ConcurrentHashMap<AnnotationConverterKey, GenericConverter>();

    private final Map<AnnotationConverterKey, GenericConverter> cachedParsers =
            new ConcurrentHashMap<AnnotationConverterKey, GenericConverter>();


    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }


    public void addFormatter(Formatter<?> formatter) {
        Class<?> fieldType = GenericTypeResolver.resolveTypeArgument(formatter.getClass(), Formatter.class);
        if (fieldType == null) {
            throw new IllegalArgumentException("Unable to extract parameterized field type argument from Formatter [" +
                    formatter.getClass().getName() + "]; does the formatter parameterize the <T> generic type?");
        }
        addFormatterForFieldType(fieldType, formatter);
    }

    public void addFormatterForFieldType(Class<?> fieldType, Formatter<?> formatter) {
        addConverter(new PrinterConverter(fieldType, formatter, this));
        addConverter(new ParserConverter(fieldType, formatter, this));
    }

    public void addFormatterForFieldType(Class<?> fieldType, Printer<?> printer, Parser<?> parser) {
        addConverter(new PrinterConverter(fieldType, printer, this));
        addConverter(new ParserConverter(fieldType, parser, this));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addFormatterForFieldAnnotation(AnnotationFormatterFactory annotationFormatterFactory) {
        final Class<? extends Annotation> annotationType = (Class<? extends Annotation>)
                GenericTypeResolver.resolveTypeArgument(annotationFormatterFactory.getClass(), AnnotationFormatterFactory.class);
        if (annotationType == null) {
            throw new IllegalArgumentException("Unable to extract parameterized Annotation type argument from AnnotationFormatterFactory [" +
                    annotationFormatterFactory.getClass().getName() + "]; does the factory parameterize the <A extends Annotation> generic type?");
        }
        if (this.embeddedValueResolver != null && annotationFormatterFactory instanceof EmbeddedValueResolverAware) {
            ((EmbeddedValueResolverAware) annotationFormatterFactory).setEmbeddedValueResolver(this.embeddedValueResolver);
        }
        Set<Class<?>> fieldTypes = annotationFormatterFactory.getFieldTypes();
        for (final Class<?> fieldType : fieldTypes) {
            addConverter(new AnnotationPrinterConverter(annotationType, annotationFormatterFactory, fieldType));
            addConverter(new AnnotationParserConverter(annotationType, annotationFormatterFactory, fieldType));
        }
    }


    private static class PrinterConverter implements GenericConverter {

        private Class<?> fieldType;

        private TypeDescriptor printerObjectType;

        @SuppressWarnings("rawtypes")
        private Printer printer;

        private ConversionService conversionService;

        public PrinterConverter(Class<?> fieldType, Printer<?> printer, ConversionService conversionService) {
            this.fieldType = fieldType;
            this.printerObjectType = TypeDescriptor.valueOf(resolvePrinterObjectType(printer));
            this.printer = printer;
            this.conversionService = conversionService;
        }

        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(this.fieldType, String.class));
        }

        @SuppressWarnings("unchecked")
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return "";
            }
            if (!sourceType.isAssignableTo(this.printerObjectType)) {
                source = this.conversionService.convert(source, sourceType, this.printerObjectType);
            }
            return this.printer.print(source, LocaleContextHolder.getLocale());
        }

        private Class<?> resolvePrinterObjectType(Printer<?> printer) {
            return GenericTypeResolver.resolveTypeArgument(printer.getClass(), Printer.class);
        }

        public String toString() {
            return this.fieldType.getName() + " -> " + String.class.getName() + " : " + this.printer;
        }
    }


    private static class ParserConverter implements GenericConverter {

        private Class<?> fieldType;

        private Parser<?> parser;

        private ConversionService conversionService;

        public ParserConverter(Class<?> fieldType, Parser<?> parser, ConversionService conversionService) {
            this.fieldType = fieldType;
            this.parser = parser;
            this.conversionService = conversionService;
        }

        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(String.class, this.fieldType));
        }

        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            String text = (String) source;
            if (!StringUtils.hasText(text)) {
                return null;
            }
            Object result;
            try {
                result = this.parser.parse(text, LocaleContextHolder.getLocale());
            }
            catch (ParseException ex) {
                throw new IllegalArgumentException("Unable to parse '" + text + "'", ex);
            }
            if (result == null) {
                throw new IllegalStateException("Parsers are not allowed to return null");
            }
            TypeDescriptor resultType = TypeDescriptor.valueOf(result.getClass());
            if (!resultType.isAssignableTo(targetType)) {
                result = this.conversionService.convert(result, resultType, targetType);
            }
            return result;
        }

        public String toString() {
            return String.class.getName() + " -> " + this.fieldType.getName() + ": " + this.parser;
        }
    }


    private class AnnotationPrinterConverter implements ConditionalGenericConverter {

        private Class<? extends Annotation> annotationType;

        private AnnotationFormatterFactory annotationFormatterFactory;

        private Class<?> fieldType;

        public AnnotationPrinterConverter(Class<? extends Annotation> annotationType,
                                          AnnotationFormatterFactory annotationFormatterFactory, Class<?> fieldType) {
            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(fieldType, String.class));
        }

        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.getAnnotation(annotationType) != null;
        }

        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            AnnotationConverterKey converterKey = new AnnotationConverterKey(sourceType.getAnnotation(annotationType), sourceType.getObjectType());
            GenericConverter converter = cachedPrinters.get(converterKey);
            if (converter == null) {
                Printer<?> printer = annotationFormatterFactory.getPrinter(converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new PrinterConverter(fieldType, printer, FormattingConversionService.this);
                cachedPrinters.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }

        public String toString() {
            return "@" + annotationType.getName() + " " + fieldType.getName() + " -> " + String.class.getName() + ": " + annotationFormatterFactory;
        }
    }


    private class AnnotationParserConverter implements ConditionalGenericConverter {

        private Class<? extends Annotation> annotationType;

        private AnnotationFormatterFactory annotationFormatterFactory;

        private Class<?> fieldType;

        public AnnotationParserConverter(Class<? extends Annotation> annotationType,
                                         AnnotationFormatterFactory<?> annotationFormatterFactory, Class<?> fieldType) {
            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, fieldType));
        }

        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return targetType.getAnnotation(annotationType) != null;
        }

        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            AnnotationConverterKey converterKey = new AnnotationConverterKey(targetType.getAnnotation(annotationType), targetType.getObjectType());
            GenericConverter converter = cachedParsers.get(converterKey);
            if (converter == null) {
                Parser<?> parser = annotationFormatterFactory.getParser(converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new ParserConverter(fieldType, parser, FormattingConversionService.this);
                cachedParsers.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }

        public String toString() {
            return String.class.getName() + " -> @" + annotationType.getName() + " " + fieldType.getName() + ": " + annotationFormatterFactory;
        }
    }


    private static class AnnotationConverterKey {

        private final Annotation annotation;

        private final Class<?> fieldType;

        public AnnotationConverterKey(Annotation annotation, Class<?> fieldType) {
            this.annotation = annotation;
            this.fieldType = fieldType;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }

        public boolean equals(Object o) {
            if (!(o instanceof AnnotationConverterKey)) {
                return false;
            }
            AnnotationConverterKey key = (AnnotationConverterKey) o;
            return this.annotation.equals(key.annotation) && this.fieldType.equals(key.fieldType);
        }

        public int hashCode() {
            return this.annotation.hashCode() + 29 * this.fieldType.hashCode();
        }
    }

}

