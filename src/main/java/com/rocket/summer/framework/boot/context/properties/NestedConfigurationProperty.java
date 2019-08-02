package com.rocket.summer.framework.boot.context.properties;

import java.lang.annotation.*;

/**
 * Indicates that a field in a {@link ConfigurationProperties} object should be treated as
 * if it were a nested type. This annotation has no bearing on the actual binding
 * processes, but it is used by the {@code spring-boot-configuration-processor} as a hint
 * that a field is not bound as a single value. When this is specified, a nested group is
 * created for the field and its type is harvested.
 * <p>
 * This has no effect on collections and maps as these types are automatically identified.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @since 1.2.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NestedConfigurationProperty {

}

