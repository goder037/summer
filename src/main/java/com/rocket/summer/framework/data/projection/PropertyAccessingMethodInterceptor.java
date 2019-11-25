package com.rocket.summer.framework.data.projection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.BeanWrapper;
import com.rocket.summer.framework.data.util.DirectFieldAccessFallbackBeanWrapper;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ReflectionUtils;

/**
 * Method interceptor to forward a delegation to bean property accessor methods to the property of a given target.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 * @since 1.10
 */
class PropertyAccessingMethodInterceptor implements MethodInterceptor {

    private final BeanWrapper target;

    /**
     * Creates a new {@link PropertyAccessingMethodInterceptor} for the given target object.
     *
     * @param target must not be {@literal null}.
     */
    public PropertyAccessingMethodInterceptor(Object target) {

        Assert.notNull(target, "Proxy target must not be null!");
        this.target = new DirectFieldAccessFallbackBeanWrapper(target);
    }

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        if (ReflectionUtils.isObjectMethod(method)) {
            return invocation.proceed();
        }

        PropertyDescriptor descriptor = BeanUtils.findPropertyForMethod(method);

        if (descriptor == null) {
            throw new IllegalStateException("Invoked method is not a property accessor!");
        }

        if (!isSetterMethod(method, descriptor)) {
            return target.getPropertyValue(descriptor.getName());
        }

        if (invocation.getArguments().length != 1) {
            throw new IllegalStateException("Invoked setter method requires exactly one argument!");
        }

        target.setPropertyValue(descriptor.getName(), invocation.getArguments()[0]);
        return null;
    }

    private static boolean isSetterMethod(Method method, PropertyDescriptor descriptor) {
        return method.equals(descriptor.getWriteMethod());
    }
}

