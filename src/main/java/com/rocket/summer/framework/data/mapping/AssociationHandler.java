package com.rocket.summer.framework.data.mapping;

/**
 * Callback interface to implement functionality to be applied to a collection of {@link Association}s.
 *
 * @author Jon Brisbin <jbrisbin@vmware.com>
 * @author Oliver Gierke
 */
public interface AssociationHandler<P extends PersistentProperty<P>> {

    /**
     * Processes the given {@link Association}.
     *
     * @param association
     */
    void doWithAssociation(Association<P> association);
}
