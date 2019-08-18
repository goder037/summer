package com.rocket.summer.framework.core.task;

/**
 * Exception thrown when a {@link AsyncTaskExecutor} rejects to accept
 * a given task for execution because of the specified timeout.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see AsyncTaskExecutor#execute(Runnable, long)
 * @see TaskRejectedException
 */
@SuppressWarnings("serial")
public class TaskTimeoutException extends TaskRejectedException {

    /**
     * Create a new {@code TaskTimeoutException}
     * with the specified detail message and no root cause.
     * @param msg the detail message
     */
    public TaskTimeoutException(String msg) {
        super(msg);
    }

    /**
     * Create a new {@code TaskTimeoutException}
     * with the specified detail message and the given root cause.
     * @param msg the detail message
     * @param cause the root cause (usually from using an underlying
     * API such as the {@code java.util.concurrent} package)
     * @see java.util.concurrent.RejectedExecutionException
     */
    public TaskTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
