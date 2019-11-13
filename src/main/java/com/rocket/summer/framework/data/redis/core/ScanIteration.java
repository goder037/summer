package com.rocket.summer.framework.data.redis.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * {@link ScanIteration} holds the values contained in Redis {@literal Multibulk reply} on exectuting {@literal SCAN}
 * command.
 *
 * @author Christoph Strobl
 * @since 1.4
 */
public class ScanIteration<T> implements Iterable<T> {

    private final long cursorId;
    private final Collection<T> items;

    /**
     * @param cursorId
     * @param items
     */
    public ScanIteration(long cursorId, Collection<T> items) {

        this.cursorId = cursorId;
        this.items = (items != null ? new ArrayList<T>(items) : Collections.<T> emptyList());
    }

    /**
     * The cursor id to be used for subsequent requests.
     *
     * @return
     */
    public long getCursorId() {
        return cursorId;
    }

    /**
     * Get the items returned.
     *
     * @return
     */
    public Collection<T> getItems() {
        return items;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

}

