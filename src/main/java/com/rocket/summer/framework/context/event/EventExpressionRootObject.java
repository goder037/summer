package com.rocket.summer.framework.context.event;


import com.rocket.summer.framework.context.event.ApplicationEvent;

/**
 * Root object used during event listener expression evaluation.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class EventExpressionRootObject {

    private final ApplicationEvent event;

    private final Object[] args;

    public EventExpressionRootObject(ApplicationEvent event, Object[] args) {
        this.event = event;
        this.args = args;
    }

    public ApplicationEvent getEvent() {
        return event;
    }

    public Object[] getArgs() {
        return args;
    }

}

