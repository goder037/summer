package com.rocket.summer.framework.core.task;

import com.rocket.summer.framework.util.Assert;

import java.io.Serializable;

/**
 * <code>TaskExecutor</code> implementation that executes each task
 * <i>synchronously</i> in the calling thread.
 *
 * <p>Mainly intended for testing scenarios.
 *
 * <p>Execution in the calling thread does have the advantage of participating
 * in it's thread context, for example the thread context class loader or the
 * thread's current transaction association. That said, in many cases,
 * asynchronous execution will be preferable: choose an asynchronous
 * <code>TaskExecutor</code> instead for such scenarios.
 *
 * @author Juergen Hoeller
 * @see SimpleAsyncTaskExecutor
 * @see org.springframework.scheduling.timer.TimerTaskExecutor
 * @since 2.0
 */
public class SyncTaskExecutor implements TaskExecutor, Serializable {

    /**
     * Executes the given <code>task</code> synchronously, through direct
     * invocation of it's {@link Runnable#run() run()} method.
     * @throws IllegalArgumentException if the given <code>task</code> is <code>null</code>
     */
    public void execute(Runnable task) {
        Assert.notNull(task, "Runnable must not be null");
        task.run();
    }

}
