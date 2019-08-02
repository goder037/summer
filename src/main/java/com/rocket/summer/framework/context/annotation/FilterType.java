package com.rocket.summer.framework.context.annotation;

/**
 * Enumeration of the type filters that may be used in conjunction with
 * {@link ComponentScan @ComponentScan}.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.5
 * @see ComponentScan
 * @see ComponentScan#includeFilters()
 * @see ComponentScan#excludeFilters()
 * @see com.rocket.summer.framework.core.type.filter.TypeFilter
 */
public enum FilterType {

    /**
     * Filter candidates marked with a given annotation.
     * @see com.rocket.summer.framework.core.type.filter.AnnotationTypeFilter
     */
    ANNOTATION,

    /**
     * Filter candidates assignable to a given type.
     * @see com.rocket.summer.framework.core.type.filter.AssignableTypeFilter
     */
    ASSIGNABLE_TYPE,

    /**
     * Filter candidates matching a given AspectJ type pattern expression.
     * @see com.rocket.summer.framework.core.type.filter.AspectJTypeFilter
     */
    ASPECTJ,

    /**
     * Filter candidates matching a given regex pattern.
     * @see com.rocket.summer.framework.core.type.filter.RegexPatternTypeFilter
     */
    REGEX,

    /** Filter candidates using a given custom
     * {@link com.rocket.summer.framework.core.type.filter.TypeFilter} implementation.
     */
    CUSTOM

}
