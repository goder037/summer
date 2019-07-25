package com.rocket.summer.framework.web.method;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.core.BridgeMethodResolver;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Encapsulates information about a bean method consisting of a
 * {@linkplain #getMethod() method} and a {@linkplain #getBean() bean}. Provides
 * convenient access to method parameters, the method return value, method
 * annotations.
 *
 * <p>The class may be created with a bean instance or with a bean name (e.g. lazy
 * bean, prototype bean). Use {@link #createWithResolvedBean()} to obtain an
 * {@link HandlerMethod} instance with a bean instance initialized through the
 * bean factory.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class HandlerMethod {

    /** Logger that is available to subclasses */
    protected final Log logger = LogFactory.getLog(HandlerMethod.class);

    private final Object bean;

    private final Method method;

    private final BeanFactory beanFactory;

    private MethodParameter[] parameters;

    private final Method bridgedMethod;


    /**
     * Create an instance from a bean instance and a method.
     */
    public HandlerMethod(Object bean, Method method) {
        Assert.notNull(bean, "bean is required");
        Assert.notNull(method, "method is required");
        this.bean = bean;
        this.beanFactory = null;
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
    }

    /**
     * Create an instance from a bean instance, method name, and parameter types.
     * @throws NoSuchMethodException when the method cannot be found
     */
    public HandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Assert.notNull(bean, "bean is required");
        Assert.notNull(methodName, "method is required");
        this.bean = bean;
        this.beanFactory = null;
        this.method = bean.getClass().getMethod(methodName, parameterTypes);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
    }

    /**
     * Create an instance from a bean name, a method, and a {@code BeanFactory}.
     * The method {@link #createWithResolvedBean()} may be used later to
     * re-create the {@code HandlerMethod} with an initialized the bean.
     */
    public HandlerMethod(String beanName, BeanFactory beanFactory, Method method) {
        Assert.hasText(beanName, "beanName is required");
        Assert.notNull(beanFactory, "beanFactory is required");
        Assert.notNull(method, "method is required");
        Assert.isTrue(beanFactory.containsBean(beanName),
                "Bean factory [" + beanFactory + "] does not contain bean [" + beanName + "]");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
    }

    /**
     * Create an instance from another {@code HandlerMethod}.
     */
    protected HandlerMethod(HandlerMethod handlerMethod) {
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        this.bean = handlerMethod.bean;
        this.beanFactory = handlerMethod.beanFactory;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
    }

    /**
     * Returns the bean for this handler method.
     */
    public Object getBean() {
        return this.bean;
    }

    /**
     * Returns the method for this handler method.
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * Returns the type of the handler for this handler method.
     * Note that if the bean type is a CGLIB-generated class, the original, user-defined class is returned.
     */
    public Class<?> getBeanType() {
        Class<?> clazz = (this.bean instanceof String)
                ? this.beanFactory.getType((String) this.bean) : this.bean.getClass();

        return ClassUtils.getUserClass(clazz);
    }

    /**
     * If the bean method is a bridge method, this method returns the bridged (user-defined) method.
     * Otherwise it returns the same method as {@link #getMethod()}.
     */
    protected Method getBridgedMethod() {
        return this.bridgedMethod;
    }

    /**
     * Returns the method parameters for this handler method.
     */
    public MethodParameter[] getMethodParameters() {
        if (this.parameters == null) {
            int parameterCount = this.bridgedMethod.getParameterTypes().length;
            this.parameters = new MethodParameter[parameterCount];
            for (int i = 0; i < parameterCount; i++) {
                this.parameters[i] = new HandlerMethodParameter(i);
            }
        }
        return this.parameters;
    }

    /**
     * Return the HandlerMethod return type.
     */
    public MethodParameter getReturnType() {
        return new HandlerMethodParameter(-1);
    }

    /**
     * Return the actual return value type.
     */
    public MethodParameter getReturnValueType(Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }

    /**
     * Returns {@code true} if the method return type is void, {@code false} otherwise.
     */
    public boolean isVoid() {
        return Void.TYPE.equals(getReturnType().getParameterType());
    }

    /**
     * Returns a single annotation on the underlying method traversing its super methods if no
     * annotation can be found on the given method itself.
     * @param annotationType the type of annotation to introspect the method for.
     * @return the annotation, or {@code null} if none found
     */
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(this.method, annotationType);
    }

    /**
     * If the provided instance contains a bean name rather than an object instance, the bean name is resolved
     * before a {@link HandlerMethod} is created and returned.
     */
    public HandlerMethod createWithResolvedBean() {
        Object handler = this.bean;
        if (this.bean instanceof String) {
            String beanName = (String) this.bean;
            handler = this.beanFactory.getBean(beanName);
        }
        HandlerMethod handlerMethod = new HandlerMethod(handler, this.method);
        handlerMethod.parameters = getMethodParameters();
        return handlerMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof HandlerMethod) {
            HandlerMethod other = (HandlerMethod) o;
            return this.bean.equals(other.bean) && this.method.equals(other.method);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * this.bean.hashCode() + this.method.hashCode();
    }

    @Override
    public String toString() {
        return method.toGenericString();
    }

    /**
     * A MethodParameter with HandlerMethod-specific behavior.
     */
    private class HandlerMethodParameter extends MethodParameter {

        protected HandlerMethodParameter(int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }

        @Override
        public Class<?> getDeclaringClass() {
            return HandlerMethod.this.getBeanType();
        }

        @Override
        public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.getMethodAnnotation(annotationType);
        }
    }

    /**
     * A MethodParameter for a HandlerMethod return type based on an actual return value.
     */
    private class ReturnValueMethodParameter extends HandlerMethodParameter {

        private final Object returnValue;

        public ReturnValueMethodParameter(Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
        }

        @Override
        public Class<?> getParameterType() {
            return (this.returnValue != null) ? this.returnValue.getClass() : super.getParameterType();
        }
    }

}

