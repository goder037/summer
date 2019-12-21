package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.converter.ConverterRegistry;

/**
 * Configuration interface to be implemented by most if not all {@link ConversionService}
 * types. Consolidates the read-only operations exposed by {@link ConversionService} and
 * the mutating operations of {@link ConverterRegistry} to allow for convenient ad-hoc
 * addition and removal of {@link com.rocket.summer.framework.core.convert.converter.Converter
 * Converters} through. The latter is particularly useful when working against a
 * {@link com.rocket.summer.framework.core.env.ConfigurableEnvironment ConfigurableEnvironment}
 * instance in application context bootstrapping code.
 *
 * @author Chris Beams
 * @since 3.1
 * @see com.rocket.summer.framework.core.env.ConfigurablePropertyResolver#getConversionService()
 * @see com.rocket.summer.framework.core.env.ConfigurableEnvironment
 * @see com.rocket.summer.framework.context.ConfigurableApplicationContext#getEnvironment()
 */
public interface ConfigurableConversionService extends ConversionService, ConverterRegistry {

}

