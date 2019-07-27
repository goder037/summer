package com.rocket.summer.framework.core.env;

import com.rocket.summer.framework.core.convert.support.ConfigurableConversionService;

/**
 * Configuration interface to be implemented by most if not all {@link PropertyResolver
 * PropertyResolver} types. Provides facilities for accessing and customizing the
 * {@link com.rocket.summer.framework.core.convert.ConversionService ConversionService} used when
 * converting property values from one type to another.
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

    /**
     * @return the {@link ConfigurableConversionService} used when performing type
     * conversions on properties.
     * <p>The configurable nature of the returned conversion service allows for
     * the convenient addition and removal of individual {@code Converter} instances:
     * <pre class="code">
     * ConfigurableConversionService cs = env.getConversionService();
     * cs.addConverter(new FooConverter());
     * </pre>
     * @see PropertyResolver#getProperty(String, Class)
     * @see com.rocket.summer.framework.core.convert.converter.ConverterRegistry#addConverter
     */
    ConfigurableConversionService getConversionService();

    /**
     * Set the {@link ConfigurableConversionService} to be used when performing type
     * conversions on properties.
     * <p><strong>Note:</strong> as an alternative to fully replacing the {@code
     * ConversionService}, consider adding or removing individual {@code Converter}
     * instances by drilling into {@link #getConversionService()} and calling methods
     * such as {@code #addConverter}.
     * @see PropertyResolver#getProperty(String, Class)
     * @see #getConversionService()
     * @see com.rocket.summer.framework.core.convert.converter.ConverterRegistry#addConverter
     */
    void setConversionService(ConfigurableConversionService conversionService);

    /**
     * Set the prefix that placeholders replaced by this resolver must begin with.
     */
    void setPlaceholderPrefix(String placeholderPrefix);

    /**
     * Set the suffix that placeholders replaced by this resolver must end with.
     */
    void setPlaceholderSuffix(String placeholderSuffix);

    /**
     * Specify the separating character between the placeholders replaced by this
     * resolver and their associated default value, or {@code null} if no such
     * special character should be processed as a value separator.
     */
    void setValueSeparator(String valueSeparator);

    /**
     * Specify which properties must be present, to be verified by
     * {@link #validateRequiredProperties()}.
     */
    void setRequiredProperties(String... requiredProperties);

    /**
     * Validate that each of the properties specified by
     * {@link #setRequiredProperties} is present and resolves to a
     * non-{@code null} value.
     * @throws MissingRequiredPropertiesException if any of the required
     * properties are not resolvable.
     */
    void validateRequiredProperties() throws MissingRequiredPropertiesException;
}
