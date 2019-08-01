package com.rocket.summer.framework.boot.context.properties;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanFactoryPostProcessor;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class to memorize {@code @Bean} definition meta data during initialization of
 * the bean factory.
 *
 * @author Dave Syer
 * @since 1.1.0
 */
public class ConfigurationBeanFactoryMetaData implements BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, MetaData> beans = new HashMap<String, MetaData>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        this.beanFactory = beanFactory;
        for (String name : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition definition = beanFactory.getBeanDefinition(name);
            String method = definition.getFactoryMethodName();
            String bean = definition.getFactoryBeanName();
            if (method != null && bean != null) {
                this.beans.put(name, new MetaData(bean, method));
            }
        }
    }

    public <A extends Annotation> Map<String, Object> getBeansWithFactoryAnnotation(
            Class<A> type) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String name : this.beans.keySet()) {
            if (findFactoryAnnotation(name, type) != null) {
                result.put(name, this.beanFactory.getBean(name));
            }
        }
        return result;
    }

    public <A extends Annotation> A findFactoryAnnotation(String beanName,
                                                          Class<A> type) {
        Method method = findFactoryMethod(beanName);
        return (method != null) ? AnnotationUtils.findAnnotation(method, type) : null;
    }

    private Method findFactoryMethod(String beanName) {
        if (!this.beans.containsKey(beanName)) {
            return null;
        }
        final AtomicReference<Method> found = new AtomicReference<Method>(null);
        MetaData meta = this.beans.get(beanName);
        final String factory = meta.getMethod();
        Class<?> type = this.beanFactory.getType(meta.getBean());
        ReflectionUtils.doWithMethods(type, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method)
                    throws IllegalArgumentException, IllegalAccessException {
                if (method.getName().equals(factory)) {
                    found.compareAndSet(null, method);
                }
            }
        });
        return found.get();
    }

    private static class MetaData {

        private String bean;

        private String method;

        MetaData(String bean, String method) {
            this.bean = bean;
            this.method = method;
        }

        public String getBean() {
            return this.bean;
        }

        public String getMethod() {
            return this.method;
        }

    }

}

