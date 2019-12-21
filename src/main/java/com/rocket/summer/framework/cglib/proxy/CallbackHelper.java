package com.rocket.summer.framework.cglib.proxy;

import com.rocket.summer.framework.cglib.core.ReflectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version $Id: CallbackHelper.java,v 1.2 2004/06/24 21:15:20 herbyderby Exp $
 */
abstract public class CallbackHelper
        implements CallbackFilter
{
    private Map methodMap = new HashMap();
    private List callbacks = new ArrayList();

    public CallbackHelper(Class superclass, Class[] interfaces)
    {
        List methods = new ArrayList();
        Enhancer.getMethods(superclass, interfaces, methods);
        Map indexes = new HashMap();
        for (int i = 0, size = methods.size(); i < size; i++) {
            Method method = (Method)methods.get(i);
            Object callback = getCallback(method);
            if (callback == null)
                throw new IllegalStateException("getCallback cannot return null");
            boolean isCallback = callback instanceof Callback;
            if (!(isCallback || (callback instanceof Class)))
                throw new IllegalStateException("getCallback must return a Callback or a Class");
            if (i > 0 && ((callbacks.get(i - 1) instanceof Callback) ^ isCallback))
                throw new IllegalStateException("getCallback must return a Callback or a Class consistently for every Method");
            Integer index = (Integer)indexes.get(callback);
            if (index == null) {
                index = new Integer(callbacks.size());
                indexes.put(callback, index);
            }
            methodMap.put(method, index);
            callbacks.add(callback);
        }
    }

    abstract protected Object getCallback(Method method);

    public Callback[] getCallbacks()
    {
        if (callbacks.size() == 0)
            return new Callback[0];
        if (callbacks.get(0) instanceof Callback) {
            return (Callback[])callbacks.toArray(new Callback[callbacks.size()]);
        } else {
            throw new IllegalStateException("getCallback returned classes, not callbacks; call getCallbackTypes instead");
        }
    }

    public Class[] getCallbackTypes()
    {
        if (callbacks.size() == 0)
            return new Class[0];
        if (callbacks.get(0) instanceof Callback) {
            return ReflectUtils.getClasses(getCallbacks());
        } else {
            return (Class[])callbacks.toArray(new Class[callbacks.size()]);
        }
    }

    public int accept(Method method)
    {
        return ((Integer)methodMap.get(method)).intValue();
    }

    public int hashCode()
    {
        return methodMap.hashCode();
    }

    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (!(o instanceof CallbackHelper))
            return false;
        return methodMap.equals(((CallbackHelper)o).methodMap);
    }
}

