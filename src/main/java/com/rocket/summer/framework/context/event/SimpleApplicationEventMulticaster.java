package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.task.SyncTaskExecutor;
import com.rocket.summer.framework.core.task.TaskExecutor;

import java.util.Iterator;

/**
 * Simple implementation of the {@link ApplicationEventMulticaster} interface.
 *
 * <p>Multicasts all events to all registered listeners, leaving it up to
 * the listeners to ignore events that they are not interested in.
 * Listeners will usually perform corresponding <code>instanceof</code>
 * checks on the passed-in event object.
 *
 * <p>By default, all listeners are invoked in the calling thread.
 * This allows the danger of a rogue listener blocking the entire application,
 * but adds minimal overhead. Specify an alternative TaskExecutor to have
 * listeners executed in different threads, for example from a thread pool.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setTaskExecutor
 * @see #setConcurrentUpdates
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    private TaskExecutor taskExecutor = new SyncTaskExecutor();


    /**
     * Set the TaskExecutor to execute application listeners with.
     * <p>Default is a SyncTaskExecutor, executing the listeners synchronously
     * in the calling thread.
     * <p>Consider specifying an asynchronous TaskExecutor here to not block the
     * caller until all listeners have been executed. However, note that asynchronous
     * execution will not participate in the caller's thread context (class loader,
     * transaction association) unless the TaskExecutor explicitly supports this.
     * @see org.springframework.core.task.SyncTaskExecutor
     * @see org.springframework.core.task.SimpleAsyncTaskExecutor
     * @see org.springframework.scheduling.timer.TimerTaskExecutor
     */
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = (taskExecutor != null ? taskExecutor : new SyncTaskExecutor());
    }

    /**
     * Return the current TaskExecutor for this multicaster.
     */
    protected TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }


    public void multicastEvent(final ApplicationEvent event) {
        for (Iterator it = getApplicationListeners().iterator(); it.hasNext();) {
            final ApplicationListener listener = (ApplicationListener) it.next();
            getTaskExecutor().execute(new Runnable() {
                public void run() {
                    listener.onApplicationEvent(event);
                }
            });
        }
    }

}

