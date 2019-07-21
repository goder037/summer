package com.rocket.summer.framework.web.context.request;

import com.rocket.summer.framework.beans.factory.ObjectFactory;
import com.rocket.summer.framework.beans.factory.config.Scope;

/**
 * Abstract {@link Scope} implementation that reads from a particular scope
 * in the current thread-bound {@link RequestAttributes} object.
 *
 * <p>Subclasses simply need to implement {@link #getScope()} to instruct
 * this class which {@link RequestAttributes} scope to read attributes from.
 *
 * <p>Subclasses may wish to override the {@link #get} and {@link #remove}
 * methods to add synchronization around the call back into this super class.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 */
public abstract class AbstractRequestAttributesScope implements Scope {

    public Object get(String name, ObjectFactory objectFactory) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        Object scopedObject = attributes.getAttribute(name, getScope());
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            attributes.setAttribute(name, scopedObject, getScope());
        }
        return scopedObject;
    }

    public Object remove(String name) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        Object scopedObject = attributes.getAttribute(name, getScope());
        if (scopedObject != null) {
            attributes.removeAttribute(name, getScope());
            return scopedObject;
        }
        else {
            return null;
        }
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        attributes.registerDestructionCallback(name, callback, getScope());
    }

    public Object resolveContextualObject(String key) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return attributes.resolveReference(key);
    }


    /**
     * Template method that determines the actual target scope.
     * @return the target scope, in the form of an appropriate
     * {@link RequestAttributes} constant
     * @see RequestAttributes#SCOPE_REQUEST
     * @see RequestAttributes#SCOPE_SESSION
     * @see RequestAttributes#SCOPE_GLOBAL_SESSION
     */
    protected abstract int getScope();

}

