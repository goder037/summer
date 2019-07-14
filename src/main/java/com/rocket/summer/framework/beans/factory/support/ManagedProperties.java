package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.BeanMetadataElement;
import com.rocket.summer.framework.beans.Mergeable;

import java.util.Properties;

/**
 * Tag class which represents a Spring-managed {@link Properties} instance
 * that supports merging of parent/child definitions.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class ManagedProperties extends Properties implements Mergeable, BeanMetadataElement {

    private Object source;

    private boolean mergeEnabled;


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
        if (!(parent instanceof Properties)) {
            throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
        }
        Properties merged = new ManagedProperties();
        merged.putAll((Properties) parent);
        merged.putAll(this);
        return merged;
    }

}
