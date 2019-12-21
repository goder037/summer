package com.rocket.summer.framework.context;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the MessageSource (typically the ApplicationContext) that it runs in.
 *
 * <p>Note that the MessageSource can usually also be passed on as bean
 * reference (to arbitrary bean properties or constructor arguments), because
 * it is defined as bean with name "messageSource" in the application context.
 *
 * @author Juergen Hoeller
 * @since 1.1.1
 * @see ApplicationContextAware
 */
public interface MessageSourceAware {

    /**
     * Set the MessageSource that this object runs in.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's afterPropertiesSet or a custom init-method.
     * Invoked before ApplicationContextAware's setApplicationContext.
     * @param messageSource message sourceto be used by this object
     */
    void setMessageSource(MessageSource messageSource);

}
