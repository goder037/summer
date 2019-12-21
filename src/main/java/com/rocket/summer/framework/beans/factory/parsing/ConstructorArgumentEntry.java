package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.util.Assert;

/**
 * {@link ParseState} entry representing a (possibly indexed)
 * constructor argument.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class ConstructorArgumentEntry implements ParseState.Entry {

    private final int index;


    /**
     * Creates a new instance of the {@link ConstructorArgumentEntry} class
     * representing a constructor argument with a (currently) unknown index.
     */
    public ConstructorArgumentEntry() {
        this.index = -1;
    }

    /**
     * Creates a new instance of the {@link ConstructorArgumentEntry} class
     * representing a constructor argument at the supplied <code>index</code>.
     * @param index the index of the constructor argument
     * @throws IllegalArgumentException if the supplied <code>index</code>
     * is less than zero
     */
    public ConstructorArgumentEntry(int index) {
        Assert.isTrue(index >= 0, "Constructor argument index must be greater than or equal to zero");
        this.index = index;
    }


    public String toString() {
        return "Constructor-arg" + (this.index >= 0 ? " #" + this.index : "");
    }

}

