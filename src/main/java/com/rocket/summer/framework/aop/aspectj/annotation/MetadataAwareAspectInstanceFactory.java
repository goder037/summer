package com.rocket.summer.framework.aop.aspectj.annotation;

import com.rocket.summer.framework.aop.aspectj.AspectInstanceFactory;

/**
 * Subinterface of {@link com.rocket.summer.framework.aop.aspectj.AspectInstanceFactory}
 * that returns {@link AspectMetadata} associated with AspectJ-annotated classes.
 *
 * <p>Ideally, AspectInstanceFactory would include this method itself, but because
 * AspectMetadata uses Java-5-only {@link org.aspectj.lang.reflect.AjType},
 * we need to split out this subinterface.
 *
 * @author Rod Johnson
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjType
 */
public interface MetadataAwareAspectInstanceFactory extends AspectInstanceFactory {

    /**
     * Return the AspectJ AspectMetadata for this factory's aspect.
     * @return the aspect metadata
     */
    AspectMetadata getAspectMetadata();

    /**
     * Return the best possible creation mutex for this factory.
     * @return the mutex object (may be {@code null} for no mutex to use)
     * @since 4.3
     */
    Object getAspectCreationMutex();

}

