package com.rocket.summer.framework.data.repository.util;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;


import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter;
import com.rocket.summer.framework.core.convert.support.ConfigurableConversionService;
import com.rocket.summer.framework.data.domain.Page;
import com.rocket.summer.framework.data.domain.Slice;
import com.rocket.summer.framework.data.geo.GeoResults;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.scheduling.annotation.AsyncResult;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import com.rocket.summer.framework.util.concurrent.ListenableFuture;

/**
 * Converters to potentially wrap the execution of a repository method into a variety of wrapper types potentially being
 * available on the classpath. Currently supported:
 * <ul>
 * <li>{@code java.util.Optional}</li>
 * <li>{@code com.google.common.base.Optional}</li>
 * <li>{@code scala.Option} - as of 1.12</li>
 * <li>{@code java.util.concurrent.Future}</li>
 * <li>{@code java.util.concurrent.CompletableFuture}</li>
 * <li>{@code com.rocket.summer.framework.util.concurrent.ListenableFuture<}</li>
 * <li>{@code javaslang.control.Option} - as of 1.13</li>
 * <li>{@code javaslang.collection.Seq}, {@code javaslang.collection.Map}, {@code javaslang.collection.Set} - as of
 * 1.13</li>
 * <li>{@code io.vavr.collection.Seq}, {@code io.vavr.collection.Map}, {@code io.vavr.collection.Set} - as of 2.0</li>
 * </ul>
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Maciek Opała
 * @author Jacek Jackowiak
 * @author Jens Schauder
 * @since 1.8
 */
public abstract class QueryExecutionConverters {

    private static final boolean JDK_8_PRESENT = ClassUtils.isPresent("java.util.Optional",
            QueryExecutionConverters.class.getClassLoader());


    private static final Set<WrapperType> WRAPPER_TYPES = new HashSet<WrapperType>();
    private static final Set<Converter<Object, Object>> UNWRAPPERS = new HashSet<Converter<Object, Object>>();
    private static final Set<Class<?>> ALLOWED_PAGEABLE_TYPES = new HashSet<Class<?>>();

    static {

        WRAPPER_TYPES.add(WrapperType.singleValue(Future.class));
        WRAPPER_TYPES.add(WrapperType.singleValue(ListenableFuture.class));

        ALLOWED_PAGEABLE_TYPES.add(Slice.class);
        ALLOWED_PAGEABLE_TYPES.add(Page.class);
        ALLOWED_PAGEABLE_TYPES.add(List.class);



        if (JDK_8_PRESENT) {
            WRAPPER_TYPES.add(NullableWrapperToJdk8OptionalConverter.getWrapperType());
            UNWRAPPERS.add(Jdk8OptionalUnwrapper.INSTANCE);
        }

        if (JDK_8_PRESENT) {
            WRAPPER_TYPES.add(NullableWrapperToCompletableFutureConverter.getWrapperType());
        }
    }

    private QueryExecutionConverters() {}

    /**
     * Returns whether the given type is a supported wrapper type.
     *
     * @param type must not be {@literal null}.
     * @return
     */
    public static boolean supports(Class<?> type) {

        Assert.notNull(type, "Type must not be null!");

        for (WrapperType candidate : WRAPPER_TYPES) {
            if (candidate.getType().isAssignableFrom(type)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSingleValue(Class<?> type) {

        for (WrapperType candidate : WRAPPER_TYPES) {
            if (candidate.getType().isAssignableFrom(type)) {
                return candidate.isSingleValue();
            }
        }

        return false;
    }

    /**
     * Returns the types that are supported on paginating query methods. Will include custom collection types of e.g.
     * Javaslang.
     *
     * @return
     */
    public static Set<Class<?>> getAllowedPageableTypes() {
        return Collections.unmodifiableSet(ALLOWED_PAGEABLE_TYPES);
    }

    /**
     * Registers converters for wrapper types found on the classpath.
     *
     * @param conversionService must not be {@literal null}.
     */
    public static void registerConvertersIn(ConfigurableConversionService conversionService) {

        Assert.notNull(conversionService, "ConversionService must not be null!");

        conversionService.removeConvertible(Collection.class, Object.class);

        if (JDK_8_PRESENT) {
            conversionService.addConverter(new NullableWrapperToJdk8OptionalConverter(conversionService));
            conversionService.addConverter(new NullableWrapperToCompletableFutureConverter(conversionService));
        }

        conversionService.addConverter(new NullableWrapperToFutureConverter(conversionService));
    }

    /**
     * Unwraps the given source value in case it's one of the currently supported wrapper types detected at runtime.
     *
     * @param source can be {@literal null}.
     * @return
     */
    public static Object unwrap(Object source) {

        if (source == null || !supports(source.getClass())) {
            return source;
        }

        for (Converter<Object, Object> converter : UNWRAPPERS) {

            Object result = converter.convert(source);

            if (result != source) {
                return result;
            }
        }

        return source;
    }

    /**
     * Recursively unwraps well known wrapper types from the given {@link TypeInformation}.
     *
     * @param type must not be {@literal null}.
     * @return will never be {@literal null}.
     */
    public static TypeInformation<?> unwrapWrapperTypes(TypeInformation<?> type) {

        Assert.notNull(type, "type must not be null");

        Class<?> rawType = type.getType();

        boolean needToUnwrap = type.isCollectionLike() //
                || Slice.class.isAssignableFrom(rawType) //
                || GeoResults.class.isAssignableFrom(rawType) //
                || rawType.isArray() //
                || supports(rawType) //
                || com.rocket.summer.framework.data.util.ReflectionUtils.isJava8StreamType(rawType);

        return needToUnwrap ? unwrapWrapperTypes(type.getComponentType()) : type;
    }

    /**
     * Base class for converters that create instances of wrapper types such as Google Guava's and JDK 8's
     * {@code Optional} types.
     *
     * @author Oliver Gierke
     */
    private static abstract class AbstractWrapperTypeConverter implements GenericConverter {

        @SuppressWarnings("unused") //
        private final ConversionService conversionService;
        private final Class<?>[] wrapperTypes;
        private final Object nullValue;

        /**
         * Creates a new {@link AbstractWrapperTypeConverter} using the given {@link ConversionService} and wrapper type.
         *
         * @param conversionService must not be {@literal null}.
         * @param wrapperTypes must not be {@literal null}.
         */
        protected AbstractWrapperTypeConverter(ConversionService conversionService, Object nullValue,
                                               Class<?>... wrapperTypes) {

            Assert.notNull(conversionService, "ConversionService must not be null!");
            Assert.notEmpty(wrapperTypes, "Wrapper type must not be empty!");

            this.conversionService = conversionService;
            this.wrapperTypes = wrapperTypes;
            this.nullValue = nullValue;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.core.convert.converter.GenericConverter#getConvertibleTypes()
         */
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {

            Set<ConvertiblePair> pairs = new HashSet<ConvertiblePair>(wrapperTypes.length);

            for (Class<?> wrapperType : wrapperTypes) {
                pairs.add(new ConvertiblePair(NullableWrapper.class, wrapperType));
            }

            return Collections.unmodifiableSet(pairs);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.core.convert.converter.GenericConverter#convert(java.lang.Object, com.rocket.summer.framework.core.convert.TypeDescriptor, com.rocket.summer.framework.core.convert.TypeDescriptor)
         */
        @Override
        public final Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

            NullableWrapper wrapper = (NullableWrapper) source;
            Object value = wrapper.getValue();

            // TODO: Add Recursive conversion once we move to Spring 4
            return value == null ? nullValue : wrap(value);
        }

        /**
         * Wrap the given, non-{@literal null} value into the wrapper type.
         *
         * @param source will never be {@literal null}.
         * @return must not be {@literal null}.
         */
        protected abstract Object wrap(Object source);
    }

    /**
     * A Spring {@link Converter} to support JDK 8's {@link java.util.Optional}.
     *
     * @author Oliver Gierke
     */
    private static class NullableWrapperToJdk8OptionalConverter extends AbstractWrapperTypeConverter {

        /**
         * Creates a new {@link NullableWrapperToJdk8OptionalConverter} using the given {@link ConversionService}.
         *
         * @param conversionService must not be {@literal null}.
         */
        public NullableWrapperToJdk8OptionalConverter(ConversionService conversionService) {
            super(conversionService, java.util.Optional.empty(), java.util.Optional.class);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.repository.util.QueryExecutionConverters.AbstractWrapperTypeConverter#wrap(java.lang.Object)
         */
        @Override
        protected Object wrap(Object source) {
            return java.util.Optional.of(source);
        }

        public static WrapperType getWrapperType() {
            return WrapperType.singleValue(java.util.Optional.class);
        }
    }

    /**
     * A Spring {@link Converter} to support returning {@link Future} instances from repository methods.
     *
     * @author Oliver Gierke
     */
    private static class NullableWrapperToFutureConverter extends AbstractWrapperTypeConverter {

        /**
         * Creates a new {@link NullableWrapperToFutureConverter} using the given {@link ConversionService}.
         *
         * @param conversionService must not be {@literal null}.
         */
        public NullableWrapperToFutureConverter(ConversionService conversionService) {
            super(conversionService, new AsyncResult<Object>(null), Future.class, ListenableFuture.class);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.repository.util.QueryExecutionConverters.AbstractWrapperTypeConverter#wrap(java.lang.Object)
         */
        @Override
        protected Object wrap(Object source) {
            return new AsyncResult<Object>(source);
        }
    }

    /**
     * A Spring {@link Converter} to support returning {@link CompletableFuture} instances from repository methods.
     *
     * @author Oliver Gierke
     */
    private static class NullableWrapperToCompletableFutureConverter extends AbstractWrapperTypeConverter {

        /**
         * Creates a new {@link NullableWrapperToCompletableFutureConverter} using the given {@link ConversionService}.
         *
         * @param conversionService must not be {@literal null}.
         */
        public NullableWrapperToCompletableFutureConverter(ConversionService conversionService) {
            super(conversionService, CompletableFuture.completedFuture(null), CompletableFuture.class);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.repository.util.QueryExecutionConverters.AbstractWrapperTypeConverter#wrap(java.lang.Object)
         */
        @Override
        protected Object wrap(Object source) {
            return source instanceof CompletableFuture ? source : CompletableFuture.completedFuture(source);
        }

        public static WrapperType getWrapperType() {
            return WrapperType.singleValue(CompletableFuture.class);
        }
    }

    /**
     * A {@link Converter} to unwrap JDK 8 {@link java.util.Optional} instances.
     *
     * @author Oliver Gierke
     * @since 1.12
     */
    private enum Jdk8OptionalUnwrapper implements Converter<Object, Object> {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.core.convert.converter.Converter#convert(java.lang.Object)
         */
        @Override
        public Object convert(Object source) {
            return source instanceof java.util.Optional ? ((java.util.Optional<?>) source).orElse(null) : source;
        }
    }

    @Value
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WrapperType {

        Class<?> type;
        boolean singleValue;

        public static WrapperType singleValue(Class<?> type) {
            return new WrapperType(type, true);
        }

        public static WrapperType multiValue(Class<?> type) {
            return new WrapperType(type, false);
        }
    }
}
