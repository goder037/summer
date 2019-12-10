package com.rocket.summer.framework.boot.web.servlet;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.annotation.ScannedGenericBeanDefinition;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Handler for {@link WebFilter}-annotated classes.
 *
 * @author Andy Wilkinson
 */
class WebFilterHandler extends ServletComponentHandler {

    WebFilterHandler() {
        super(WebFilter.class);
    }

    @Override
    public void doHandle(Map<String, Object> attributes,
                         ScannedGenericBeanDefinition beanDefinition,
                         BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(FilterRegistrationBean.class);
        builder.addPropertyValue("asyncSupported", attributes.get("asyncSupported"));
        builder.addPropertyValue("dispatcherTypes", extractDispatcherTypes(attributes));
        builder.addPropertyValue("filter", beanDefinition);
        builder.addPropertyValue("initParameters", extractInitParameters(attributes));
        String name = determineName(attributes, beanDefinition);
        builder.addPropertyValue("name", name);
        builder.addPropertyValue("servletNames", attributes.get("servletNames"));
        builder.addPropertyValue("urlPatterns",
                extractUrlPatterns("urlPatterns", attributes));
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }

    private EnumSet<DispatcherType> extractDispatcherTypes(
            Map<String, Object> attributes) {
        DispatcherType[] dispatcherTypes = (DispatcherType[]) attributes
                .get("dispatcherTypes");
        if (dispatcherTypes.length == 0) {
            return EnumSet.noneOf(DispatcherType.class);
        }
        if (dispatcherTypes.length == 1) {
            return EnumSet.of(dispatcherTypes[0]);
        }
        return EnumSet.of(dispatcherTypes[0],
                Arrays.copyOfRange(dispatcherTypes, 1, dispatcherTypes.length));
    }

    private String determineName(Map<String, Object> attributes,
                                 BeanDefinition beanDefinition) {
        return (String) (StringUtils.hasText((String) attributes.get("filterName"))
                ? attributes.get("filterName") : beanDefinition.getBeanClassName());
    }

}

