package com.rocket.summer.framework.data.redis.core;

import java.io.IOException;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link ConvertingCursor} wraps a given cursor and applies given {@link Converter} to items prior to returning them.
 * This allows to easily perform required conversion whereas the underlying implementation may still work with its
 * native types.
 *
 * @author Christoph Strobl
 * @param <S>
 * @param <T>
 * @since 1.4
 */
public class ConvertingCursor<S, T> implements Cursor<T> {

    private Cursor<S> delegate;
    private Converter<S, T> converter;

    /**
     * @param cursor Cursor must not be {@literal null}.
     * @param converter Converter must not be {@literal null}.
     */
    public ConvertingCursor(Cursor<S> cursor, Converter<S, T> converter) {

        Assert.notNull(cursor, "Cursor delegate must not be 'null'.");
        Assert.notNull(cursor, "Converter must not be 'null'.");
        this.delegate = cursor;
        this.converter = converter;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
        return converter.convert(delegate.next());
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        delegate.remove();
    }

    /*
     * (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        delegate.close();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.Cursor#getCursorId()
     */
    @Override
    public long getCursorId() {
        return delegate.getCursorId();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.Cursor#isClosed()
     */
    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.Cursor#open()
     */
    @Override
    public Cursor<T> open() {
        this.delegate = delegate.open();
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.Cursor#getPosition()
     */
    @Override
    public long getPosition() {
        return delegate.getPosition();
    }

}

