package com.rocket.summer.framework.aop.aspectj.annotation;

import java.io.Serializable;

import com.rocket.summer.framework.util.Assert;

/**
 * Decorator to cause a {@link MetadataAwareAspectInstanceFactory} to instantiate only once.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class LazySingletonAspectInstanceFactoryDecorator implements MetadataAwareAspectInstanceFactory, Serializable {

    private final MetadataAwareAspectInstanceFactory maaif;

    private volatile Object materialized;


    /**
     * Create a new lazily initializing decorator for the given AspectInstanceFactory.
     * @param maaif the MetadataAwareAspectInstanceFactory to decorate
     */
    public LazySingletonAspectInstanceFactoryDecorator(MetadataAwareAspectInstanceFactory maaif) {
        Assert.notNull(maaif, "AspectInstanceFactory must not be null");
        this.maaif = maaif;
    }


    @Override
    public Object getAspectInstance() {
        if (this.materialized == null) {
            Object mutex = this.maaif.getAspectCreationMutex();
            if (mutex == null) {
                this.materialized = this.maaif.getAspectInstance();
            }
            else {
                synchronized (mutex) {
                    if (this.materialized == null) {
                        this.materialized = this.maaif.getAspectInstance();
                    }
                }
            }
        }
        return this.materialized;
    }

    public boolean isMaterialized() {
        return (this.materialized != null);
    }

    @Override
    public ClassLoader getAspectClassLoader() {
        return this.maaif.getAspectClassLoader();
    }

    @Override
    public AspectMetadata getAspectMetadata() {
        return this.maaif.getAspectMetadata();
    }

    @Override
    public Object getAspectCreationMutex() {
        return this.maaif.getAspectCreationMutex();
    }

    @Override
    public int getOrder() {
        return this.maaif.getOrder();
    }


    @Override
    public String toString() {
        return "LazySingletonAspectInstanceFactoryDecorator: decorating " + this.maaif;
    }

}

