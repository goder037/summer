package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StopWatch;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * Logs application information on startup.
 *
 * @author Phillip Webb
 * @author Dave Syer
 */
class StartupInfoLogger {

    private final Class<?> sourceClass;

    StartupInfoLogger(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }

    public void logStarting(Log log) {
        Assert.notNull(log, "Log must not be null");
        if (log.isInfoEnabled()) {
            log.info(getStartupMessage());
        }
        if (log.isDebugEnabled()) {
            log.debug(getRunningMessage());
        }
    }

    public void logStarted(Log log, StopWatch stopWatch) {
        if (log.isInfoEnabled()) {
            log.info(getStartedMessage(stopWatch));
        }
    }

    private String getStartupMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Starting ");
        message.append(getApplicationName());
        message.append(getVersion(this.sourceClass));
        message.append(getOn());
        message.append(getPid());
        message.append(getContext());
        return message.toString();
    }

    private StringBuilder getRunningMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Running with Spring Boot");
        message.append(getVersion(getClass()));
        message.append(", Spring");
        message.append(getVersion(ApplicationContext.class));
        return message;
    }

    private StringBuilder getStartedMessage(StopWatch stopWatch) {
        StringBuilder message = new StringBuilder();
        message.append("Started ");
        message.append(getApplicationName());
        message.append(" in ");
        message.append(stopWatch.getTotalTimeSeconds());
        try {
            double uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0;
            message.append(" seconds (JVM running for " + uptime + ")");
        }
        catch (Throwable ex) {
            // No JVM time available
        }
        return message;
    }

    private String getApplicationName() {
        return (this.sourceClass != null) ? ClassUtils.getShortName(this.sourceClass)
                : "application";
    }

    private String getVersion(final Class<?> source) {
        return getValue(" v", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return source.getPackage().getImplementationVersion();
            }
        }, "");
    }

    private String getOn() {
        return getValue(" on ", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return InetAddress.getLocalHost().getHostName();
            }
        });
    }

    private String getPid() {
        return getValue(" with PID ", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return System.getProperty("PID");
            }
        });
    }

    private String getContext() {
        String startedBy = getValue("started by ", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return System.getProperty("user.name");
            }
        });
        String in = getValue("in ", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return System.getProperty("user.dir");
            }
        });
        ApplicationHome home = new ApplicationHome(this.sourceClass);
        String path = (home.getSource() != null) ? home.getSource().getAbsolutePath()
                : "";
        if (startedBy == null && path == null) {
            return "";
        }
        if (StringUtils.hasLength(startedBy) && StringUtils.hasLength(path)) {
            startedBy = " " + startedBy;
        }
        if (StringUtils.hasLength(in) && StringUtils.hasLength(startedBy)) {
            in = " " + in;
        }
        return " (" + path + startedBy + in + ")";
    }

    private String getValue(String prefix, Callable<Object> call) {
        return getValue(prefix, call, "");
    }

    private String getValue(String prefix, Callable<Object> call, String defaultValue) {
        try {
            Object value = call.call();
            if (value != null && StringUtils.hasLength(value.toString())) {
                return prefix + value;
            }
        }
        catch (Exception ex) {
            // Swallow and continue
        }
        return defaultValue;
    }

}

