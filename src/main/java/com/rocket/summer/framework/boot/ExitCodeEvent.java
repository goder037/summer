package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.context.event.ApplicationEvent;

/**
 * Event fired when an application exit code has been determined from an
 * {@link ExitCodeGenerator}.
 *
 * @author Phillip Webb
 * @since 1.3.2
 */
public class ExitCodeEvent extends ApplicationEvent {

    private final int exitCode;

    /**
     * Create a new {@link ExitCodeEvent} instance.
     * @param source the source of the event
     * @param exitCode the exit code
     */
    public ExitCodeEvent(Object source, int exitCode) {
        super(source);
        this.exitCode = exitCode;
    }

    /**
     * Return the exit code that will be used to exit the JVM.
     * @return the exit code
     */
    public int getExitCode() {
        return this.exitCode;
    }

}
