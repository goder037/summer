package com.rocket.summer.framework.beans.factory.parsing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple {@link ProblemReporter} implementation that exhibits fail-fast
 * behavior when errors are encountered.
 *
 * <p>The first error encountered results in a {@link BeanDefinitionParsingException}
 * being thrown.
 *
 * <p>Warnings are written to
 * {@link #setLogger(org.apache.commons.logging.Log) the log} for this class.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 */
public class FailFastProblemReporter implements ProblemReporter {

    private Log logger = LogFactory.getLog(getClass());


    /**
     * Set the {@link Log logger} that is to be used to report warnings.
     * <p>If set to <code>null</code> then a default {@link Log logger} set to
     * the name of the instance class will be used.
     * @param logger the {@link Log logger} that is to be used to report warnings
     */
    public void setLogger(Log logger) {
        this.logger = (logger != null ? logger : LogFactory.getLog(getClass()));
    }


    /**
     * Throws a {@link BeanDefinitionParsingException} detailing the error
     * that has occurred.
     * @param problem the source of the error
     */
    public void fatal(Problem problem) {
        throw new BeanDefinitionParsingException(problem);
    }

    /**
     * Throws a {@link BeanDefinitionParsingException} detailing the error
     * that has occurred.
     * @param problem the source of the error
     */
    public void error(Problem problem) {
        throw new BeanDefinitionParsingException(problem);
    }

    /**
     * Writes the supplied {@link Problem} to the {@link Log} at <code>WARN</code> level.
     * @param problem the source of the warning
     */
    public void warning(Problem problem) {
        this.logger.warn(problem, problem.getRootCause());
    }

}
