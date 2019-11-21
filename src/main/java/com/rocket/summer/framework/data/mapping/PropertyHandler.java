package com.rocket.summer.framework.data.mapping;

/**
 * Callback interface to do something with all plain {@link PersistentProperty} instances <em>except</em> associations
 * and transient properties.
 *
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
public interface PropertyHandler<P extends PersistentProperty<P>> {

    void doWithPersistentProperty(P persistentProperty);
}

