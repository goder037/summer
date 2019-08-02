package com.rocket.summer.framework.context.weaving;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.config.BeanPostProcessor;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.instrument.classloading.LoadTimeWeaver;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link com.rocket.summer.framework.beans.factory.config.BeanPostProcessor}
 * implementation that passes the context's default {@link LoadTimeWeaver}
 * to beans that implement the {@link LoadTimeWeaverAware} interface.
 *
 * <p>{@link com.rocket.summer.framework.context.ApplicationContext Application contexts}
 * will automatically register this with their underlying {@link BeanFactory bean factory},
 * provided that a default {@code LoadTimeWeaver} is actually available.
 *
 * <p>Applications should not use this class directly.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see LoadTimeWeaverAware
 * @see com.rocket.summer.framework.context.ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME
 */
public class LoadTimeWeaverAwareProcessor implements BeanPostProcessor, BeanFactoryAware {

    private LoadTimeWeaver loadTimeWeaver;

    private BeanFactory beanFactory;


    /**
     * Create a new {@code LoadTimeWeaverAwareProcessor} that will
     * auto-retrieve the {@link LoadTimeWeaver} from the containing
     * {@link BeanFactory}, expecting a bean named
     * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
     */
    public LoadTimeWeaverAwareProcessor() {
    }

    /**
     * Create a new {@code LoadTimeWeaverAwareProcessor} for the given
     * {@link LoadTimeWeaver}.
     * <p>If the given {@code loadTimeWeaver} is {@code null}, then a
     * {@code LoadTimeWeaver} will be auto-retrieved from the containing
     * {@link BeanFactory}, expecting a bean named
     * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
     * @param loadTimeWeaver the specific {@code LoadTimeWeaver} that is to be used
     */
    public LoadTimeWeaverAwareProcessor(LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    /**
     * Create a new {@code LoadTimeWeaverAwareProcessor}.
     * <p>The {@code LoadTimeWeaver} will be auto-retrieved from
     * the given {@link BeanFactory}, expecting a bean named
     * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
     * @param beanFactory the BeanFactory to retrieve the LoadTimeWeaver from
     */
    public LoadTimeWeaverAwareProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LoadTimeWeaverAware) {
            LoadTimeWeaver ltw = this.loadTimeWeaver;
            if (ltw == null) {
                Assert.state(this.beanFactory != null,
                        "BeanFactory required if no LoadTimeWeaver explicitly specified");
                ltw = this.beanFactory.getBean(
                        ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME, LoadTimeWeaver.class);
            }
            ((LoadTimeWeaverAware) bean).setLoadTimeWeaver(ltw);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        return bean;
    }

}
