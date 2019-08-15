package com.rocket.summer.framework.boot.web.servlet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.core.annotation.AliasFor;

/**
 * Enables scanning for Servlet components ({@link WebFilter filters}, {@link WebServlet
 * servlets}, and {@link WebListener listeners}). Scanning is only performed when using an
 * embedded Servlet container.
 * <p>
 * Typically, one of {@code value}, {@code basePackages}, or {@code basePackageClasses}
 * should be specified to control the packages to be scanned for components. In their
 * absence, scanning will be performed from the package of the class with the annotation.
 *
 * @author Andy Wilkinson
 * @since 1.3.0
 * @see WebServlet
 * @see WebFilter
 * @see WebListener
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ServletComponentScanRegistrar.class)
public @interface ServletComponentScan {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @ServletComponentScan("org.my.pkg")} instead of
     * {@code @ServletComponentScan(basePackages="org.my.pkg")}.
     * @return the base packages to scan
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * Base packages to scan for annotated servlet components. {@link #value()} is an
     * alias for (and mutually exclusive with) this attribute.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     * @return the base packages to scan
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to
     * scan for annotated servlet components. The package of each class specified will be
     * scanned.
     * @return classes from the base packages to scan
     */
    Class<?>[] basePackageClasses() default {};

}

