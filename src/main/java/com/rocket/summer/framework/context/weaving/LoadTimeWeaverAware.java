package com.rocket.summer.framework.context.weaving;

import com.rocket.summer.framework.beans.factory.Aware;
import com.rocket.summer.framework.instrument.classloading.LoadTimeWeaver;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the application context's default {@link LoadTimeWeaver}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.5
 * @see com.rocket.summer.framework.context.ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME
 */
public interface LoadTimeWeaverAware extends Aware {

    /**
     * Set the {@link LoadTimeWeaver} of this object's containing
     * {@link com.rocket.summer.framework.context.ApplicationContext ApplicationContext}.
     * <p>Invoked after the population of normal bean properties but before an
     * initialization callback like
     * {@link com.rocket.summer.framework.beans.factory.InitializingBean InitializingBean's}
     * {@link com.rocket.summer.framework.beans.factory.InitializingBean#afterPropertiesSet() afterPropertiesSet()}
     * or a custom init-method. Invoked after
     * {@link com.rocket.summer.framework.context.ApplicationContextAware ApplicationContextAware's}
     * {@link com.rocket.summer.framework.context.ApplicationContextAware#setApplicationContext setApplicationContext(..)}.
     * <p><b>NOTE:</b> This method will only be called if there actually is a
     * {@code LoadTimeWeaver} available in the application context. If
     * there is none, the method will simply not get invoked, assuming that the
     * implementing object is able to activate its weaving dependency accordingly.
     * @param loadTimeWeaver the {@code LoadTimeWeaver} instance (never {@code null})
     * @see com.rocket.summer.framework.beans.factory.InitializingBean#afterPropertiesSet
     * @see com.rocket.summer.framework.context.ApplicationContextAware#setApplicationContext
     */
    void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver);

}
