package com.rocket.summer.framework.data.redis.connection;

import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.dao.InvalidDataAccessResourceUsageException;

/**
 * Exception thrown when executing/closing a pipeline that contains one or multiple invalid/incorrect statements. The
 * exception might also contain the pipeline result (if the driver returns it), allowing for analysis and tracing.
 * <p>
 * Typically, the first exception returned by the pipeline is used as the <i>cause</i> of this exception for easier
 * debugging.
 *
 * @author Costin Leau
 */
public class RedisPipelineException extends InvalidDataAccessResourceUsageException {

    private final List<Object> results;

    /**
     * Constructs a new <code>RedisPipelineException</code> instance.
     *
     * @param msg the message
     * @param cause the cause
     * @param pipelineResult the pipeline result
     */
    public RedisPipelineException(String msg, Throwable cause, List<Object> pipelineResult) {
        super(msg, cause);
        results = Collections.unmodifiableList(pipelineResult);
    }

    /**
     * Constructs a new <code>RedisPipelineException</code> instance using a default message.
     *
     * @param cause the cause
     * @param pipelineResult the pipeline result
     */
    public RedisPipelineException(Exception cause, List<Object> pipelineResult) {
        this("Pipeline contained one or more invalid commands", cause, pipelineResult);
    }

    /**
     * Constructs a new <code>RedisPipelineException</code> instance using a default message and an empty pipeline result
     * list.
     *
     * @param cause the cause
     */
    public RedisPipelineException(Exception cause) {
        this("Pipeline contained one or more invalid commands", cause, Collections.emptyList());
    }

    /**
     * Constructs a new <code>RedisPipelineException</code> instance.
     *
     * @param msg message
     * @param pipelineResult pipeline partial results
     */
    public RedisPipelineException(String msg, List<Object> pipelineResult) {
        super(msg);
        results = Collections.unmodifiableList(pipelineResult);
    }

    /**
     * Optionally returns the result of the pipeline that caused the exception. Typically contains both the results of the
     * successful statements but also the exceptions of the incorrect ones.
     *
     * @return result of the pipeline
     */
    public List<Object> getPipelineResult() {
        return results;
    }
}

