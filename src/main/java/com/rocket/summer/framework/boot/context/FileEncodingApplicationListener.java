package com.rocket.summer.framework.boot.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.Ordered;

/**
 * An {@link ApplicationListener} that halts application startup if the system file
 * encoding does not match an expected value set in the environment. By default has no
 * effect, but if you set {@code spring.mandatory_file_encoding} (or some camelCase or
 * UPPERCASE variant of that) to the name of a character encoding (e.g. "UTF-8") then this
 * initializer throws an exception when the {@code file.encoding} System property does not
 * equal it.
 *
 * <p>
 * The System property {@code file.encoding} is normally set by the JVM in response to the
 * {@code LANG} or {@code LC_ALL} environment variables. It is used (along with other
 * platform-dependent variables keyed off those environment variables) to encode JVM
 * arguments as well as file names and paths. In most cases you can override the file
 * encoding System property on the command line (with standard JVM features), but also
 * consider setting the {@code LANG} environment variable to an explicit
 * character-encoding value (e.g. "en_GB.UTF-8").
 *
 * @author Dave Syer
 */
public class FileEncodingApplicationListener
        implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    private static final Log logger = LogFactory
            .getLog(FileEncodingApplicationListener.class);

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
                event.getEnvironment(), "spring.");
        if (resolver.containsProperty("mandatoryFileEncoding")) {
            String encoding = System.getProperty("file.encoding");
            String desired = resolver.getProperty("mandatoryFileEncoding");
            if (encoding != null && !desired.equalsIgnoreCase(encoding)) {
                logger.error("System property 'file.encoding' is currently '" + encoding
                        + "'. It should be '" + desired
                        + "' (as defined in 'spring.mandatoryFileEncoding').");
                logger.error("Environment variable LANG is '" + System.getenv("LANG")
                        + "'. You could use a locale setting that matches encoding='"
                        + desired + "'.");
                logger.error("Environment variable LC_ALL is '" + System.getenv("LC_ALL")
                        + "'. You could use a locale setting that matches encoding='"
                        + desired + "'.");
                throw new IllegalStateException(
                        "The Java Virtual Machine has not been configured to use the "
                                + "desired default character encoding (" + desired
                                + ").");
            }
        }
    }

}

