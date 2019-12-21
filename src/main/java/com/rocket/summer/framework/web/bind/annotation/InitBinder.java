package com.rocket.summer.framework.web.bind.annotation;

import java.lang.annotation.*;

/**
 * Annotation that identifies methods which initialize the
 * {@link com.rocket.summer.framework.web.bind.WebDataBinder} which
 * will be used for populating command and form object arguments
 * of annotated handler methods.
 *
 * <p>Such init-binder methods support all arguments that {@link RequestMapping}
 * supports, except for command/form objects and corresponding validation result
 * objects. Init-binder methods must not have a return value; they are usually
 * declared as <code>void</code>.
 *
 * <p>Typical arguments are {@link com.rocket.summer.framework.web.bind.WebDataBinder}
 * in combination with {@link com.rocket.summer.framework.web.context.request.WebRequest}
 * or {@link java.util.Locale}, allowing to register context-specific editors.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see com.rocket.summer.framework.web.bind.WebDataBinder
 * @see com.rocket.summer.framework.web.context.request.WebRequest
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InitBinder {

    /**
     * The names of command/form attributes and/or request parameters
     * that this init-binder method is supposed to apply to.
     * <p>Default is to apply to all command/form attributes and all request parameters
     * processed by the annotated handler class. Specifying model attribute names or
     * request parameter names here restricts the init-binder method to those specific
     * attributes/parameters, with different init-binder methods typically applying to
     * different groups of attributes or parameters.
     */
    String[] value() default {};

}