package com.rocket.summer.framework.boot.logging;

import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

import java.util.*;

/**
 * Common abstraction over logging systems.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Ben Hale
 */
public abstract class LoggingSystem {

    /**
     * A System property that can be used to indicate the {@link LoggingSystem} to use.
     */
    public static final String SYSTEM_PROPERTY = LoggingSystem.class.getName();

    /**
     * The value of the {@link #SYSTEM_PROPERTY} that can be used to indicate that no
     * {@link LoggingSystem} should be used.
     */
    public static final String NONE = "none";

    /**
     * The name used for the root logger. LoggingSystem implementations should ensure that
     * this is the name used to represent the root logger, regardless of the underlying
     * implementation.
     */
    public static final String ROOT_LOGGER_NAME = "ROOT";

    private static final Map<String, String> SYSTEMS;

    static {
        Map<String, String> systems = new LinkedHashMap<String, String>();
        systems.put("ch.qos.logback.core.Appender",
                "com.rocket.summer.framework.boot.logging.logback.LogbackLoggingSystem");
        systems.put("org.apache.logging.log4j.core.impl.Log4jContextFactory",
                "com.rocket.summer.framework.boot.logging.log4j2.Log4J2LoggingSystem");
        systems.put("java.util.logging.LogManager",
                "com.rocket.summer.framework.boot.logging.java.JavaLoggingSystem");
        SYSTEMS = Collections.unmodifiableMap(systems);
    }

    /**
     * Reset the logging system to be limit output. This method may be called before
     * {@link #initialize(LoggingInitializationContext, String, LogFile)} to reduce
     * logging noise until the system has been fully initialized.
     */
    public abstract void beforeInitialize();

    /**
     * Fully initialize the logging system.
     * @param initializationContext the logging initialization context
     * @param configLocation a log configuration location or {@code null} if default
     * initialization is required
     * @param logFile the log output file that should be written or {@code null} for
     * console only output
     */
    public void initialize(LoggingInitializationContext initializationContext,
                           String configLocation, LogFile logFile) {
    }

    /**
     * Clean up the logging system. The default implementation does nothing. Subclasses
     * should override this method to perform any logging system-specific cleanup.
     */
    public void cleanUp() {
    }

    /**
     * Returns a {@link Runnable} that can handle shutdown of this logging system when the
     * JVM exits. The default implementation returns {@code null}, indicating that no
     * shutdown is required.
     * @return the shutdown handler, or {@code null}
     */
    public Runnable getShutdownHandler() {
        return null;
    }

    /**
     * Returns a set of the {@link LogLevel LogLevels} that are actually supported by the
     * logging system.
     * @return the supported levels
     */
    public Set<LogLevel> getSupportedLogLevels() {
        return EnumSet.allOf(LogLevel.class);
    }

    /**
     * Sets the logging level for a given logger.
     * @param loggerName the name of the logger to set ({@code null} can be used for the
     * root logger).
     * @param level the log level
     */
    public void setLogLevel(String loggerName, LogLevel level) {
        throw new UnsupportedOperationException("Unable to set log level");
    }

    /**
     * Returns a collection of the current configuration for all a {@link LoggingSystem}'s
     * loggers.
     * @return the current configurations
     * @since 1.5.0
     */
    public List<LoggerConfiguration> getLoggerConfigurations() {
        throw new UnsupportedOperationException("Unable to get logger configurations");
    }

    /**
     * Returns the current configuration for a {@link LoggingSystem}'s logger.
     * @param loggerName the name of the logger
     * @return the current configuration
     * @since 1.5.0
     */
    public LoggerConfiguration getLoggerConfiguration(String loggerName) {
        throw new UnsupportedOperationException("Unable to get logger configuration");
    }

    /**
     * Detect and return the logging system in use. Supports Logback and Java Logging.
     * @param classLoader the classloader
     * @return the logging system
     */
    public static LoggingSystem get(ClassLoader classLoader) {
        String loggingSystem = System.getProperty(SYSTEM_PROPERTY);
        if (StringUtils.hasLength(loggingSystem)) {
            if (NONE.equals(loggingSystem)) {
                return new NoOpLoggingSystem();
            }
            return get(classLoader, loggingSystem);
        }
        for (Map.Entry<String, String> entry : SYSTEMS.entrySet()) {
            if (ClassUtils.isPresent(entry.getKey(), classLoader)) {
                return get(classLoader, entry.getValue());
            }
        }
        throw new IllegalStateException("No suitable logging system located");
    }

    private static LoggingSystem get(ClassLoader classLoader, String loggingSystemClass) {
        try {
            Class<?> systemClass = ClassUtils.forName(loggingSystemClass, classLoader);
            return (LoggingSystem) systemClass.getConstructor(ClassLoader.class)
                    .newInstance(classLoader);
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@link LoggingSystem} that does nothing.
     */
    static class NoOpLoggingSystem extends LoggingSystem {

        @Override
        public void beforeInitialize() {

        }

        @Override
        public void setLogLevel(String loggerName, LogLevel level) {

        }

        @Override
        public List<LoggerConfiguration> getLoggerConfigurations() {
            return Collections.emptyList();
        }

        @Override
        public LoggerConfiguration getLoggerConfiguration(String loggerName) {
            return null;
        }

    }

}

