package com.rocket.summer.framework.cglib.proxy;

/**
 * Dispatching {@link Enhancer} callback. This is the same as the
 * {@link Dispatcher} except for the addition of an argument
 * which references the proxy object.
 */
public interface ProxyRefDispatcher extends Callback {
    /**
     * Return the object which the original method invocation should
     * be dispatched. This method is called for <b>every</b> method invocation.
     * @param proxy a reference to the proxy (generated) object
     * @return an object that can invoke the method
     */
    Object loadObject(Object proxy) throws Exception;
}
