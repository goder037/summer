package com.rocket.summer.framework.boot.jackson;

import java.lang.reflect.Modifier;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.HierarchicalBeanFactory;
import com.rocket.summer.framework.beans.factory.ListableBeanFactory;
import com.rocket.summer.framework.core.ResolvableType;

/**
 * Spring Bean and Jackson {@link Module} to register {@link JsonComponent} annotated
 * beans.
 *
 * @author Phillip Webb
 * @since 1.4.0
 * @see JsonComponent
 */
public class JsonComponentModule extends SimpleModule implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void registerJsonComponents() {
        BeanFactory beanFactory = this.beanFactory;
        while (beanFactory != null) {
            if (beanFactory instanceof ListableBeanFactory) {
                addJsonBeans((ListableBeanFactory) beanFactory);
            }
            beanFactory = (beanFactory instanceof HierarchicalBeanFactory)
                    ? ((HierarchicalBeanFactory) beanFactory).getParentBeanFactory()
                    : null;
        }
    }

    private void addJsonBeans(ListableBeanFactory beanFactory) {
        Map<String, Object> beans = beanFactory
                .getBeansWithAnnotation(JsonComponent.class);
        for (Object bean : beans.values()) {
            addJsonBean(bean);
        }
    }

    private void addJsonBean(Object bean) {
        if (bean instanceof JsonSerializer) {
            addSerializerWithDeducedType((JsonSerializer<?>) bean);
        }
        if (bean instanceof JsonDeserializer) {
            addDeserializerWithDeducedType((JsonDeserializer<?>) bean);
        }
        for (Class<?> innerClass : bean.getClass().getDeclaredClasses()) {
            if (!Modifier.isAbstract(innerClass.getModifiers())
                    && (JsonSerializer.class.isAssignableFrom(innerClass)
                    || JsonDeserializer.class.isAssignableFrom(innerClass))) {
                try {
                    addJsonBean(innerClass.newInstance());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    private <T> void addSerializerWithDeducedType(JsonSerializer<T> serializer) {
        ResolvableType type = ResolvableType.forClass(JsonSerializer.class,
                serializer.getClass());
        addSerializer((Class<T>) type.resolveGeneric(), serializer);
    }

    @SuppressWarnings({ "unchecked" })
    private <T> void addDeserializerWithDeducedType(JsonDeserializer<T> deserializer) {
        ResolvableType type = ResolvableType.forClass(JsonDeserializer.class,
                deserializer.getClass());
        addDeserializer((Class<T>) type.resolveGeneric(), deserializer);
    }

}

