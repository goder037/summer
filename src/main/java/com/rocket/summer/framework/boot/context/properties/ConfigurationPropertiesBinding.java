package com.rocket.summer.framework.boot.context.properties;

import com.rocket.summer.framework.core.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Qualifier for beans that are needed to configure the binding of
 * {@link ConfigurationProperties} (e.g. Converters).
 *
 * @author Dave Syer
 */
@Qualifier
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigurationPropertiesBinding {

}
