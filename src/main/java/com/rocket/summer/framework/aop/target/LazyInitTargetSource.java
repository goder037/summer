package com.rocket.summer.framework.aop.target;

import com.rocket.summer.framework.context.BeansException;

/**
 * {@link com.rocket.summer.framework.aop.TargetSource} that lazily accesses a
 * singleton bean from a {@link com.rocket.summer.framework.beans.factory.BeanFactory}.
 *
 * <p>Useful when a proxy reference is needed on initialization but
 * the actual target object should not be initialized until first use.
 * When the target bean is defined in an
 * {@link com.rocket.summer.framework.context.ApplicationContext} (or a
 * {@code BeanFactory} that is eagerly pre-instantiating singleton beans)
 * it must be marked as "lazy-init" too, else it will be instantiated by said
 * {@code ApplicationContext} (or {@code BeanFactory}) on startup.
 * <p>For example:
 *
 * <pre class="code">
 * &lt;bean id="serviceTarget" class="example.MyService" lazy-init="true"&gt;
 *   ...
 * &lt;/bean&gt;
 *
 * &lt;bean id="service" class="com.rocket.summer.framework.aop.framework.ProxyFactoryBean"&gt;
 *   &lt;property name="targetSource"&gt;
 *     &lt;bean class="com.rocket.summer.framework.aop.target.LazyInitTargetSource"&gt;
 *       &lt;property name="targetBeanName"&gt;&lt;idref local="serviceTarget"/&gt;&lt;/property&gt;
 *     &lt;/bean&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * The "serviceTarget" bean will not get initialized until a method on the
 * "service" proxy gets invoked.
 *
 * <p>Subclasses can extend this class and override the {@link #postProcessTargetObject(Object)} to
 * perform some additional processing with the target object when it is first loaded.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 1.1.4
 * @see com.rocket.summer.framework.beans.factory.BeanFactory#getBean
 * @see #postProcessTargetObject
 */
@SuppressWarnings("serial")
public class LazyInitTargetSource extends AbstractBeanFactoryBasedTargetSource {

    private Object target;


    @Override
    public synchronized Object getTarget() throws BeansException {
        if (this.target == null) {
            this.target = getBeanFactory().getBean(getTargetBeanName());
            postProcessTargetObject(this.target);
        }
        return this.target;
    }

    /**
     * Subclasses may override this method to perform additional processing on
     * the target object when it is first loaded.
     * @param targetObject the target object that has just been instantiated (and configured)
     */
    protected void postProcessTargetObject(Object targetObject) {
    }

}

