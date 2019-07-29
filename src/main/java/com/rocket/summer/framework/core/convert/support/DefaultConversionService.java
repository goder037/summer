package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.converter.ConverterRegistry;
import com.rocket.summer.framework.util.ClassUtils;

import java.util.Locale;

/**
 * A specialization of {@link GenericConversionService} configured by default with
 * converters appropriate for most environments.
 *
 * <p>Designed for direct instantiation but also exposes the static
 * {@link #addDefaultConverters(ConverterRegistry)} utility method for ad hoc use against any
 * {@code ConverterRegistry} instance.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class DefaultConversionService extends GenericConversionService {

    /** Java 8's java.util.stream.Stream class available? */
    private static final boolean streamAvailable = ClassUtils.isPresent(
            "java.util.stream.Stream", DefaultConversionService.class.getClassLoader());

    /**
     * Create a new {@code DefaultConversionService} with the set of
     * {@linkplain DefaultConversionService#addDefaultConverters(ConverterRegistry) default converters}.
     */
    public DefaultConversionService() {
        addDefaultConverters(this);
    }

    // static utility methods

    /**
     * Add converters appropriate for most environments.
     * @param converterRegistry the registry of converters to add to (must also be castable to ConversionService)
     * @throws ClassCastException if the converterRegistry could not be cast to a ConversionService
     */
    public static void addDefaultConverters(ConverterRegistry converterRegistry) {
        addScalarConverters(converterRegistry);
        addCollectionConverters(converterRegistry);
        addFallbackConverters(converterRegistry);
    }

    // internal helpers

    private static void addScalarConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverter(new StringToBooleanConverter());
        converterRegistry.addConverter(Boolean.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
        converterRegistry.addConverter(Number.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverterFactory(new NumberToNumberConverterFactory());

        converterRegistry.addConverter(new StringToCharacterConverter());
        converterRegistry.addConverter(Character.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverter(new NumberToCharacterConverter());
        converterRegistry.addConverterFactory(new CharacterToNumberFactory());

        converterRegistry.addConverterFactory(new StringToEnumConverterFactory());
        converterRegistry.addConverter(Enum.class, String.class, new EnumToStringConverter());

        converterRegistry.addConverter(new StringToLocaleConverter());
        converterRegistry.addConverter(Locale.class, String.class, new ObjectToStringConverter());

        converterRegistry.addConverter(new PropertiesToStringConverter());
        converterRegistry.addConverter(new StringToPropertiesConverter());
    }

    /**
     * Add common collection converters.
     * @param converterRegistry the registry of converters to add to
     * (must also be castable to ConversionService, e.g. being a {@link ConfigurableConversionService})
     * @throws ClassCastException if the given ConverterRegistry could not be cast to a ConversionService
     * @since 4.2.3
     */
    public static void addCollectionConverters(ConverterRegistry converterRegistry) {
        ConversionService conversionService = (ConversionService) converterRegistry;

        converterRegistry.addConverter(new ArrayToCollectionConverter(conversionService));
        converterRegistry.addConverter(new CollectionToArrayConverter(conversionService));

        converterRegistry.addConverter(new ArrayToArrayConverter(conversionService));
        converterRegistry.addConverter(new CollectionToCollectionConverter(conversionService));
        converterRegistry.addConverter(new MapToMapConverter(conversionService));

        converterRegistry.addConverter(new ArrayToStringConverter(conversionService));
        converterRegistry.addConverter(new StringToArrayConverter(conversionService));

        converterRegistry.addConverter(new ArrayToObjectConverter(conversionService));
        converterRegistry.addConverter(new ObjectToArrayConverter(conversionService));

        converterRegistry.addConverter(new CollectionToStringConverter(conversionService));
        converterRegistry.addConverter(new StringToCollectionConverter(conversionService));

        converterRegistry.addConverter(new CollectionToObjectConverter(conversionService));
        converterRegistry.addConverter(new ObjectToCollectionConverter(conversionService));

        if (streamAvailable) {
            converterRegistry.addConverter(new StreamConverter(conversionService));
        }
    }

    private static void addFallbackConverters(ConverterRegistry converterRegistry) {
        ConversionService conversionService = (ConversionService) converterRegistry;
        converterRegistry.addConverter(new ObjectToObjectConverter());
        converterRegistry.addConverter(new IdToEntityConverter(conversionService));
        converterRegistry.addConverter(new FallbackObjectToStringConverter());
    }

}
