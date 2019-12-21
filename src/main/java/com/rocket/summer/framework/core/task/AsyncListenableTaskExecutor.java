package com.rocket.summer.framework.core.task;

import com.rocket.summer.framework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;

/**
 * Extension of the {@link AsyncTaskExecutor} interface, adding the capability to submit
 * tasks for {@link ListenableFuture}s.
 *
 * @author Arjen Poutsma
 * @since 4.0
 * @see ListenableFuture
 */
public interface AsyncListenableTaskExecutor extends AsyncTaskExecutor {

    /**
     * Submit a {@code Runnable} task for execution, receiving a {@code ListenableFuture}
     * representing that task. The Future will return a {@code null} result upon completion.
     * @param task the {@code Runnable} to execute (never {@code null})
     * @return a {@code ListenableFuture} representing pending completion of the task
     * @throws TaskRejectedException if the given task was not accepted
     */
    ListenableFuture<?> submitListenable(Runnable task);

    /**
     * Submit a {@code Callable} task for execution, receiving a {@code ListenableFuture}
     * representing that task. The Future will return the Callable's result upon
     * completion.
     * @param task the {@code Callable} to execute (never {@code null})
     * @return a {@code ListenableFuture} representing pending completion of the task
     * @throws TaskRejectedException if the given task was not accepted
     */
    <T> ListenableFuture<T> submitListenable(Callable<T> task);

}
