package com.rocket.summer.framework.core.convert.support;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.util.StringUtils;

import java.util.UUID;

/**
 * Converts from a String to a {@link java.util.UUID}.
 *
 * @author Phillip Webb
 * @since 3.2
 * @see UUID#fromString
 */
final class StringToUUIDConverter implements Converter<String, UUID> {

    @Override
    public UUID convert(String source) {
        return (StringUtils.hasLength(source) ? UUID.fromString(source.trim()) : null);
    }

}
