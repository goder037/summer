package com.rocket.summer.framework.aop.scope;

import com.rocket.summer.framework.aop.framework.AopInfrastructureBean;
import com.rocket.summer.framework.aop.framework.ProxyConfig;
import com.rocket.summer.framework.aop.framework.ProxyFactory;
import com.rocket.summer.framework.aop.support.DelegatingIntroductionInterceptor;
import com.rocket.summer.framework.aop.target.SimpleBeanTargetSource;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.FactoryBean;
import com.rocket.summer.framework.beans.factory.FactoryBeanNotInitializedException;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.util.ClassUtils;

import java.lang.reflect.Modifier;

/**
 * Convenient proxy factory bean for scoped objects.
 *
 * <p>Proxies created using this factory bean are thread-safe singletons
 * and may be injected into shared objects, with transparent scoping behavior.
 *
 * <p>Proxies returned by this class implement the {@link ScopedObject} interface.
 * This presently allows for removing the corresponding object from the scope,
 * seamlessly creating a new instance in the scope on next access.
 *
 * <p>Please note that the proxies created by this factory are
 * <i>class-based</i> proxies by default. This can be customized
 * through switching the "proxyTargetClass" property to "false".
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see #setProxyTargetClass
 */
public class ScopedProxyFactoryBean extends ProxyConfig implements FactoryBean, BeanFactoryAware {

    /** The TargetSource that manages scoping */
    private final SimpleBeanTargetSource scopedTargetSource = new SimpleBeanTargetSource();

    /** The name of the target bean */
    private String targetBeanName;

    /** The cached singleton proxy */
    private Object proxy;


    /**
     * Create a new ScopedProxyFactoryBean instance.
     */
    public ScopedProxyFactoryBean() {
        setProxyTargetClass(true);
    }


    /**
     * Set the name of the bean that is to be scoped.
     */
    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
        this.scopedTargetSource.setTargetBeanName(targetBeanName);
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;

        this.scopedTargetSource.setBeanFactory(beanFactory);

        ProxyFactory pf = new ProxyFactory();
        pf.copyFrom(this);
        pf.setTargetSource(this.scopedTargetSource);

        Class beanType = beanFactory.getType(this.targetBeanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot create scoped proxy for bean '" + this.targetBeanName +
                    "': Target type could not be determined at the time of proxy creation.");
        }
        if (!isProxyTargetClass() || beanType.isInterface() || Modifier.isPrivate(beanType.getModifiers())) {
            pf.setInterfaces(ClassUtils.getAllInterfacesForClass(beanType, cbf.getBeanClassLoader()));
        }

        // Add an introduction that implements only the methods on ScopedObject.
        ScopedObject scopedObject = new DefaultScopedObject(cbf, this.scopedTargetSource.getTargetBeanName());
        pf.addAdvice(new DelegatingIntroductionInterceptor(scopedObject));

        // Add the AopInfrastructureBean marker to indicate that the scoped proxy
        // itself is not subject to auto-proxying! Only its target bean is.
        pf.addInterface(AopInfrastructureBean.class);

        this.proxy = pf.getProxy(cbf.getBeanClassLoader());
    }


    public Object getObject() {
        if (this.proxy == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.proxy;
    }

    public Class getObjectType() {
        if (this.proxy != null) {
            return this.proxy.getClass();
        }
        if (this.scopedTargetSource != null) {
            return this.scopedTargetSource.getTargetClass();
        }
        return null;
    }

    public boolean isSingleton() {
        return true;
    }

}

