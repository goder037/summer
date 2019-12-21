package com.rocket.summer.framework.boot.diagnostics;

/**
 * Reports a {@code FailureAnalysis} to the user.
 *
 * @author Andy Wilkinson
 * @since 1.4.0
 */
public interface FailureAnalysisReporter {

    /**
     * Reports the given {@code failureAnalysis} to the user.
     * @param analysis the analysis
     */
    void report(FailureAnalysis analysis);

}
