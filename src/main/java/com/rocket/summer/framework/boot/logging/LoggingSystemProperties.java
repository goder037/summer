package com.rocket.summer.framework.boot.logging;

import com.rocket.summer.framework.boot.ApplicationPid;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.core.env.Environment;

/**
 * Utility to set system properties that can later be used by log configuration files.
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 */
class LoggingSystemProperties {

    static final String PID_KEY = LoggingApplicationListener.PID_KEY;

    static final String EXCEPTION_CONVERSION_WORD = LoggingApplicationListener.EXCEPTION_CONVERSION_WORD;

    static final String CONSOLE_LOG_PATTERN = LoggingApplicationListener.CONSOLE_LOG_PATTERN;

    static final String FILE_LOG_PATTERN = LoggingApplicationListener.FILE_LOG_PATTERN;

    static final String LOG_LEVEL_PATTERN = LoggingApplicationListener.LOG_LEVEL_PATTERN;

    private final Environment environment;

    LoggingSystemProperties(Environment environment) {
        this.environment = environment;
    }

    public void apply() {
        apply(null);
    }

    public void apply(LogFile logFile) {
        RelaxedPropertyResolver propertyResolver = RelaxedPropertyResolver
                .ignoringUnresolvableNestedPlaceholders(this.environment, "logging.");
        setSystemProperty(propertyResolver, EXCEPTION_CONVERSION_WORD,
                "exception-conversion-word");
        setSystemProperty(PID_KEY, new ApplicationPid().toString());
        setSystemProperty(propertyResolver, CONSOLE_LOG_PATTERN, "pattern.console");
        setSystemProperty(propertyResolver, FILE_LOG_PATTERN, "pattern.file");
        setSystemProperty(propertyResolver, LOG_LEVEL_PATTERN, "pattern.level");
        if (logFile != null) {
            logFile.applyToSystemProperties();
        }
    }

    private void setSystemProperty(RelaxedPropertyResolver propertyResolver,
                                   String systemPropertyName, String propertyName) {
        setSystemProperty(systemPropertyName, propertyResolver.getProperty(propertyName));
    }

    private void setSystemProperty(String name, String value) {
        if (System.getProperty(name) == null && value != null) {
            System.setProperty(name, value);
        }
    }

}

