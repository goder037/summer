package com.rocket.summer.framework.boot;

/**
 * Strategy interface that can be used to provide a mapping between exceptions and exit
 * codes.
 *
 * @author Phillip Webb
 * @since 1.3.2
 */
public interface ExitCodeExceptionMapper {

    /**
     * Returns the exit code that should be returned from the application.
     * @param exception the exception causing the application to exit
     * @return the exit code or {@code 0}.
     */
    int getExitCode(Throwable exception);

}
