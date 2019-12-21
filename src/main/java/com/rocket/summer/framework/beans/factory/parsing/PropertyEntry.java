package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link ParseState} entry representing a JavaBean property.
 *
 * @author Rob Harrop
 * @since 2.0
 */
public class PropertyEntry implements ParseState.Entry {

    private final String name;


    /**
     * Creates a new instance of the {@link PropertyEntry} class.
     * @param name the name of the JavaBean property represented by this instance
     * @throws IllegalArgumentException if the supplied <code>name</code> is <code>null</code>
     * or consists wholly of whitespace
     */
    public PropertyEntry(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Invalid property name '" + name + "'.");
        }
        this.name = name;
    }


    public String toString() {
        return "Property '" + this.name + "'";
    }

}
