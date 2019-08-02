package com.rocket.summer.framework.boot.context.properties;

import com.rocket.summer.framework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable support for {@link ConfigurationProperties} annotated beans.
 * {@link ConfigurationProperties} beans can be registered in the standard way (for
 * example using {@link Bean @Bean} methods) or, for convenience, can be specified
 * directly on this annotation.
 *
 * @author Dave Syer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableConfigurationPropertiesImportSelector.class)
public @interface EnableConfigurationProperties {

    /**
     * Convenient way to quickly register {@link ConfigurationProperties} annotated beans
     * with Spring. Standard Spring Beans will also be scanned regardless of this value.
     * @return {@link ConfigurationProperties} annotated beans to register
     */
    Class<?>[] value() default {};

}
