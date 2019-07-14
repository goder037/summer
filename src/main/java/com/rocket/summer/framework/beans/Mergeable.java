package com.rocket.summer.framework.beans;

/**
 * Interface representing an object whose value set can be merged with
 * that of a parent object.
 *
 * @author Rob Harrop
 * @since 2.0
 * @see org.springframework.beans.factory.support.ManagedSet
 * @see org.springframework.beans.factory.support.ManagedList
 * @see org.springframework.beans.factory.support.ManagedMap
 * @see org.springframework.beans.factory.support.ManagedProperties
 */
public interface Mergeable {

    /**
     * Is merging enabled for this particular instance?
     */
    boolean isMergeEnabled();

    /**
     * Merge the current value set with that of the supplied object.
     * <p>The supplied object is considered the parent, and values in
     * the callee's value set must override those of the supplied object.
     * @param parent the object to merge with
     * @return the result of the merge operation
     * @throws IllegalArgumentException if the supplied parent is <code>null</code>
     * @exception IllegalStateException if merging is not enabled for this instance
     * (i.e. <code>mergeEnabled</code> equals <code>false</code>).
     */
    Object merge(Object parent);

}
