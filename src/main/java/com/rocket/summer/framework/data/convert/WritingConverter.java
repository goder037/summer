package com.rocket.summer.framework.data.convert;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.core.convert.converter.Converter;

/**
 * Annotation to clarify intended usage of a {@link Converter} as writing converter in case the conversion types leave
 * room for disambiguation.
 *
 * @author Oliver Gierke
 */
@Target(TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface WritingConverter {

}
