package com.rocket.summer.framework.data.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to exclude repository interfaces from being picked up and thus in consequence getting an instance being
 * created.
 * <p/>
 * This will typically be used when providing an extended base interface for all repositories in combination with a
 * custom repository base class to implement methods declared in that intermediate interface. In this case you typically
 * derive your concrete repository interfaces from the intermediate one but don't want to create a Spring bean for the
 * intermediate interface.
 *
 * @author Oliver Gierke
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface NoRepositoryBean {

}
