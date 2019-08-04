package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.beans.factory.MergedBeanDefinitionPostProcessor;
import com.rocket.summer.framework.beans.factory.config.DestructionAwareBeanPostProcessor;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.event.ApplicationEventMulticaster;
import com.rocket.summer.framework.util.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code BeanPostProcessor} that detects beans which implement the {@code ApplicationListener}
 * interface. This catches beans that can't reliably be detected by {@code getBeanNamesForType}
 * and related operations which only work against top-level beans.
 *
 * <p>With standard Java serialization, this post-processor won't get serialized as part of
 * {@code DisposableBeanAdapter} to begin with. However, with alternative serialization
 * mechanisms, {@code DisposableBeanAdapter.writeReplace} might not get used at all, so we
 * defensively mark this post-processor's field state as {@code transient}.
 *
 * @author Juergen Hoeller
 * @since 4.3.4
 */
class ApplicationListenerDetector implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor {

    private static final Log logger = LogFactory.getLog(ApplicationListenerDetector.class);

    private transient final AbstractApplicationContext applicationContext;

    private transient final Map<String, Boolean> singletonNames = new ConcurrentHashMap<String, Boolean>(256);


    public ApplicationListenerDetector(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (this.applicationContext != null) {
            this.singletonNames.put(beanName, beanDefinition.isSingleton());
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (this.applicationContext != null && bean instanceof ApplicationListener) {
            // potentially not detected as a listener by getBeanNamesForType retrieval
            Boolean flag = this.singletonNames.get(beanName);
            if (Boolean.TRUE.equals(flag)) {
                // singleton bean (top-level or inner): register on the fly
                this.applicationContext.addApplicationListener((ApplicationListener<?>) bean);
            }
            else if (Boolean.FALSE.equals(flag)) {
                if (logger.isWarnEnabled() && !this.applicationContext.containsBean(beanName)) {
                    // inner bean with other scope - can't reliably process events
                    logger.warn("Inner bean '" + beanName + "' implements ApplicationListener interface " +
                            "but is not reachable for event multicasting by its containing ApplicationContext " +
                            "because it does not have singleton scope. Only top-level listener beans are allowed " +
                            "to be of non-singleton scope.");
                }
                this.singletonNames.remove(beanName);
            }
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        if (this.applicationContext != null && bean instanceof ApplicationListener) {
            try {
                ApplicationEventMulticaster multicaster = this.applicationContext.getApplicationEventMulticaster();
                multicaster.removeApplicationListener((ApplicationListener<?>) bean);
                multicaster.removeApplicationListenerBean(beanName);
            }
            catch (IllegalStateException ex) {
                // ApplicationEventMulticaster not initialized yet - no need to remove a listener
            }
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return (bean instanceof ApplicationListener);
    }


    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof ApplicationListenerDetector &&
                this.applicationContext == ((ApplicationListenerDetector) other).applicationContext));
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.applicationContext);
    }

}

