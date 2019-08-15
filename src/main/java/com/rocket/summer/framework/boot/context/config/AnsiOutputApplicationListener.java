package com.rocket.summer.framework.boot.context.config;

import java.util.Locale;

import com.rocket.summer.framework.boot.ansi.AnsiOutput;
import com.rocket.summer.framework.boot.ansi.AnsiOutput.Enabled;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.Ordered;

/**
 * An {@link ApplicationListener} that configures {@link AnsiOutput} depending on the
 * value of the property {@code spring.output.ansi.enabled}. See {@link Enabled} for valid
 * values.
 *
 * @author Raphael von der Grün
 * @since 1.2.0
 */
public class AnsiOutputApplicationListener
        implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
                event.getEnvironment(), "spring.output.ansi.");
        if (resolver.containsProperty("enabled")) {
            String enabled = resolver.getProperty("enabled");
            AnsiOutput.setEnabled(
                    Enum.valueOf(Enabled.class, enabled.toUpperCase(Locale.ENGLISH)));
        }

        if (resolver.containsProperty("console-available")) {
            AnsiOutput.setConsoleAvailable(
                    resolver.getProperty("console-available", Boolean.class));
        }
    }

    @Override
    public int getOrder() {
        // Apply after ConfigFileApplicationListener has called all
        // EnvironmentPostProcessors
        return ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    }

}
