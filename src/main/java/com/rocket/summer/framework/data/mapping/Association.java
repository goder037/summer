package com.rocket.summer.framework.data.mapping;

/**
 * Value object to capture {@link Association}s.
 *
 *  {@link PersistentProperty}s the association connects.
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
public class Association<P extends PersistentProperty<P>> {

    private final P inverse;
    private final P obverse;

    /**
     * Creates a new {@link Association} between the two given {@link PersistentProperty}s.
     *
     * @param inverse
     * @param obverse
     */
    public Association(P inverse, P obverse) {
        this.inverse = inverse;
        this.obverse = obverse;
    }

    public P getInverse() {
        return inverse;
    }

    public P getObverse() {
        return obverse;
    }
}
