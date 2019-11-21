package com.rocket.summer.framework.data.mapping;

/**
 * Association handler to work with the untyped {@link PersistentProperty} based {@link Association}.
 *
 * @author Oliver Gierke
 * @see PropertyHandler
 */
public interface SimpleAssociationHandler {

    /**
     * Handle the given {@link Association}.
     *
     * @param association will never be {@literal null}.
     */
    void doWithAssociation(Association<? extends PersistentProperty<?>> association);
}
