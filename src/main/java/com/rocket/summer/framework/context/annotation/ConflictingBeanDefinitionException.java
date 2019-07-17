package com.rocket.summer.framework.context.annotation;

/**
 * Marker subclass of {@link IllegalStateException}, allowing for explicit
 * catch clauses in calling code.
 *
 * @author Chris Beams
 * @since 3.1
 */
class ConflictingBeanDefinitionException extends IllegalStateException {

    public ConflictingBeanDefinitionException(String message) {
        super(message);
    }

}
