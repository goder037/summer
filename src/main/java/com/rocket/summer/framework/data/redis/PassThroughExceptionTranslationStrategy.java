package com.rocket.summer.framework.data.redis;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.dao.DataAccessException;

/**
 * {@link PassThroughExceptionTranslationStrategy} returns {@literal null} for unknown {@link Exception}s.
 *
 * @author Christoph Strobl
 * @since 1.4
 */
public class PassThroughExceptionTranslationStrategy implements ExceptionTranslationStrategy {

    private Converter<Exception, DataAccessException> converter;

    public PassThroughExceptionTranslationStrategy(Converter<Exception, DataAccessException> converter) {
        this.converter = converter;
    }

    @Override
    public DataAccessException translate(Exception e) {
        return this.converter.convert(e);
    }
}

