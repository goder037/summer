package com.rocket.summer.framework.boot.autoconfigure;

import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Import and apply the specified auto-configuration classes. Applies the same ordering
 * rules as {@code @EnableAutoConfiguration} but restricts the auto-configuration classes
 * to the specified set, rather than consulting {@code spring.factories}.
 * <p>
 * Can also be used to {@link #exclude()} specific auto-configuration classes such that
 * they will never be applied.
 * <p>
 * Generally, {@code @EnableAutoConfiguration} should be used in preference to this
 * annotation, however, {@code @ImportAutoConfiguration} can be useful in some situations
 * and especially when writing tests.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.3.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(ImportAutoConfigurationImportSelector.class)
public @interface ImportAutoConfiguration {

    /**
     * The auto-configuration classes that should be imported. This is an alias for
     * {@link #classes()}.
     * @return the classes to import
     */
    @AliasFor("classes")
    Class<?>[] value() default {};

    /**
     * The auto-configuration classes that should be imported. When empty, the classes are
     * specified using an entry in {@code META-INF/spring.factories} where the key is the
     * fully-qualified name of the annotated class.
     * @return the classes to import
     */
    @AliasFor("value")
    Class<?>[] classes() default {};

    /**
     * Exclude specific auto-configuration classes such that they will never be applied.
     * @return the classes to exclude
     */
    Class<?>[] exclude() default {};

}

