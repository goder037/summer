package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;

/**
 * Exception thrown when a bean definition reader encounters an error
 * during the parsing process.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 */
public class BeanDefinitionParsingException extends BeanDefinitionStoreException {

    /**
     * Create a new BeanDefinitionParsingException.
     * @param problem the configuration problem that was detected during the parsing process
     */
    public BeanDefinitionParsingException(Problem problem) {
        super(problem.getResourceDescription(), problem.toString(), problem.getRootCause());
    }

}

