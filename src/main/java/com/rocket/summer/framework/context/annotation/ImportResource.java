package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.support.BeanDefinitionReader;

import java.lang.annotation.*;

/**
 * Indicates one or more resources containing bean definitions to import.
 *
 * <p>Like {@link Import @Import}, this annotation provides functionality similar to the
 * {@literal <import/>} element in Spring XML.  It is typically used when
 * designing {@link Configuration @Configuration} classes to be bootstrapped by
 * {@link AnnotationConfigApplicationContext}, but where some XML functionality such as
 * namespaces is still necessary.
 *
 * <p>By default, arguments to the {@link #value()} attribute will be processed using
 * an {@link XmlBeanDefinitionReader}, i.e. it is assumed that resources are Spring
 * {@literal <beans/>} XML files.  Optionally, the {@link #reader()} attribute may be
 * supplied, allowing the user to specify a different {@link BeanDefinitionReader}
 * implementation, such as
 * {@link com.rocket.summer.framework.beans.factory.support.PropertiesBeanDefinitionReader}.
 *
 * @author Chris Beams
 * @since 3.0
 * @see Configuration
 * @see Import
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ImportResource {

    /**
     * Resource paths to import.  Resource-loading prefixes such as {@literal classpath:} and
     * {@literal file:}, etc may be used.
     */
    String[] value();

    /**
     * {@link BeanDefinitionReader} implementation to use when processing resources specified
     * by the {@link #value()} attribute.
     */
    Class<? extends BeanDefinitionReader> reader() ;

}
