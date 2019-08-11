package com.rocket.summer.framework.boot.logging;

import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import com.rocket.summer.framework.boot.context.event.ApplicationFailedEvent;
import com.rocket.summer.framework.boot.context.event.ApplicationPreparedEvent;
import com.rocket.summer.framework.boot.context.event.ApplicationStartingEvent;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.event.ApplicationEvent;
import com.rocket.summer.framework.context.event.ContextClosedEvent;
import com.rocket.summer.framework.context.event.GenericApplicationListener;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.util.ResourceUtils;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An {@link ApplicationListener} that configures the {@link LoggingSystem}. If the
 * environment contains a {@code logging.config} property it will be used to bootstrap the
 * logging system, otherwise a default configuration is used. Regardless, logging levels
 * will be customized if the environment contains {@code logging.level.*} entries.
 * <p>
 * Debug and trace logging for Spring, Tomcat, Jetty and Hibernate will be enabled when
 * the environment contains {@code debug} or {@code trace} properties that aren't set to
 * {@code "false"} (i.e. if you start your application using
 * {@literal java -jar myapp.jar [--debug | --trace]}). If you prefer to ignore these
 * properties you can set {@link #setParseArgs(boolean) parseArgs} to {@code false}.
 * <p>
 * By default, log output is only written to the console. If a log file is required the
 * {@code logging.path} and {@code logging.file} properties can be used.
 * <p>
 * Some system properties may be set as side effects, and these can be useful if the
 * logging configuration supports placeholders (i.e. log4j or logback):
 * <ul>
 * <li>{@code LOG_FILE} is set to the value of path of the log file that should be written
 * (if any).</li>
 * <li>{@code PID} is set to the value of the current process ID if it can be determined.
 * </li>
 * </ul>
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @see LoggingSystem#get(ClassLoader)
 */
public class LoggingApplicationListener implements GenericApplicationListener {

    /**
     * The default order for the LoggingApplicationListener.
     */
    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 20;

    /**
     * The name of the Spring property that contains a reference to the logging
     * configuration to load.
     */
    public static final String CONFIG_PROPERTY = "logging.config";

    /**
     * The name of the Spring property that controls the registration of a shutdown hook
     * to shut down the logging system when the JVM exits.
     * @see LoggingSystem#getShutdownHandler
     */
    public static final String REGISTER_SHUTDOWN_HOOK_PROPERTY = "logging.register-shutdown-hook";

    /**
     * The name of the Spring property that contains the directory where log files are
     * written.
     * @deprecated as of 1.5 in favor of {@link LogFile#PATH_PROPERTY}
     */
    @Deprecated
    public static final String PATH_PROPERTY = LogFile.PATH_PROPERTY;

    /**
     * The name of the Spring property that contains the name of the log file. Names can
     * be an exact location or relative to the current directory.
     * @deprecated as of 1.5 in favor of {@link LogFile#FILE_PROPERTY}
     */
    @Deprecated
    public static final String FILE_PROPERTY = LogFile.FILE_PROPERTY;

    /**
     * The name of the System property that contains the process ID.
     */
    public static final String PID_KEY = "PID";

    /**
     * The name of the System property that contains the exception conversion word.
     */
    public static final String EXCEPTION_CONVERSION_WORD = "LOG_EXCEPTION_CONVERSION_WORD";

    /**
     * The name of the System property that contains the log file.
     */
    public static final String LOG_FILE = "LOG_FILE";

    /**
     * The name of the System property that contains the log path.
     */
    public static final String LOG_PATH = "LOG_PATH";

    /**
     * The name of the System property that contains the console log pattern.
     */
    public static final String CONSOLE_LOG_PATTERN = "CONSOLE_LOG_PATTERN";

    /**
     * The name of the System property that contains the file log pattern.
     */
    public static final String FILE_LOG_PATTERN = "FILE_LOG_PATTERN";

    /**
     * The name of the System property that contains the log level pattern.
     */
    public static final String LOG_LEVEL_PATTERN = "LOG_LEVEL_PATTERN";

    /**
     * The name of the {@link LoggingSystem} bean.
     */
    public static final String LOGGING_SYSTEM_BEAN_NAME = "springBootLoggingSystem";

    private static MultiValueMap<LogLevel, String> LOG_LEVEL_LOGGERS;

    private static AtomicBoolean shutdownHookRegistered = new AtomicBoolean(false);

    static {
        LOG_LEVEL_LOGGERS = new LinkedMultiValueMap<LogLevel, String>();
        LOG_LEVEL_LOGGERS.add(LogLevel.DEBUG, "com.rocket.summer.framework.boot");
        LOG_LEVEL_LOGGERS.add(LogLevel.TRACE, "com.rocket.summer.framework");
        LOG_LEVEL_LOGGERS.add(LogLevel.TRACE, "org.apache.tomcat");
        LOG_LEVEL_LOGGERS.add(LogLevel.TRACE, "org.apache.catalina");
        LOG_LEVEL_LOGGERS.add(LogLevel.TRACE, "org.eclipse.jetty");
        LOG_LEVEL_LOGGERS.add(LogLevel.TRACE, "org.hibernate.tool.hbm2ddl");
        LOG_LEVEL_LOGGERS.add(LogLevel.DEBUG, "org.hibernate.SQL");
    }

    private static Class<?>[] EVENT_TYPES = { ApplicationStartingEvent.class,
            ApplicationEnvironmentPreparedEvent.class, ApplicationPreparedEvent.class,
            ContextClosedEvent.class, ApplicationFailedEvent.class };

    private static Class<?>[] SOURCE_TYPES = { SpringApplication.class,
            ApplicationContext.class };

    private final Log logger = LogFactory.getLog(getClass());

    private LoggingSystem loggingSystem;

    private int order = DEFAULT_ORDER;

    private boolean parseArgs = true;

    private LogLevel springBootLogging = null;

    @Override
    public boolean supportsEventType(ResolvableType resolvableType) {
        return isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            for (Class<?> supportedType : supportedTypes) {
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartingEvent) {
            onApplicationStartingEvent((ApplicationStartingEvent) event);
        }
        else if (event instanceof ApplicationEnvironmentPreparedEvent) {
            onApplicationEnvironmentPreparedEvent(
                    (ApplicationEnvironmentPreparedEvent) event);
        }
        else if (event instanceof ApplicationPreparedEvent) {
            onApplicationPreparedEvent((ApplicationPreparedEvent) event);
        }
        else if (event instanceof ContextClosedEvent && ((ContextClosedEvent) event)
                .getApplicationContext().getParent() == null) {
            onContextClosedEvent();
        }
        else if (event instanceof ApplicationFailedEvent) {
            onApplicationFailedEvent();
        }
    }

    private void onApplicationStartingEvent(ApplicationStartingEvent event) {
        this.loggingSystem = LoggingSystem
                .get(event.getSpringApplication().getClassLoader());
        this.loggingSystem.beforeInitialize();
    }

    private void onApplicationEnvironmentPreparedEvent(
            ApplicationEnvironmentPreparedEvent event) {
        if (this.loggingSystem == null) {
            this.loggingSystem = LoggingSystem
                    .get(event.getSpringApplication().getClassLoader());
        }
        initialize(event.getEnvironment(), event.getSpringApplication().getClassLoader());
    }

    private void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
        ConfigurableListableBeanFactory beanFactory = event.getApplicationContext()
                .getBeanFactory();
        if (!beanFactory.containsBean(LOGGING_SYSTEM_BEAN_NAME)) {
            beanFactory.registerSingleton(LOGGING_SYSTEM_BEAN_NAME, this.loggingSystem);
        }
    }

    private void onContextClosedEvent() {
        if (this.loggingSystem != null) {
            this.loggingSystem.cleanUp();
        }
    }

    private void onApplicationFailedEvent() {
        if (this.loggingSystem != null) {
            this.loggingSystem.cleanUp();
        }
    }

    /**
     * Initialize the logging system according to preferences expressed through the
     * {@link Environment} and the classpath.
     * @param environment the environment
     * @param classLoader the classloader
     */
    protected void initialize(ConfigurableEnvironment environment,
                              ClassLoader classLoader) {
        new LoggingSystemProperties(environment).apply();
        LogFile logFile = LogFile.get(environment);
        if (logFile != null) {
            logFile.applyToSystemProperties();
        }
        initializeEarlyLoggingLevel(environment);
        initializeSystem(environment, this.loggingSystem, logFile);
        initializeFinalLoggingLevels(environment, this.loggingSystem);
        registerShutdownHookIfNecessary(environment, this.loggingSystem);
    }

    private void initializeEarlyLoggingLevel(ConfigurableEnvironment environment) {
        if (this.parseArgs && this.springBootLogging == null) {
            if (isSet(environment, "debug")) {
                this.springBootLogging = LogLevel.DEBUG;
            }
            if (isSet(environment, "trace")) {
                this.springBootLogging = LogLevel.TRACE;
            }
        }
    }

    private boolean isSet(ConfigurableEnvironment environment, String property) {
        String value = environment.getProperty(property);
        return (value != null && !value.equals("false"));
    }

    private void initializeSystem(ConfigurableEnvironment environment,
                                  LoggingSystem system, LogFile logFile) {
        LoggingInitializationContext initializationContext = new LoggingInitializationContext(
                environment);
        String logConfig = environment.getProperty(CONFIG_PROPERTY);
        if (ignoreLogConfig(logConfig)) {
            system.initialize(initializationContext, null, logFile);
        }
        else {
            try {
                ResourceUtils.getURL(logConfig).openStream().close();
                system.initialize(initializationContext, logConfig, logFile);
            }
            catch (Exception ex) {
                // NOTE: We can't use the logger here to report the problem
                System.err.println("Logging system failed to initialize "
                        + "using configuration from '" + logConfig + "'");
                ex.printStackTrace(System.err);
                throw new IllegalStateException(ex);
            }
        }
    }

    private boolean ignoreLogConfig(String logConfig) {
        return !StringUtils.hasLength(logConfig) || logConfig.startsWith("-D");
    }

    private void initializeFinalLoggingLevels(ConfigurableEnvironment environment,
                                              LoggingSystem system) {
        if (this.springBootLogging != null) {
            initializeLogLevel(system, this.springBootLogging);
        }
        setLogLevels(system, environment);
    }

    protected void initializeLogLevel(LoggingSystem system, LogLevel level) {
        List<String> loggers = LOG_LEVEL_LOGGERS.get(level);
        if (loggers != null) {
            for (String logger : loggers) {
                system.setLogLevel(logger, level);
            }
        }
    }

    protected void setLogLevels(LoggingSystem system, Environment environment) {
        Map<String, Object> levels = new RelaxedPropertyResolver(environment)
                .getSubProperties("logging.level.");
        boolean rootProcessed = false;
        for (Map.Entry<String, Object> entry : levels.entrySet()) {
            String name = entry.getKey();
            if (name.equalsIgnoreCase(LoggingSystem.ROOT_LOGGER_NAME)) {
                if (rootProcessed) {
                    continue;
                }
                name = null;
                rootProcessed = true;
            }
            setLogLevel(system, environment, name, entry.getValue().toString());
        }
    }

    private void setLogLevel(LoggingSystem system, Environment environment, String name,
                             String level) {
        try {
            level = environment.resolvePlaceholders(level);
            system.setLogLevel(name, coerceLogLevel(level));
        }
        catch (RuntimeException ex) {
            this.logger.error("Cannot set level: " + level + " for '" + name + "'");
        }
    }

    private LogLevel coerceLogLevel(String level) {
        if ("false".equalsIgnoreCase(level)) {
            return LogLevel.OFF;
        }
        return LogLevel.valueOf(level.toUpperCase(Locale.ENGLISH));
    }

    private void registerShutdownHookIfNecessary(Environment environment,
                                                 LoggingSystem loggingSystem) {
        boolean registerShutdownHook = new RelaxedPropertyResolver(environment)
                .getProperty(REGISTER_SHUTDOWN_HOOK_PROPERTY, Boolean.class, false);
        if (registerShutdownHook) {
            Runnable shutdownHandler = loggingSystem.getShutdownHandler();
            if (shutdownHandler != null
                    && shutdownHookRegistered.compareAndSet(false, true)) {
                registerShutdownHook(new Thread(shutdownHandler));
            }
        }
    }

    void registerShutdownHook(Thread shutdownHook) {
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Sets a custom logging level to be used for Spring Boot and related libraries.
     * @param springBootLogging the logging level
     */
    public void setSpringBootLogging(LogLevel springBootLogging) {
        this.springBootLogging = springBootLogging;
    }

    /**
     * Sets if initialization arguments should be parsed for {@literal --debug} and
     * {@literal --trace} options. Defaults to {@code true}.
     * @param parseArgs if arguments should be parsed
     */
    public void setParseArgs(boolean parseArgs) {
        this.parseArgs = parseArgs;
    }

}
