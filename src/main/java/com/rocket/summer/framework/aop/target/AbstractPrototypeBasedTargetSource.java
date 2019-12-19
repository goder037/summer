package com.rocket.summer.framework.aop.target;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.DisposableBean;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;

/**
 * Base class for dynamic {@link com.rocket.summer.framework.aop.TargetSource} implementations
 * that create new prototype bean instances to support a pooling or
 * new-instance-per-invocation strategy.
 *
 * <p>Such TargetSources must run in a {@link BeanFactory}, as it needs to
 * call the {@code getBean} method to create a new prototype instance.
 * Therefore, this base class extends {@link AbstractBeanFactoryBasedTargetSource}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.rocket.summer.framework.beans.factory.BeanFactory#getBean
 * @see PrototypeTargetSource
 * @see ThreadLocalTargetSource
 * @see CommonsPool2TargetSource
 */
@SuppressWarnings("serial")
public abstract class AbstractPrototypeBasedTargetSource extends AbstractBeanFactoryBasedTargetSource {

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);

        // Check whether the target bean is defined as prototype.
        if (!beanFactory.isPrototype(getTargetBeanName())) {
            throw new BeanDefinitionStoreException(
                    "Cannot use prototype-based TargetSource against non-prototype bean with name '" +
                            getTargetBeanName() + "': instances would not be independent");
        }
    }

    /**
     * Subclasses should call this method to create a new prototype instance.
     * @throws BeansException if bean creation failed
     */
    protected Object newPrototypeInstance() throws BeansException {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating new instance of bean '" + getTargetBeanName() + "'");
        }
        return getBeanFactory().getBean(getTargetBeanName());
    }

    /**
     * Subclasses should call this method to destroy an obsolete prototype instance.
     * @param target the bean instance to destroy
     */
    protected void destroyPrototypeInstance(Object target) {
        if (logger.isDebugEnabled()) {
            logger.debug("Destroying instance of bean '" + getTargetBeanName() + "'");
        }
        if (getBeanFactory() instanceof ConfigurableBeanFactory) {
            ((ConfigurableBeanFactory) getBeanFactory()).destroyBean(getTargetBeanName(), target);
        }
        else if (target instanceof DisposableBean) {
            try {
                ((DisposableBean) target).destroy();
            }
            catch (Throwable ex) {
                logger.error("Couldn't invoke destroy method of bean with name '" + getTargetBeanName() + "'", ex);
            }
        }
    }


    //---------------------------------------------------------------------
    // Serialization support
    //---------------------------------------------------------------------

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("A prototype-based TargetSource itself is not deserializable - " +
                "just a disconnected SingletonTargetSource is");
    }

    /**
     * Replaces this object with a SingletonTargetSource on serialization.
     * Protected as otherwise it won't be invoked for subclasses.
     * (The {@code writeReplace()} method must be visible to the class
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

}

