package com.rocket.summer.framework.aop.target;

import com.rocket.summer.framework.aop.TargetSource;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Base class for {@link com.rocket.summer.framework.aop.TargetSource} implementations
 * that are based on a Spring {@link com.rocket.summer.framework.beans.factory.BeanFactory},
 * delegating to Spring-managed bean instances.
 *
 * <p>Subclasses can create prototype instances or lazily access a
 * singleton target, for example. See {@link LazyInitTargetSource} and
 * {@link AbstractPrototypeBasedTargetSource}'s subclasses for concrete strategies.
 *
 * <p>BeanFactory-based TargetSources are serializable. This involves
 * disconnecting the current target and turning into a {@link SingletonTargetSource}.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 1.1.4
 * @see com.rocket.summer.framework.beans.factory.BeanFactory#getBean
 * @see LazyInitTargetSource
 * @see PrototypeTargetSource
 * @see ThreadLocalTargetSource
 * @see CommonsPoolTargetSource
 */
public abstract class AbstractBeanFactoryBasedTargetSource
        implements TargetSource, BeanFactoryAware, Serializable {

    /** use serialVersionUID from Spring 1.2.7 for interoperability */
    private static final long serialVersionUID = -4721607536018568393L;


    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    /** Name of the target bean we will create on each invocation */
    private String targetBeanName;

    /** Class of the target */
    private Class targetClass;

    /**
     * BeanFactory that owns this TargetSource. We need to hold onto this
     * reference so that we can create new prototype instances as necessary.
     */
    private BeanFactory beanFactory;


    /**
     * Set the name of the target bean in the factory.
     * <p>The target bean should not be a singleton, else the same instance will
     * always be obtained from the factory, resulting in the same behavior as
     * provided by {@link SingletonTargetSource}.
     * @param targetBeanName name of the target bean in the BeanFactory
     * that owns this interceptor
     * @see SingletonTargetSource
     */
    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    /**
     * Return the name of the target bean in the factory.
     */
    public String getTargetBeanName() {
        return this.targetBeanName;
    }

    /**
     * Specify the target class explicitly, to avoid any kind of access to the
     * target bean (for example, to avoid initialization of a FactoryBean instance).
     * <p>Default is to detect the type automatically, through a <code>getType</code>
     * call on the BeanFactory (or even a full <code>getBean</code> call as fallback).
     */
    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * Set the owning BeanFactory. We need to save a reference so that we can
     * use the <code>getBean</code> method on every invocation.
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.targetBeanName == null) {
            throw new IllegalStateException("Property'targetBeanName' is required");
        }
        this.beanFactory = beanFactory;
    }

    /**
     * Return the owning BeanFactory.
     */
    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }


    public synchronized Class getTargetClass() {
        if (this.targetClass == null && this.beanFactory != null) {
            // Determine type of the target bean.
            this.targetClass = this.beanFactory.getType(this.targetBeanName);
            if (this.targetClass == null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Getting bean with name '" + this.targetBeanName + "' in order to determine type");
                }
                this.targetClass = this.beanFactory.getBean(this.targetBeanName).getClass();
            }
        }
        return this.targetClass;
    }

    public boolean isStatic() {
        return false;
    }

    public void releaseTarget(Object target) throws Exception {
        // Nothing to do here.
    }


    /**
     * Copy configuration from the other AbstractBeanFactoryBasedTargetSource object.
     * Subclasses should override this if they wish to expose it.
     * @param other object to copy configuration from
     */
    protected void copyFrom(AbstractBeanFactoryBasedTargetSource other) {
        this.targetBeanName = other.targetBeanName;
        this.targetClass = other.targetClass;
        this.beanFactory = other.beanFactory;
    }

    /**
     * Replaces this object with a SingletonTargetSource on serialization.
     * Protected as otherwise it won't be invoked for subclasses.
     * (The <code>writeReplace()</code> method must be visible to the class
     * being serialized.)
     * <p>With this implementation of this method, there is no need to mark
     * non-serializable fields in this class or subclasses as transient.
     */
    protected Object writeReplace() throws ObjectStreamException {
        if (logger.isDebugEnabled()) {
            logger.debug("Disconnecting TargetSource [" + this + "]");
        }
        try {
            // Create disconnected SingletonTargetSource.
            return new SingletonTargetSource(getTarget());
        }
        catch (Exception ex) {
            logger.error("Cannot get target for disconnecting TargetSource [" + this + "]", ex);
            throw new NotSerializableException(
                    "Cannot get target for disconnecting TargetSource [" + this + "]: " + ex);
        }
    }


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        AbstractBeanFactoryBasedTargetSource otherTargetSource = (AbstractBeanFactoryBasedTargetSource) other;
        return (ObjectUtils.nullSafeEquals(this.beanFactory, otherTargetSource.beanFactory) &&
                ObjectUtils.nullSafeEquals(this.targetBeanName, otherTargetSource.targetBeanName));
    }

    public int hashCode() {
        int hashCode = getClass().hashCode();
        hashCode = 13 * hashCode + ObjectUtils.nullSafeHashCode(this.beanFactory);
        hashCode = 13 * hashCode + ObjectUtils.nullSafeHashCode(this.targetBeanName);
        return hashCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(ClassUtils.getShortName(getClass()));
        sb.append(" for target bean '").append(this.targetBeanName).append("'");
        if (this.targetClass != null) {
            sb.append(" of type [").append(this.targetClass.getName()).append("]");
        }
        return sb.toString();
    }

}

