package com.rocket.summer.framework.boot.diagnostics;

/**
 * A {@code FailureAnalyzer} is used to analyze a failure and provide diagnostic
 * information that can be displayed to the user.
 *
 * @author Andy Wilkinson
 * @since 1.4.0
 */
public interface FailureAnalyzer {

    /**
     * Returns an analysis of the given {@code failure}, or {@code null} if no analysis
     * was possible.
     * @param failure the failure
     * @return the analysis or {@code null}
     */
    FailureAnalysis analyze(Throwable failure);

}

