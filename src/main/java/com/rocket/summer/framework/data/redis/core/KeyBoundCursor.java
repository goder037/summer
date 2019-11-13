package com.rocket.summer.framework.data.redis.core;

/**
 * @author Christoph Strobl
 * @param <T>
 * @since 1.4
 */
public abstract class KeyBoundCursor<T> extends ScanCursor<T> {

    private byte[] key;

    /**
     * Crates new {@link ScanCursor}
     *
     * @param cursorId
     * @param options Defaulted to {@link ScanOptions#NONE} if nulled.
     */
    public KeyBoundCursor(byte[] key, long cursorId, ScanOptions options) {
        super(cursorId, options != null ? options : ScanOptions.NONE);
        this.key = key;
    }

    protected ScanIteration<T> doScan(long cursorId, ScanOptions options) {
        return doScan(this.key, cursorId, options);
    }

    protected abstract ScanIteration<T> doScan(byte[] key, long cursorId, ScanOptions options);

    public byte[] getKey() {
        return key;
    }

}
