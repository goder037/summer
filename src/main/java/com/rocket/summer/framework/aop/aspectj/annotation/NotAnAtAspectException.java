package com.rocket.summer.framework.aop.aspectj.annotation;

import com.rocket.summer.framework.aop.framework.AopConfigException;

/**
 * Extension of AopConfigException thrown when trying to perform
 * an advisor generation operation on a class that is not an
 * AspectJ annotation-style aspect.
 *
 * @author Rod Johnson
 * @since 2.0
 */
public class NotAnAtAspectException extends AopConfigException {

    private Class<?> nonAspectClass;


    /**
     * Create a new NotAnAtAspectException for the given class.
     * @param nonAspectClass the offending class
     */
    public NotAnAtAspectException(Class<?> nonAspectClass) {
        super(nonAspectClass.getName() + " is not an @AspectJ aspect");
        this.nonAspectClass = nonAspectClass;
    }

    /**
     * Returns the offending class.
     */
    public Class<?> getNonAspectClass() {
        return this.nonAspectClass;
    }

}
