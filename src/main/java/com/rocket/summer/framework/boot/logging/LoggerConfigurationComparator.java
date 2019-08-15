package com.rocket.summer.framework.boot.logging;

import java.util.Comparator;

import com.rocket.summer.framework.util.Assert;

/**
 * An implementation of {@link Comparator} for comparing {@link LoggerConfiguration}s.
 * Sorts the "root" logger as the first logger and then lexically by name after that.
 *
 * @author Ben Hale
 */
class LoggerConfigurationComparator implements Comparator<LoggerConfiguration> {

    private final String rootLoggerName;

    /**
     * Create a new {@link LoggerConfigurationComparator} instance.
     * @param rootLoggerName the name of the "root" logger
     */
    LoggerConfigurationComparator(String rootLoggerName) {
        Assert.notNull(rootLoggerName, "RootLoggerName must not be null");
        this.rootLoggerName = rootLoggerName;
    }

    @Override
    public int compare(LoggerConfiguration o1, LoggerConfiguration o2) {
        if (this.rootLoggerName.equals(o1.getName())) {
            return -1;
        }
        if (this.rootLoggerName.equals(o2.getName())) {
            return 1;
        }
        return o1.getName().compareTo(o2.getName());
    }

}
