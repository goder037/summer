package com.rocket.summer.framework.aop.config;

import com.rocket.summer.framework.beans.factory.parsing.ParseState;

/**
 * {@link ParseState} entry representing an advisor.
 *
 * @author Mark Fisher
 * @since 2.0
 */
public class AdvisorEntry implements ParseState.Entry {

    private final String name;


    /**
     * Creates a new instance of the {@link AdvisorEntry} class.
     * @param name the bean name of the advisor
     */
    public AdvisorEntry(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Advisor '" + this.name + "'";
    }

}
