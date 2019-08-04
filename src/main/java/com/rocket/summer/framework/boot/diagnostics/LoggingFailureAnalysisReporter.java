package com.rocket.summer.framework.boot.diagnostics;

import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link FailureAnalysisReporter} that logs the failure analysis.
 *
 * @author Andy Wilkinson
 * @since 1.4.0
 */
public final class LoggingFailureAnalysisReporter implements FailureAnalysisReporter {

    private static final Log logger = LogFactory
            .getLog(LoggingFailureAnalysisReporter.class);

    @Override
    public void report(FailureAnalysis failureAnalysis) {
        if (logger.isDebugEnabled()) {
            logger.debug("Application failed to start due to an exception",
                    failureAnalysis.getCause());
        }
        if (logger.isErrorEnabled()) {
            logger.error(buildMessage(failureAnalysis));
        }
    }

    private String buildMessage(FailureAnalysis failureAnalysis) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%n%n"));
        builder.append(String.format("***************************%n"));
        builder.append(String.format("APPLICATION FAILED TO START%n"));
        builder.append(String.format("***************************%n%n"));
        builder.append(String.format("Description:%n%n"));
        builder.append(String.format("%s%n", failureAnalysis.getDescription()));
        if (StringUtils.hasText(failureAnalysis.getAction())) {
            builder.append(String.format("%nAction:%n%n"));
            builder.append(String.format("%s%n", failureAnalysis.getAction()));
        }
        return builder.toString();
    }

}

