package com.rocket.summer.framework.data.projection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.rocket.summer.framework.aop.framework.Advised;
import com.rocket.summer.framework.aop.framework.ProxyFactory;
import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.context.ResourceLoaderAware;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.support.DefaultConversionService;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * A {@link ProjectionFactory} to create JDK proxies to back interfaces and handle method invocations on them. By
 * default accessor methods are supported. In case the delegating lookups result in an object of different type that the
 * projection interface method's return type, another projection will be created to transparently mitigate between the
 * types.
 *
 * @author Oliver Gierke
 * @see SpelAwareProxyProjectionFactory
 * @since 1.10
 */
class ProxyProjectionFactory implements ProjectionFactory, ResourceLoaderAware, BeanClassLoaderAware {

    private static final boolean IS_JAVA_8 = com.rocket.summer.framework.util.ClassUtils.isPresent("java.util.Optional",
            ProxyProjectionFactory.class.getClassLoader());

    private final List<MethodInterceptorFactory> factories;
    private final ConversionService conversionService;
    private ClassLoader classLoader;

    /**
     * Creates a new {@link ProxyProjectionFactory}.
     */
    protected ProxyProjectionFactory() {

        this.factories = new ArrayList<MethodInterceptorFactory>();
        this.factories.add(MapAccessingMethodInterceptorFactory.INSTANCE);
        this.factories.add(PropertyAccessingMethodInvokerFactory.INSTANCE);

        this.conversionService = new DefaultConversionService();
    }

    /**
     * @see com.rocket.summer.framework.context.ResourceLoaderAware#setResourceLoader(com.rocket.summer.framework.core.io.ResourceLoader)
     * @deprecated rather set the {@link ClassLoader} directly via {@link #setBeanClassLoader(ClassLoader)}.
     */
    @Override
    @Deprecated
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.classLoader = resourceLoader.getClassLoader();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Registers the given {@link MethodInterceptorFactory} to be used with the factory. Factories registered later enjoy
     * precedence over previously registered ones.
     *
     * @param factory must not be {@literal null}.
     * @since 1.13
     */
    public void registerMethodInvokerFactory(MethodInterceptorFactory factory) {

        Assert.notNull(factory, "MethodInterceptorFactory must not be null!");

        this.factories.add(0, factory);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.rest.core.projection.ProjectionFactory#createProjection(java.lang.Object, java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProjection(Class<T> projectionType, Object source) {

        Assert.notNull(projectionType, "Projection type must not be null!");
        Assert.isTrue(projectionType.isInterface(), "Projection type must be an interface!");

        if (source == null || projectionType.isInstance(source)) {
            return (T) source;
        }

        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(source);
        factory.setOpaque(true);
        factory.setInterfaces(projectionType, TargetAware.class);

        if (IS_JAVA_8) {
            factory.addAdvice(new DefaultMethodInvokingMethodInterceptor());
        }

        factory.addAdvice(new TargetAwareMethodInterceptor(source.getClass()));
        factory.addAdvice(getMethodInterceptor(source, projectionType));

        return (T) factory.getProxy(classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.projection.ProjectionFactory#createProjection(java.lang.Class)
     */
    @Override
    public <T> T createProjection(Class<T> projectionType) {

        Assert.notNull(projectionType, "Projection type must not be null!");

        return createProjection(projectionType, new HashMap<String, Object>());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.projection.ProjectionFactory#getProperties(java.lang.Class)
     */
    @Override
    public List<String> getInputProperties(Class<?> projectionType) {

        Assert.notNull(projectionType, "Projection type must not be null!");

        List<String> result = new ArrayList<String>();

        for (PropertyDescriptor descriptor : getProjectionInformation(projectionType).getInputProperties()) {
            result.add(descriptor.getName());
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.projection.ProjectionFactory#getProjectionInformation(java.lang.Class)
     */
    @Override
    public ProjectionInformation getProjectionInformation(Class<?> projectionType) {
        return new DefaultProjectionInformation(projectionType);
    }

    /**
     * Returns the {@link MethodInterceptor} to add to the proxy.
     *
     * @param source must not be {@literal null}.
     * @param projectionType must not be {@literal null}.
     * @return
     */
    private MethodInterceptor getMethodInterceptor(Object source, Class<?> projectionType) {

        MethodInterceptor propertyInvocationInterceptor = getFactoryFor(source, projectionType)
                .createMethodInterceptor(source, projectionType);

        return new ProjectingMethodInterceptor(this,
                postProcessAccessorInterceptor(propertyInvocationInterceptor, source, projectionType), conversionService);
    }

    /**
     * Returns the {@link MethodInterceptorFactory} to be used with the given source object and target type.
     *
     * @param source must not be {@literal null}.
     * @param projectionType must not be {@literal null}.
     * @return
     */
    private MethodInterceptorFactory getFactoryFor(Object source, Class<?> projectionType) {

        for (MethodInterceptorFactory factory : factories) {
            if (factory.supports(source, projectionType)) {
                return factory;
            }
        }

        throw new IllegalStateException("No MethodInterceptorFactory found for type ".concat(source.getClass().getName()));
    }

    /**
     * Post-process the given {@link MethodInterceptor} for the given source instance and projection type. Default
     * implementation will simply return the given interceptor.
     *
     * @param interceptor will never be {@literal null}.
     * @param source will never be {@literal null}.
     * @param projectionType will never be {@literal null}.
     * @return
     */
    protected MethodInterceptor postProcessAccessorInterceptor(MethodInterceptor interceptor, Object source,
                                                               Class<?> projectionType) {
        return interceptor;
    }

    /**
     * Custom {@link MethodInterceptor} to expose the proxy target class even if we set
     * {@link ProxyFactory#setOpaque(boolean)} to true to prevent properties on {@link Advised} to be rendered.
     *
     * @author Oliver Gierke
     */
    private static class TargetAwareMethodInterceptor implements MethodInterceptor {

        private static final Method GET_TARGET_CLASS_METHOD;
        private static final Method GET_TARGET_METHOD;

        private final Class<?> targetType;

        static {
            try {
                GET_TARGET_CLASS_METHOD = TargetAware.class.getMethod("getTargetClass");
                GET_TARGET_METHOD = TargetAware.class.getMethod("getTarget");
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        /**
         * Creates a new {@link TargetAwareMethodInterceptor} with the given target class.
         *
         * @param targetType must not be {@literal null}.
         */
        public TargetAwareMethodInterceptor(Class<?> targetType) {

            Assert.notNull(targetType, "Target type must not be null!");
            this.targetType = targetType;
        }

        /*
         * (non-Javadoc)
         * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
         */
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {

            if (invocation.getMethod().equals(GET_TARGET_CLASS_METHOD)) {
                return targetType;
            } else if (invocation.getMethod().equals(GET_TARGET_METHOD)) {
                return invocation.getThis();
            }

            return invocation.proceed();
        }
    }

    /**
     * {@link MethodInterceptorFactory} handling {@link Map}s as target objects.
     *
     * @author Oliver Gierke
     */
    private static enum MapAccessingMethodInterceptorFactory implements MethodInterceptorFactory {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.projection.MethodInterceptorFactory#createMethodInterceptor(java.lang.Object, java.lang.Class)
         */
        @Override
        @SuppressWarnings("unchecked")
        public MethodInterceptor createMethodInterceptor(Object source, Class<?> targetType) {
            return new MapAccessingMethodInterceptor((Map<String, Object>) source);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.projection.MethodInterceptorFactory#supports(java.lang.Object, java.lang.Class)
         */
        @Override
        public boolean supports(Object source, Class<?> targetType) {
            return Map.class.isInstance(source);
        }
    }

    /**
     * {@link MethodInterceptorFactory} to create a {@link PropertyAccessingMethodInterceptor} for arbitrary objects.
     *
     * @author Oliver Gierke
     */
    private static enum PropertyAccessingMethodInvokerFactory implements MethodInterceptorFactory {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.projection.MethodInterceptorFactory#createMethodInterceptor(java.lang.Object, java.lang.Class)
         */
        @Override
        public MethodInterceptor createMethodInterceptor(Object source, Class<?> targetType) {
            return new PropertyAccessingMethodInterceptor(source);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.projection.MethodInterceptorFactory#supports(java.lang.Object, java.lang.Class)
         */
        @Override
        public boolean supports(Object source, Class<?> targetType) {
            return true;
        }
    }
}

