package com.rocket.summer.framework.web.bind.annotation;

import java.lang.annotation.*;

/**
 * Annotation that binds a method parameter or method return value
 * to a named model attribute, exposed to a web view. Supported
 * for {@link RequestMapping} annotated handler classes.
 *
 * <p>Can be used to expose command objects to a web view, using
 * specific attribute names, through annotating corresponding
 * parameters of a {@link RequestMapping} annotated handler method).
 *
 * <p>Can also be used to expose reference data to a web view
 * through annotating accessor methods in a controller class which
 * is based on {@link RequestMapping} annotated handler methods,
 * with such accessor methods allowed to have any arguments that
 * {@link RequestMapping} supports for handler methods, returning
 * the model attribute value to expose.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelAttribute {

    /**
     * The name of the model attribute to bind to.
     * <p>The default model attribute name is inferred from the declared
     * attribute type (i.e. the method parameter type or method return type),
     * based on the non-qualified class name:
     * e.g. "orderAddress" for class "mypackage.OrderAddress",
     * or "orderAddressList" for "List&lt;mypackage.OrderAddress&gt;".
     */
    String value() default "";

}
