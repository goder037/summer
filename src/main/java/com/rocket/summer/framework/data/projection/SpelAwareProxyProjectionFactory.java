package com.rocket.summer.framework.data.projection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.annotation.Value;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.data.util.AnnotationDetectionMethodCallback;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;
import com.rocket.summer.framework.util.ReflectionUtils;

/**
 * A {@link ProxyProjectionFactory} that adds support to use {@link Value}-annotated methods on a projection interface
 * to evaluate the contained SpEL expression to define the outcome of the method call.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Mark Paluch
 * @since 1.10
 */
public class SpelAwareProxyProjectionFactory extends ProxyProjectionFactory implements BeanFactoryAware {

    private final Map<Class<?>, Boolean> typeCache = new ConcurrentHashMap<Class<?>, Boolean>();
    private final SpelExpressionParser parser = new SpelExpressionParser();

    private BeanFactory beanFactory;

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.BeanFactoryAware#setBeanFactory(com.rocket.summer.framework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * Inspects the given target type for methods with {@link Value} annotations and caches the result. Will create a
     * {@link SpelEvaluatingMethodInterceptor} if an annotation was found or return the delegate as is if not.
     *
     * @param interceptor the root {@link MethodInterceptor}.
     * @param source The backing source object.
     * @param projectionType the proxy target type.
     * @return
     */
    @Override
    protected MethodInterceptor postProcessAccessorInterceptor(MethodInterceptor interceptor, Object source,
                                                               Class<?> projectionType) {

        if (!typeCache.containsKey(projectionType)) {

            AnnotationDetectionMethodCallback<Value> callback = new AnnotationDetectionMethodCallback<Value>(Value.class);
            ReflectionUtils.doWithMethods(projectionType, callback);

            typeCache.put(projectionType, callback.hasFoundAnnotation());
        }

        return typeCache.get(projectionType)
                ? new SpelEvaluatingMethodInterceptor(interceptor, source, beanFactory, parser, projectionType)
                : interceptor;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.projection.ProxyProjectionFactory#getProjectionInformation(java.lang.Class)
     */
    @Override
    public ProjectionInformation getProjectionInformation(Class<?> projectionType) {
        return new SpelAwareProjectionInformation(projectionType);
    }

    protected static class SpelAwareProjectionInformation extends DefaultProjectionInformation {

        protected SpelAwareProjectionInformation(Class<?> projectionType) {
            super(projectionType);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.projection.DefaultProjectionInformation#isInputProperty(java.beans.PropertyDescriptor)
         */
        @Override
        protected boolean isInputProperty(PropertyDescriptor descriptor) {

            if (!super.isInputProperty(descriptor)) {
                return false;
            }

            Method readMethod = descriptor.getReadMethod();

            if (readMethod == null) {
                return false;
            }

            return AnnotationUtils.findAnnotation(readMethod, Value.class) == null;
        }
    }
}

