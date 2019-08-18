package com.rocket.summer.framework.cache.interceptor;

/**
 * Abstract the invocation of a cache operation.
 *
 * <p>Does not provide a way to transmit checked exceptions but
 * provide a special exception that should be used to wrap any
 * exception that was thrown by the underlying invocation.
 * Callers are expected to handle this issue type specifically.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public interface CacheOperationInvoker {

    /**
     * Invoke the cache operation defined by this instance. Wraps any exception
     * that is thrown during the invocation in a {@link ThrowableWrapper}.
     *
     * @return the result of the operation
     * @throws ThrowableWrapper if an error occurred while invoking the operation
     */
    Object invoke() throws ThrowableWrapper;


    /**
     * Wrap any exception thrown while invoking {@link #invoke()}.
     */
    @SuppressWarnings("serial")
    class ThrowableWrapper extends RuntimeException {

        private final Throwable original;

        public ThrowableWrapper(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }

}
