package com.rocket.summer.framework.context.annotation;

/**
 * Enumeration used to determine whether JDK proxy-based or
 * AspectJ weaving-based advice should be applied.
 *
 * @author Chris Beams
 * @since 3.1
 * @see com.rocket.summer.framework.scheduling.annotation.EnableAsync#mode()
 * @see com.rocket.summer.framework.scheduling.annotation.AsyncConfigurationSelector#selectImports
 * @see com.rocket.summer.framework.transaction.annotation.EnableTransactionManagement#mode()
 */
public enum AdviceMode {

    /**
     * JDK proxy-based advice.
     */
    PROXY,

    /**
     * AspectJ weaving-based advice.
     */
    ASPECTJ

}
