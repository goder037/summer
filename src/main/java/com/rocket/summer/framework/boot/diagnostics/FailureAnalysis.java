package com.rocket.summer.framework.boot.diagnostics;

/**
 * The result of analyzing a failure.
 *
 * @author Andy Wilkinson
 * @since 1.4.0
 */
public class FailureAnalysis {

    private final String description;

    private final String action;

    private final Throwable cause;

    /**
     * Creates a new {@code FailureAnalysis} with the given {@code description} and
     * {@code action}, if any, that the user should take to address the problem. The
     * failure had the given underlying {@code cause}.
     * @param description the description
     * @param action the action
     * @param cause the cause
     */
    public FailureAnalysis(String description, String action, Throwable cause) {
        this.description = description;
        this.action = action;
        this.cause = cause;
    }

    /**
     * Returns a description of the failure.
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the action, if any, to be taken to address the failure.
     * @return the action or {@code null}
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Returns the cause of the failure.
     * @return the cause
     */
    public Throwable getCause() {
        return this.cause;
    }

}

