package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.core.env.Environment;

import java.io.PrintStream;

/**
 * Interface class for writing a banner programmatically.
 *
 * @author Phillip Webb
 * @author Michael Stummvoll
 * @author Jeremy Rickard
 * @since 1.2.0
 */
public interface Banner {

    /**
     * Print the banner to the specified print stream.
     * @param environment the spring environment
     * @param sourceClass the source class for the application
     * @param out the output print stream
     */
    void printBanner(Environment environment, Class<?> sourceClass, PrintStream out);

    /**
     * An enumeration of possible values for configuring the Banner.
     */
    enum Mode {

        /**
         * Disable printing of the banner.
         */
        OFF,

        /**
         * Print the banner to System.out.
         */
        CONSOLE,

        /**
         * Print the banner to the log file.
         */
        LOG

    }

}

