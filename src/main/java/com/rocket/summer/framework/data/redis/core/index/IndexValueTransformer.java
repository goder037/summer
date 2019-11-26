package com.rocket.summer.framework.data.redis.core.index;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * {@link Converter} implementation that is used to transform values for usage in a particular secondary index.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface IndexValueTransformer extends Converter<Object, Object> {

}

