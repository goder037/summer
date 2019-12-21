package com.rocket.summer.framework.boot.context.properties;

import java.lang.annotation.*;

/**
 * Indicates that a getter in a {@link ConfigurationProperties} object is deprecated. This
 * annotation has no bearing on the actual binding processes, but it is used by the
 * {@code spring-boot-configuration-processor} to add deprecation meta-data.
 * <p>
 * This annotation <strong>must</strong> be used on the getter of the deprecated element.
 *
 * @author Phillip Webb
 * @since 1.3.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeprecatedConfigurationProperty {

    /**
     * The reason for the deprecation.
     * @return the deprecation reason
     */
    String reason() default "";

    /**
     * The field that should be used instead (if any).
     * @return the replacement field
     */
    String replacement() default "";

}
