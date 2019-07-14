package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.BeanMetadataElement;
import com.rocket.summer.framework.beans.Mergeable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tag collection class used to hold managed Set values, which may
 * include runtime bean references (to be resolved into bean objects).
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 21.01.2004
 */
public class ManagedSet extends LinkedHashSet implements Mergeable, BeanMetadataElement {

    private Object source;

    private boolean mergeEnabled;


    public ManagedSet() {
    }

    public ManagedSet(int initialCapacity) {
        super(initialCapacity);
    }


    /**
     * Set the configuration source <code>Object</code> for this metadata element.
     * <p>The exact type of the object will depend on the configuration mechanism used.
     */
    public void setSource(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return this.source;
    }

    /**
     * Set whether merging should be enabled for this collection,
     * in case of a 'parent' collection value being present.
     */
    public void setMergeEnabled(boolean mergeEnabled) {
        this.mergeEnabled = mergeEnabled;
    }

    public boolean isMergeEnabled() {
        return this.mergeEnabled;
    }

    public Object merge(Object parent) {
        if (!this.mergeEnabled) {
            throw new IllegalStateException("Not allowed to merge when the 'mergeEnabled' property is set to 'false'");
        }
        if (parent == null) {
            return this;
        }
        if (!(parent instanceof Set)) {
            throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
        }
        Set merged = new ManagedSet();
        merged.addAll((Set) parent);
        merged.addAll(this);
        return merged;
    }

}

