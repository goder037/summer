package com.rocket.summer.framework.util.concurrent;

import com.rocket.summer.framework.util.Assert;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Abstract class that adapts a {@link Future} parameterized over S into a {@code Future}
 * parameterized over T. All methods are delegated to the adaptee, where {@link #get()}
 * and {@link #get(long, TimeUnit)} call {@link #adapt(Object)} on the adaptee's result.
 *
 * @author Arjen Poutsma
 * @since 4.0
 * @param <T> the type of this {@code Future}
 * @param <S> the type of the adaptee's {@code Future}
 */
public abstract class FutureAdapter<T, S> implements Future<T> {

    private final Future<S> adaptee;

    private Object result = null;

    private State state = State.NEW;

    private final Object mutex = new Object();


    /**
     * Constructs a new {@code FutureAdapter} with the given adaptee.
     * @param adaptee the future to delegate to
     */
    protected FutureAdapter(Future<S> adaptee) {
        Assert.notNull(adaptee, "Delegate must not be null");
        this.adaptee = adaptee;
    }


    /**
     * Returns the adaptee.
     */
    protected Future<S> getAdaptee() {
        return this.adaptee;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.adaptee.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.adaptee.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.adaptee.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return adaptInternal(this.adaptee.get());
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return adaptInternal(this.adaptee.get(timeout, unit));
    }

    @SuppressWarnings("unchecked")
    final T adaptInternal(S adapteeResult) throws ExecutionException {
        synchronized (this.mutex) {
            switch (this.state) {
                case SUCCESS:
                    return (T) this.result;
                case FAILURE:
                    throw (ExecutionException) this.result;
                case NEW:
                    try {
                        T adapted = adapt(adapteeResult);
                        this.result = adapted;
                        this.state = State.SUCCESS;
                        return adapted;
                    }
                    catch (ExecutionException ex) {
                        this.result = ex;
                        this.state = State.FAILURE;
                        throw ex;
                    }
                    catch (Throwable ex) {
                        ExecutionException execEx = new ExecutionException(ex);
                        this.result = execEx;
                        this.state = State.FAILURE;
                        throw execEx;
                    }
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /**
     * Adapts the given adaptee's result into T.
     * @return the adapted result
     */
    protected abstract T adapt(S adapteeResult) throws ExecutionException;


    private enum State {NEW, SUCCESS, FAILURE}

}

