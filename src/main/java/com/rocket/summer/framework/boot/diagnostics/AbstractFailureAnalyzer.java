package com.rocket.summer.framework.boot.diagnostics;

import com.rocket.summer.framework.core.ResolvableType;

/**
 * Abstract base class for most {@code FailureAnalyzer} implementations.
 *
 * @param <T> the type of exception to analyze
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @since 1.4.0
 */
public abstract class AbstractFailureAnalyzer<T extends Throwable>
        implements FailureAnalyzer {

    @Override
    public FailureAnalysis analyze(Throwable failure) {
        T cause = findCause(failure, getCauseType());
        if (cause != null) {
            return analyze(failure, cause);
        }
        return null;
    }

    /**
     * Returns an analysis of the given {@code failure}, or {@code null} if no analysis
     * was possible.
     * @param rootFailure the root failure passed to the analyzer
     * @param cause the actual found cause
     * @return the analysis or {@code null}
     */
    protected abstract FailureAnalysis analyze(Throwable rootFailure, T cause);

    /**
     * Return the cause type being handled by the analyzer. By default the class generic
     * is used.
     * @return the cause type
     */
    protected Class<? extends T> getCauseType() {
        return (Class<? extends T>) ResolvableType
                .forClass(AbstractFailureAnalyzer.class, getClass()).resolveGeneric();
    }

    protected final <E extends Throwable> T findCause(Throwable failure, Class<E> type) {
        while (failure != null) {
            if (type.isInstance(failure)) {
                return (T) failure;
            }
            failure = failure.getCause();
        }
        return null;
    }

}

