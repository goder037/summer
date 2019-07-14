package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.DefaultListableBeanFactory;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.BeansException;

import java.io.IOException;

public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    private Boolean allowBeanDefinitionOverriding;

    private Boolean allowCircularReferences;

    /** Bean factory for this context */
    private DefaultListableBeanFactory beanFactory;

    /** Synchronization monitor for the internal BeanFactory */
    private final Object beanFactoryMonitor = new Object();

    /**
     * Create a new AbstractRefreshableApplicationContext with no parent.
     */
    public AbstractRefreshableApplicationContext() {
    }

    /**
     * Create a new AbstractRefreshableApplicationContext with the given parent context.
     * @param parent the parent context
     */
    public AbstractRefreshableApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    /**
     * Customize the internal bean factory used by this context.
     * Called for each {@link #refresh()} attempt.
     * <p>The default implementation applies this context's
     * {@link #setAllowBeanDefinitionOverriding "allowBeanDefinitionOverriding"}
     * and {@link #setAllowCircularReferences "allowCircularReferences"} settings,
     * if specified. Can be overridden in subclasses to customize any of
     * {@link DefaultListableBeanFactory}'s settings.
     * @param beanFactory the newly created bean factory for this context
     * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
     * @see DefaultListableBeanFactory#setAllowCircularReferences
     * @see DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
     * @see DefaultListableBeanFactory#setAllowEagerClassLoading
     */
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding.booleanValue());
        }
        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences.booleanValue());
        }
    }

    /**
     * Load bean definitions into the given bean factory, typically through
     * delegating to one or more bean definition readers.
     * @param beanFactory the bean factory to load bean definitions into
     * @throws IOException if loading of bean definition files failed
     * @throws BeansException if parsing of the bean definitions failed
     * @see org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
            throws IOException, BeansException;

    public final ConfigurableListableBeanFactory getBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("BeanFactory not initialized or already closed - " +
                        "call 'refresh' before accessing beans via the ApplicationContext");
            }
            return this.beanFactory;
        }
    }
}
