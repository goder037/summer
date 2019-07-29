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
 * @see ComponentScan.IncludeFilter
 * @see ComponentScan.ExcludeFilter
 * @see com.rocket.summer.framework.core.type.filter.TypeFilter
 */
public enum FilterType {

    /**
     * Filter candidates marked with a given annotation.
     * @see org.springframework.core.type.filter.AnnotationTypeFilter
     */
    ANNOTATION,

    /**
     * Filter candidates assignable to a given type.
     * @see AssignableTypeFilter
     */
    ASSIGNABLE_TYPE,

    /** Filter candidates using a given custom
     * {@link org.springframework.core.type.filter.TypeFilter} implementation
     */
    CUSTOM

}

