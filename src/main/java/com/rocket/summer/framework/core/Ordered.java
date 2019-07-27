package com.rocket.summer.framework.core;

/**
 * Interface that can be implemented by objects that should be
 * orderable, for example in a Collection.
 *
 * <p>The actual order can be interpreted as prioritization, with
 * the first object (with the lowest order value) having the highest
 * priority.
 *
 * <p>Note that there is a 'priority' marker for this interface:
 * {@link PriorityOrdered}. Order values expressed by PriorityOrdered
 * objects always apply before order values of 'plain' Ordered values.
 *
 * @author Juergen Hoeller
 * @since 07.04.2003
 * @see OrderComparator
 * @see com.rocket.summer.framework.core.annotation.Order
 */
public interface Ordered {

    /**
     * Useful constant for the highest precedence value.
     * @see java.lang.Integer#MIN_VALUE
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * Useful constant for the lowest precedence value.
     * @see java.lang.Integer#MAX_VALUE
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;


    /**
     * Return the order value of this object, with a
     * higher value meaning greater in terms of sorting.
     * <p>Normally starting with 0 or 1, with {@link #LOWEST_PRECEDENCE}
     * indicating greatest. Same order values will result in arbitrary
     * positions for the affected objects.
     * <p>Higher value can be interpreted as lower priority,
     * consequently the first object has highest priority
     * (somewhat analogous to Servlet "load-on-startup" values).
     * <p>Note that order values below 0 are reserved for framework
     * purposes. Application-specified values should always be 0 or
     * greater, with only framework components (internal or third-party)
     * supposed to use lower values.
     * @return the order value
     * @see #LOWEST_PRECEDENCE
     */
    int getOrder();

}
