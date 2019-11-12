package com.rocket.summer.framework.data.redis.core;

import java.io.Closeable;
import java.util.Iterator;

/**
 * @author Christoph Strobl
 * @param <T>
 * @since 1.4
 */
public interface Cursor<T> extends Iterator<T>, Closeable {

    /**
     * Get the reference cursor. <br>
     * <strong>NOTE:</strong> the id might change while iterating items.
     *
     * @return
     */
    long getCursorId();

    /**
     * @return Returns true if cursor closed.
     */
    boolean isClosed();

    /**
     * Opens cursor and returns itself.
     *
     * @return
     */
    Cursor<T> open();

    /**
     * @return Returns the current position of the cursor.
     */
    long getPosition();

}

