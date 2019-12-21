package com.rocket.summer.framework.scheduling.config;

import com.rocket.summer.framework.scheduling.Trigger;

/**
 * {@link Task} implementation defining a {@code Runnable} to be executed
 * according to a given {@link Trigger}.
 *
 * @author Chris Beams
 * @since 3.2
 * @see Trigger#nextExecutionTime(com.rocket.summer.framework.scheduling.TriggerContext)
 * @see ScheduledTaskRegistrar#setTriggerTasksList(java.util.List)
 * @see com.rocket.summer.framework.scheduling.TaskScheduler#schedule(Runnable, Trigger)
 */
public class TriggerTask extends Task {

    private final Trigger trigger;


    /**
     * Create a new {@link TriggerTask}.
     * @param runnable the underlying task to execute
     * @param trigger specifies when the task should be executed
     */
    public TriggerTask(Runnable runnable, Trigger trigger) {
        super(runnable);
        this.trigger = trigger;
    }


    public Trigger getTrigger() {
        return this.trigger;
    }

}

