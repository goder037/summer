package com.rocket.summer.framework.scheduling.config;

import java.util.concurrent.ScheduledFuture;

/**
 * A representation of a scheduled task,
 * used as a return value for scheduling methods.
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see ScheduledTaskRegistrar#scheduleTriggerTask
 * @see ScheduledTaskRegistrar#scheduleFixedRateTask
 */
public final class ScheduledTask {

    volatile ScheduledFuture<?> future;


    ScheduledTask() {
    }


    /**
     * Trigger cancellation of this scheduled task.
     */
    public void cancel() {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(true);
        }
    }

}
