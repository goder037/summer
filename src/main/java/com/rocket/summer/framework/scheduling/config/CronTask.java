package com.rocket.summer.framework.scheduling.config;

import com.rocket.summer.framework.scheduling.support.CronTrigger;

/**
 * {@link TriggerTask} implementation defining a {@code Runnable} to be executed according
 * to a {@linkplain com.rocket.summer.framework.scheduling.support.CronSequenceGenerator standard
 * cron expression}.
 *
 * @author Chris Beams
 * @since 3.2
 * @see com.rocket.summer.framework.scheduling.annotation.Scheduled#cron()
 * @see ScheduledTaskRegistrar#setCronTasksList(java.util.List)
 * @see com.rocket.summer.framework.scheduling.TaskScheduler
 */
public class CronTask extends TriggerTask {

    private final String expression;


    /**
     * Create a new {@code CronTask}.
     * @param runnable the underlying task to execute
     * @param expression cron expression defining when the task should be executed
     */
    public CronTask(Runnable runnable, String expression) {
        this(runnable, new CronTrigger(expression));
    }

    /**
     * Create a new {@code CronTask}.
     * @param runnable the underlying task to execute
     * @param cronTrigger the cron trigger defining when the task should be executed
     */
    public CronTask(Runnable runnable, CronTrigger cronTrigger) {
        super(runnable, cronTrigger);
        this.expression = cronTrigger.getExpression();
    }


    public String getExpression() {
        return this.expression;
    }

}

