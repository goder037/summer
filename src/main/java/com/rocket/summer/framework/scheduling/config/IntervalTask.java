package com.rocket.summer.framework.scheduling.config;

/**
 * {@link Task} implementation defining a {@code Runnable} to be executed at a given
 * millisecond interval which may be treated as fixed-rate or fixed-delay depending on
 * context.
 *
 * @author Chris Beams
 * @since 3.2
 * @see com.rocket.summer.framework.scheduling.annotation.Scheduled#fixedRate()
 * @see com.rocket.summer.framework.scheduling.annotation.Scheduled#fixedDelay()
 * @see ScheduledTaskRegistrar#setFixedRateTasksList(java.util.List)
 * @see ScheduledTaskRegistrar#setFixedDelayTasksList(java.util.List)
 * @see com.rocket.summer.framework.scheduling.TaskScheduler
 */
public class IntervalTask extends Task {

    private final long interval;

    private final long initialDelay;


    /**
     * Create a new {@code IntervalTask}.
     * @param runnable the underlying task to execute
     * @param interval how often in milliseconds the task should be executed
     * @param initialDelay initial delay before first execution of the task
     */
    public IntervalTask(Runnable runnable, long interval, long initialDelay) {
        super(runnable);
        this.interval = interval;
        this.initialDelay = initialDelay;
    }

    /**
     * Create a new {@code IntervalTask} with no initial delay.
     * @param runnable the underlying task to execute
     * @param interval how often in milliseconds the task should be executed
     */
    public IntervalTask(Runnable runnable, long interval) {
        this(runnable, interval, 0);
    }


    public long getInterval() {
        return this.interval;
    }

    public long getInitialDelay() {
        return this.initialDelay;
    }

}

