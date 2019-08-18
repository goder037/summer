package com.rocket.summer.framework.scheduling.config;

/**
 * Holder class defining a {@code Runnable} to be executed as a task, typically at a
 * scheduled time or interval. See subclass hierarchy for various scheduling approaches.
 *
 * @author Chris Beams
 * @since 3.2
 */
public class Task {

    private final Runnable runnable;


    /**
     * Create a new {@code Task}.
     * @param runnable the underlying task to execute.
     */
    public Task(Runnable runnable) {
        this.runnable = runnable;
    }


    public Runnable getRunnable() {
        return runnable;
    }
}

