package com.rocket.summer.framework.boot.web.servlet;

import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.annotation.ScannedGenericBeanDefinition;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Handler for {@link WebServlet}-annotated classes.
 *
 * @author Andy Wilkinson
 */
class WebServletHandler extends ServletComponentHandler {

    WebServletHandler() {
        super(WebServlet.class);
    }

    @Override
    public void doHandle(Map<String, Object> attributes,
                         ScannedGenericBeanDefinition beanDefinition,
                         BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(ServletRegistrationBean.class);
        builder.addPropertyValue("asyncSupported", attributes.get("asyncSupported"));
        builder.addPropertyValue("initParameters", extractInitParameters(attributes));
        builder.addPropertyValue("loadOnStartup", attributes.get("loadOnStartup"));
        String name = determineName(attributes, beanDefinition);
        builder.addPropertyValue("name", name);
        builder.addPropertyValue("servlet", beanDefinition);
        builder.addPropertyValue("urlMappings",
                extractUrlPatterns("urlPatterns", attributes));
        builder.addPropertyValue("multipartConfig",
                determineMultipartConfig(beanDefinition));
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }

    private String determineName(Map<String, Object> attributes,
                                 BeanDefinition beanDefinition) {
        return (String) (StringUtils.hasText((String) attributes.get("name"))
                ? attributes.get("name") : beanDefinition.getBeanClassName());
    }

    private MultipartConfigElement determineMultipartConfig(
            ScannedGenericBeanDefinition beanDefinition) {
        Map<String, Object> attributes = beanDefinition.getMetadata()
                .getAnnotationAttributes(MultipartConfig.class.getName());
        if (attributes == null) {
            return null;
        }
        return new MultipartConfigElement((String) attributes.get("location"),
                (Long) attributes.get("maxFileSize"),
                (Long) attributes.get("maxRequestSize"),
                (Integer) attributes.get("fileSizeThreshold"));
    }

}

