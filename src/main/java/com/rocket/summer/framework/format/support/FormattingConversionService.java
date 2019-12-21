package com.rocket.summer.framework.format.support;

import com.rocket.summer.framework.context.EmbeddedValueResolverAware;
import com.rocket.summer.framework.context.i18n.LocaleContextHolder;
import com.rocket.summer.framework.core.DecoratingProxy;
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
            new ConcurrentHashMap<AnnotationConverterKey, GenericConverter>(64);

    private final Map<AnnotationConverterKey, GenericConverter> cachedParsers =
            new ConcurrentHashMap<AnnotationConverterKey, GenericConverter>(64);


    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }


    @Override
    public void addFormatter(Formatter<?> formatter) {
        addFormatterForFieldType(getFieldType(formatter), formatter);
    }

    @Override
    public void addFormatterForFieldType(Class<?> fieldType, Formatter<?> formatter) {
        addConverter(new PrinterConverter(fieldType, formatter, this));
        addConverter(new ParserConverter(fieldType, formatter, this));
    }

    @Override
    public void addFormatterForFieldType(Class<?> fieldType, Printer<?> printer, Parser<?> parser) {
        addConverter(new PrinterConverter(fieldType, printer, this));
        addConverter(new ParserConverter(fieldType, parser, this));
    }

    @Override
    public void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> annotationFormatterFactory) {
        Class<? extends Annotation> annotationType = getAnnotationType(annotationFormatterFactory);
        if (this.embeddedValueResolver != null && annotationFormatterFactory instanceof EmbeddedValueResolverAware) {
            ((EmbeddedValueResolverAware) annotationFormatterFactory).setEmbeddedValueResolver(this.embeddedValueResolver);
        }
        Set<Class<?>> fieldTypes = annotationFormatterFactory.getFieldTypes();
        for (Class<?> fieldType : fieldTypes) {
            addConverter(new AnnotationPrinterConverter(annotationType, annotationFormatterFactory, fieldType));
            addConverter(new AnnotationParserConverter(annotationType, annotationFormatterFactory, fieldType));
        }
    }


    static Class<?> getFieldType(Formatter<?> formatter) {
        Class<?> fieldType = GenericTypeResolver.resolveTypeArgument(formatter.getClass(), Formatter.class);
        if (fieldType == null && formatter instanceof DecoratingProxy) {
            fieldType = GenericTypeResolver.resolveTypeArgument(
                    ((DecoratingProxy) formatter).getDecoratedClass(), Formatter.class);
        }
        if (fieldType == null) {
            throw new IllegalArgumentException("Unable to extract the parameterized field type from Formatter [" +
                    formatter.getClass().getName() + "]; does the class parameterize the <T> generic type?");
        }
        return fieldType;
    }

    @SuppressWarnings("unchecked")
    static Class<? extends Annotation> getAnnotationType(AnnotationFormatterFactory<? extends Annotation> factory) {
        Class<? extends Annotation> annotationType = (Class<? extends Annotation>)
                GenericTypeResolver.resolveTypeArgument(factory.getClass(), AnnotationFormatterFactory.class);
        if (annotationType == null) {
            throw new IllegalArgumentException("Unable to extract parameterized Annotation type argument from " +
                    "AnnotationFormatterFactory [" + factory.getClass().getName() +
                    "]; does the factory parameterize the <A extends Annotation> generic type?");
        }
        return annotationType;
    }


    private static class PrinterConverter implements GenericConverter {

        private final Class<?> fieldType;

        private final TypeDescriptor printerObjectType;

        @SuppressWarnings("rawtypes")
        private final Printer printer;

        private final ConversionService conversionService;

        public PrinterConverter(Class<?> fieldType, Printer<?> printer, ConversionService conversionService) {
            this.fieldType = fieldType;
            this.printerObjectType = TypeDescriptor.valueOf(resolvePrinterObjectType(printer));
            this.printer = printer;
            this.conversionService = conversionService;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(this.fieldType, String.class));
        }

        @Override
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

        @Override
        public String toString() {
            return (this.fieldType.getName() + " -> " + String.class.getName() + " : " + this.printer);
        }
    }


    private static class ParserConverter implements GenericConverter {

        private final Class<?> fieldType;

        private final Parser<?> parser;

        private final ConversionService conversionService;

        public ParserConverter(Class<?> fieldType, Parser<?> parser, ConversionService conversionService) {
            this.fieldType = fieldType;
            this.parser = parser;
            this.conversionService = conversionService;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(String.class, this.fieldType));
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            String text = (String) source;
            if (!StringUtils.hasText(text)) {
                return null;
            }
            Object result;
            try {
                result = this.parser.parse(text, LocaleContextHolder.getLocale());
            }
            catch (IllegalArgumentException ex) {
                throw ex;
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Parse attempt failed for value [" + text + "]", ex);
            }
            if (result == null) {
                throw new IllegalStateException("Parsers are not allowed to return null: " + this.parser);
            }
            TypeDescriptor resultType = TypeDescriptor.valueOf(result.getClass());
            if (!resultType.isAssignableTo(targetType)) {
                result = this.conversionService.convert(result, resultType, targetType);
            }
            return result;
        }

        @Override
        public String toString() {
            return (String.class.getName() + " -> " + this.fieldType.getName() + ": " + this.parser);
        }
    }


    private class AnnotationPrinterConverter implements ConditionalGenericConverter {

        private final Class<? extends Annotation> annotationType;

        @SuppressWarnings("rawtypes")
        private final AnnotationFormatterFactory annotationFormatterFactory;

        private final Class<?> fieldType;

        public AnnotationPrinterConverter(Class<? extends Annotation> annotationType,
                                          AnnotationFormatterFactory<?> annotationFormatterFactory, Class<?> fieldType) {

            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(this.fieldType, String.class));
        }

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.hasAnnotation(this.annotationType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            Annotation ann = sourceType.getAnnotation(this.annotationType);
            if (ann == null) {
                throw new IllegalStateException(
                        "Expected [" + this.annotationType.getName() + "] to be present on " + sourceType);
            }
            AnnotationConverterKey converterKey = new AnnotationConverterKey(ann, sourceType.getObjectType());
            GenericConverter converter = cachedPrinters.get(converterKey);
            if (converter == null) {
                Printer<?> printer = this.annotationFormatterFactory.getPrinter(
                        converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new PrinterConverter(this.fieldType, printer, FormattingConversionService.this);
                cachedPrinters.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }

        @Override
        public String toString() {
            return ("@" + this.annotationType.getName() + " " + this.fieldType.getName() + " -> " +
                    String.class.getName() + ": " + this.annotationFormatterFactory);
        }
    }


    private class AnnotationParserConverter implements ConditionalGenericConverter {

        private final Class<? extends Annotation> annotationType;

        @SuppressWarnings("rawtypes")
        private final AnnotationFormatterFactory annotationFormatterFactory;

        private final Class<?> fieldType;

        public AnnotationParserConverter(Class<? extends Annotation> annotationType,
                                         AnnotationFormatterFactory<?> annotationFormatterFactory, Class<?> fieldType) {

            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(String.class, fieldType));
        }

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return targetType.hasAnnotation(this.annotationType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            Annotation ann = targetType.getAnnotation(this.annotationType);
            if (ann == null) {
                throw new IllegalStateException(
                        "Expected [" + this.annotationType.getName() + "] to be present on " + targetType);
            }
            AnnotationConverterKey converterKey = new AnnotationConverterKey(ann, targetType.getObjectType());
            GenericConverter converter = cachedParsers.get(converterKey);
            if (converter == null) {
                Parser<?> parser = this.annotationFormatterFactory.getParser(
                        converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new ParserConverter(this.fieldType, parser, FormattingConversionService.this);
                cachedParsers.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }

        @Override
        public String toString() {
            return (String.class.getName() + " -> @" + this.annotationType.getName() + " " +
                    this.fieldType.getName() + ": " + this.annotationFormatterFactory);
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
            return this.annotation;
        }

        public Class<?> getFieldType() {
            return this.fieldType;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            AnnotationConverterKey otherKey = (AnnotationConverterKey) other;
            return (this.fieldType == otherKey.fieldType && this.annotation.equals(otherKey.annotation));
        }

        @Override
        public int hashCode() {
            return (this.fieldType.hashCode() * 29 + this.annotation.hashCode());
        }
    }

}
