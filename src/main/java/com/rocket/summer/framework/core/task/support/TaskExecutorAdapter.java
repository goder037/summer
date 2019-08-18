package com.rocket.summer.framework.core.task.support;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;

import com.rocket.summer.framework.core.task.AsyncListenableTaskExecutor;
import com.rocket.summer.framework.core.task.TaskDecorator;
import com.rocket.summer.framework.core.task.TaskRejectedException;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.concurrent.ListenableFuture;
import com.rocket.summer.framework.util.concurrent.ListenableFutureTask;

/**
 * Adapter that takes a JDK {@code java.util.concurrent.Executor} and
 * exposes a Spring {@link com.rocket.summer.framework.core.task.TaskExecutor} for it.
 * Also detects an extended {@code java.util.concurrent.ExecutorService}, adapting
 * the {@link com.rocket.summer.framework.core.task.AsyncTaskExecutor} interface accordingly.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.util.concurrent.Executor
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.Executors
 */
public class TaskExecutorAdapter implements AsyncListenableTaskExecutor {

    private final Executor concurrentExecutor;

    private TaskDecorator taskDecorator;


    /**
     * Create a new TaskExecutorAdapter,
     * using the given JDK concurrent executor.
     * @param concurrentExecutor the JDK concurrent executor to delegate to
     */
    public TaskExecutorAdapter(Executor concurrentExecutor) {
        Assert.notNull(concurrentExecutor, "Executor must not be null");
        this.concurrentExecutor = concurrentExecutor;
    }


    /**
     * Specify a custom {@link TaskDecorator} to be applied to any {@link Runnable}
     * about to be executed.
     * <p>Note that such a decorator is not necessarily being applied to the
     * user-supplied {@code Runnable}/{@code Callable} but rather to the actual
     * execution callback (which may be a wrapper around the user-supplied task).
     * <p>The primary use case is to set some execution context around the task's
     * invocation, or to provide some monitoring/statistics for task execution.
     * @since 4.3
     */
    public final void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }


    /**
     * Delegates to the specified JDK concurrent executor.
     * @see java.util.concurrent.Executor#execute(Runnable)
     */
    @Override
    public void execute(Runnable task) {
        try {
            doExecute(this.concurrentExecutor, this.taskDecorator, task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(
                    "Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        execute(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        try {
            if (this.taskDecorator == null && this.concurrentExecutor instanceof ExecutorService) {
                return ((ExecutorService) this.concurrentExecutor).submit(task);
            }
            else {
                FutureTask<Object> future = new FutureTask<Object>(task, null);
                doExecute(this.concurrentExecutor, this.taskDecorator, future);
                return future;
            }
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(
                    "Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        try {
            if (this.taskDecorator == null && this.concurrentExecutor instanceof ExecutorService) {
                return ((ExecutorService) this.concurrentExecutor).submit(task);
            }
            else {
                FutureTask<T> future = new FutureTask<T>(task);
                doExecute(this.concurrentExecutor, this.taskDecorator, future);
                return future;
            }
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(
                    "Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        try {
            ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
            doExecute(this.concurrentExecutor, this.taskDecorator, future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(
                    "Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        try {
            ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
            doExecute(this.concurrentExecutor, this.taskDecorator, future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(
                    "Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }


    /**
     * Actually execute the given {@code Runnable} (which may be a user-supplied task
     * or a wrapper around a user-supplied task) with the given executor.
     * @param concurrentExecutor the underlying JDK concurrent executor to delegate to
     * @param taskDecorator the specified decorator to be applied, if any
     * @param runnable the runnable to execute
     * @throws RejectedExecutionException if the given runnable cannot be accepted
     * @since 4.3
     */
    protected void doExecute(Executor concurrentExecutor, TaskDecorator taskDecorator, Runnable runnable)
            throws RejectedExecutionException{

        concurrentExecutor.execute(taskDecorator != null ? taskDecorator.decorate(runnable) : runnable);
    }

}

