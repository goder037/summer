package com.rocket.summer.framework.boot;

/**
 * Interface used to generate an 'exit code' from a running command line
 * {@link SpringApplication}. Can be used on exceptions as well as directly on beans.
 *
 * @author Dave Syer
 * @see SpringApplication#exit(com.rocket.summer.framework.context.ApplicationContext,
 * ExitCodeGenerator...)
 */
public interface ExitCodeGenerator {

    /**
     * Returns the exit code that should be returned from the application.
     * @return the exit code.
     */
    int getExitCode();

}
