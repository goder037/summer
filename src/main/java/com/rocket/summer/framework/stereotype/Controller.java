package com.rocket.summer.framework.stereotype;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a "Controller" (e.g. a web controller).
 *
 * <p>This annotation serves as a specialization of {@link Component @Component},
 * allowing for implementation classes to be autodetected through classpath scanning.
 * It is typically used in combination with annotated handler methods based on the
 * {@link com.rocket.summer.framework.web.bind.annotation.RequestMapping} annotation.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 2.5
 * @see Component
 * @see com.rocket.summer.framework.web.bind.annotation.RequestMapping
 * @see com.rocket.summer.framework.context.annotation.ClassPathBeanDefinitionScanner
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any
     */
    String value() default "";

}
