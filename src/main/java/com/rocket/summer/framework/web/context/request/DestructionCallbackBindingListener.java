package com.rocket.summer.framework.web.context.request;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;

/**
 * Adapter that implements the Servlet 2.3 HttpSessionBindingListener
 * interface, wrapping a session destruction callback.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see RequestAttributes#registerDestructionCallback
 * @see ServletRequestAttributes#registerSessionDestructionCallback
 */
public class DestructionCallbackBindingListener implements HttpSessionBindingListener, Serializable {

    private final Runnable destructionCallback;


    /**
     * Create a new DestructionCallbackBindingListener for the given callback.
     * @param destructionCallback the Runnable to execute when this listener
     * object gets unbound from the session
     */
    public DestructionCallbackBindingListener(Runnable destructionCallback) {
        this.destructionCallback = destructionCallback;
    }


    public void valueBound(HttpSessionBindingEvent event) {
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        this.destructionCallback.run();
    }

}

