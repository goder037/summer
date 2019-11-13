package com.rocket.summer.framework.data.redis;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.dao.DataAccessException;

/**
 * {@link FallbackExceptionTranslationStrategy} returns {@link RedisSystemException} for unknown {@link Exception}s.
 *
 * @author Christoph Strobl
 * @author Thomas Darimont
 * @since 1.4
 */
public class FallbackExceptionTranslationStrategy extends PassThroughExceptionTranslationStrategy {

    public FallbackExceptionTranslationStrategy(Converter<Exception, DataAccessException> converter) {
        super(converter);
    }

    @Override
    public DataAccessException translate(Exception e) {

        DataAccessException translated = super.translate(e);
        return translated != null ? translated : getFallback(e);
    }

    /**
     * Returns a new {@link RedisSystemException} wrapping the given {@link Exception}.
     *
     * @param e
     * @return
     */
    protected RedisSystemException getFallback(Exception e) {
        return new RedisSystemException("Unknown redis exception", e);
    }
}

