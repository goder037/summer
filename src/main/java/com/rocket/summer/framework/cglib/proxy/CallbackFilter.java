package com.rocket.summer.framework.cglib.proxy;

import java.lang.reflect.Method;

/**
 * Map methods of subclasses generated by {@link Enhancer} to a particular
 * callback. The type of the callbacks chosen for each method affects
 * the bytecode generated for that method in the subclass, and cannot
 * change for the life of the class.
 * <p>Note: {@link CallbackFilter} implementations are supposed to be
 * lightweight as cglib might keep {@link CallbackFilter} objects
 * alive to enable caching of generated classes. Prefer using {@code static}
 * classes for implementation of {@link CallbackFilter}.</p>
 */
public interface CallbackFilter {
    /**
     * Map a method to a callback.
     * @param method the intercepted method
     * @return the index into the array of callbacks (as specified by {@link Enhancer#setCallbacks}) to use for the method,
     */
    int accept(Method method);

    /**
     * The <code>CallbackFilter</code> in use affects which cached class
     * the <code>Enhancer</code> will use, so this is a reminder that
     * you should correctly implement <code>equals</code> and
     * <code>hashCode</code> for custom <code>CallbackFilter</code>
     * implementations in order to improve performance.
     */
    boolean equals(Object o);
}

