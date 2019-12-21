package com.rocket.summer.framework.beans;

/**
 * Interface to be implemented by bean metadata elements
 * that carry a configuration source object.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public interface BeanMetadataElement {

    /**
     * Return the configuration source <code>Object</code> for this metadata element
     * (may be <code>null</code>).
     */
    Object getSource();

}
