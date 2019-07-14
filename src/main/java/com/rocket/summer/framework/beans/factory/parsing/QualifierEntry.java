package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link ParseState} entry representing an autowire candidate qualifier.
 *
 * @author Mark Fisher
 * @since 2.5
 */
public class QualifierEntry implements ParseState.Entry {

    private String typeName;


    public QualifierEntry(String typeName) {
        if (!StringUtils.hasText(typeName)) {
            throw new IllegalArgumentException("Invalid qualifier type '" + typeName + "'.");
        }
        this.typeName = typeName;
    }

    public String toString() {
        return "Qualifier '" + this.typeName + "'";
    }

}
