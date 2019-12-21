package com.rocket.summer.framework.boot.logging;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ClassPathResource;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.util.SystemPropertyUtils;

/**
 * Abstract base class for {@link LoggingSystem} implementations.
 *
 * @author Phillip Webb
 * @author Dave Syer
 */
public abstract class AbstractLoggingSystem extends LoggingSystem {

    protected static final Comparator<LoggerConfiguration> CONFIGURATION_COMPARATOR = new LoggerConfigurationComparator(
            ROOT_LOGGER_NAME);

    private final ClassLoader classLoader;

    public AbstractLoggingSystem(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void beforeInitialize() {
    }

    @Override
    public void initialize(LoggingInitializationContext initializationContext,
                           String configLocation, LogFile logFile) {
        if (StringUtils.hasLength(configLocation)) {
            initializeWithSpecificConfig(initializationContext, configLocation, logFile);
            return;
        }
        initializeWithConventions(initializationContext, logFile);
    }

    private void initializeWithSpecificConfig(
            LoggingInitializationContext initializationContext, String configLocation,
            LogFile logFile) {
        configLocation = SystemPropertyUtils.resolvePlaceholders(configLocation);
        loadConfiguration(initializationContext, configLocation, logFile);
    }

    private void initializeWithConventions(
            LoggingInitializationContext initializationContext, LogFile logFile) {
        String config = getSelfInitializationConfig();
        if (config != null && logFile == null) {
            // self initialization has occurred, reinitialize in case of property changes
            reinitialize(initializationContext);
            return;
        }
        if (config == null) {
            config = getSpringInitializationConfig();
        }
        if (config != null) {
            loadConfiguration(initializationContext, config, logFile);
            return;
        }
        loadDefaults(initializationContext, logFile);
    }

    /**
     * Return any self initialization config that has been applied. By default this method
     * checks {@link #getStandardConfigLocations()} and assumes that any file that exists
     * will have been applied.
     * @return the self initialization config or {@code null}
     */
    protected String getSelfInitializationConfig() {
        return findConfig(getStandardConfigLocations());
    }

    /**
     * Return any spring specific initialization config that should be applied. By default
     * this method checks {@link #getSpringConfigLocations()}.
     * @return the spring initialization config or {@code null}
     */
    protected String getSpringInitializationConfig() {
        return findConfig(getSpringConfigLocations());
    }

    private String findConfig(String[] locations) {
        for (String location : locations) {
            ClassPathResource resource = new ClassPathResource(location,
                    this.classLoader);
            if (resource.exists()) {
                return "classpath:" + location;
            }
        }
        return null;
    }

    /**
     * Return the standard config locations for this system.
     * @return the standard config locations
     * @see #getSelfInitializationConfig()
     */
    protected abstract String[] getStandardConfigLocations();

    /**
     * Return the spring config locations for this system. By default this method returns
     * a set of locations based on {@link #getStandardConfigLocations()}.
     * @return the spring config locations
     * @see #getSpringInitializationConfig()
     */
    protected String[] getSpringConfigLocations() {
        String[] locations = getStandardConfigLocations();
        for (int i = 0; i < locations.length; i++) {
            String extension = StringUtils.getFilenameExtension(locations[i]);
            locations[i] = locations[i].substring(0,
                    locations[i].length() - extension.length() - 1) + "-spring."
                    + extension;
        }
        return locations;
    }

    /**
     * Load sensible defaults for the logging system.
     * @param initializationContext the logging initialization context
     * @param logFile the file to load or {@code null} if no log file is to be written
     */
    protected abstract void loadDefaults(
            LoggingInitializationContext initializationContext, LogFile logFile);

    /**
     * Load a specific configuration.
     * @param initializationContext the logging initialization context
     * @param location the location of the configuration to load (never {@code null})
     * @param logFile the file to load or {@code null} if no log file is to be written
     */
    protected abstract void loadConfiguration(
            LoggingInitializationContext initializationContext, String location,
            LogFile logFile);

    /**
     * Reinitialize the logging system if required. Called when
     * {@link #getSelfInitializationConfig()} is used and the log file hasn't changed. May
     * be used to reload configuration (for example to pick up additional System
     * properties).
     * @param initializationContext the logging initialization context
     */
    protected void reinitialize(LoggingInitializationContext initializationContext) {
    }

    protected final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    protected final String getPackagedConfigFile(String fileName) {
        String defaultPath = ClassUtils.getPackageName(getClass());
        defaultPath = defaultPath.replace('.', '/');
        defaultPath = defaultPath + "/" + fileName;
        defaultPath = "classpath:" + defaultPath;
        return defaultPath;
    }

    protected final void applySystemProperties(Environment environment, LogFile logFile) {
        new LoggingSystemProperties(environment).apply(logFile);
    }

    /**
     * Maintains a mapping between native levels and {@link LogLevel}.
     *
     * @param <T> the native level type
     */
    protected static class LogLevels<T> {

        private final Map<LogLevel, T> systemToNative;

        private final Map<T, LogLevel> nativeToSystem;

        public LogLevels() {
            this.systemToNative = new HashMap<LogLevel, T>();
            this.nativeToSystem = new HashMap<T, LogLevel>();
        }

        public void map(LogLevel system, T nativeLevel) {
            if (!this.systemToNative.containsKey(system)) {
                this.systemToNative.put(system, nativeLevel);
            }
            if (!this.nativeToSystem.containsKey(nativeLevel)) {
                this.nativeToSystem.put(nativeLevel, system);
            }
        }

        public LogLevel convertNativeToSystem(T level) {
            return this.nativeToSystem.get(level);
        }

        public T convertSystemToNative(LogLevel level) {
            return this.systemToNative.get(level);
        }

        public Set<LogLevel> getSupported() {
            return new LinkedHashSet<LogLevel>(this.nativeToSystem.values());
        }

    }

}

