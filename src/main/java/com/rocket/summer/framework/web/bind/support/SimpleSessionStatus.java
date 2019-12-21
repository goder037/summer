package com.rocket.summer.framework.web.bind.support;

/**
 * Simple implementation of the {@link SessionStatus} interface,
 * keeping the <code>complete</code> flag as an instance variable.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public class SimpleSessionStatus implements SessionStatus {

    private boolean complete = false;


    public void setComplete() {
        this.complete = true;
    }

    public boolean isComplete() {
        return this.complete;
    }

}
