package com.rocket.summer.framework.boot.autoconfigure.logging;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionEvaluationReport;
import com.rocket.summer.framework.boot.context.event.ApplicationFailedEvent;
import com.rocket.summer.framework.boot.logging.LogLevel;
import com.rocket.summer.framework.context.ApplicationContextInitializer;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.context.event.ApplicationContextEvent;
import com.rocket.summer.framework.context.event.ApplicationEvent;
import com.rocket.summer.framework.context.event.ContextRefreshedEvent;
import com.rocket.summer.framework.context.event.GenericApplicationListener;
import com.rocket.summer.framework.context.support.GenericApplicationContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.ResolvableType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link ApplicationContextInitializer} that writes the {@link ConditionEvaluationReport}
 * to the log. Reports are logged at the {@link LogLevel#DEBUG DEBUG} level. A crash
 * report triggers an info output suggesting the user runs again with debug enabled to
 * display the report.
 * <p>
 * This initializer is not intended to be shared across multiple application context
 * instances.
 *
 * @author Greg Turnquist
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class AutoConfigurationReportLoggingInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final Log logger = LogFactory.getLog(getClass());

    private ConfigurableApplicationContext applicationContext;

    private ConditionEvaluationReport report;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        applicationContext.addApplicationListener(new AutoConfigurationReportListener());
        if (applicationContext instanceof GenericApplicationContext) {
            // Get the report early in case the context fails to load
            this.report = ConditionEvaluationReport
                    .get(this.applicationContext.getBeanFactory());
        }
    }

    protected void onApplicationEvent(ApplicationEvent event) {
        ConfigurableApplicationContext initializerApplicationContext = AutoConfigurationReportLoggingInitializer.this.applicationContext;
        if (event instanceof ContextRefreshedEvent) {
            if (((ApplicationContextEvent) event)
                    .getApplicationContext() == initializerApplicationContext) {
                logAutoConfigurationReport();
            }
        }
        else if (event instanceof ApplicationFailedEvent) {
            if (((ApplicationFailedEvent) event)
                    .getApplicationContext() == initializerApplicationContext) {
                logAutoConfigurationReport(true);
            }
        }
    }

    private void logAutoConfigurationReport() {
        logAutoConfigurationReport(!this.applicationContext.isActive());
    }

    public void logAutoConfigurationReport(boolean isCrashReport) {
        if (this.report == null) {
            if (this.applicationContext == null) {
                this.logger.info("Unable to provide auto-configuration report "
                        + "due to missing ApplicationContext");
                return;
            }
            this.report = ConditionEvaluationReport
                    .get(this.applicationContext.getBeanFactory());
        }
        if (!this.report.getConditionAndOutcomesBySource().isEmpty()) {
            if (isCrashReport && this.logger.isInfoEnabled()
                    && !this.logger.isDebugEnabled()) {
                this.logger.info(String
                        .format("%n%nError starting ApplicationContext. To display the "
                                + "auto-configuration report re-run your application with "
                                + "'debug' enabled."));
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(new ConditionEvaluationReportMessage(this.report));
            }
        }
    }

    private class AutoConfigurationReportListener implements GenericApplicationListener {

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }

        @Override
        public boolean supportsEventType(ResolvableType resolvableType) {
            Class<?> type = resolvableType.getRawClass();
            if (type == null) {
                return false;
            }
            return ContextRefreshedEvent.class.isAssignableFrom(type)
                    || ApplicationFailedEvent.class.isAssignableFrom(type);
        }

        @Override
        public boolean supportsSourceType(Class<?> sourceType) {
            return true;
        }

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            AutoConfigurationReportLoggingInitializer.this.onApplicationEvent(event);
        }

    }

}
