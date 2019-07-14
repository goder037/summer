package com.rocket.summer.framework.core;

import java.util.Comparator;

/**
 * {@link Comparator} implementation for {@link Ordered} objects,
 * sorting by order value ascending (resp. by priority descending).
 *
 * <p>Non-<code>Ordered</code> objects are treated as greatest order
 * values, thus ending up at the end of the list, in arbitrary order
 * (just like same order values of <code>Ordered</code> objects).
 *
 * @author Juergen Hoeller
 * @since 07.04.2003
 * @see Ordered
 * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
 */
public class OrderComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        boolean p1 = (o1 instanceof PriorityOrdered);
        boolean p2 = (o2 instanceof PriorityOrdered);
        if (p1 && !p2) {
            return -1;
        }
        else if (p2 && !p1) {
            return 1;
        }

        // Direct evaluation instead of Integer.compareTo to avoid unnecessary object creation.
        int i1 = getOrder(o1);
        int i2 = getOrder(o2);
        return (i1 < i2) ? -1 : (i1 > i2) ? 1 : 0;
    }

    /**
     * Determine the order value for the given object.
     * <p>The default implementation checks against the {@link Ordered}
     * interface. Can be overridden in subclasses.
     * @param obj the object to check
     * @return the order value, or <code>Ordered.LOWEST_PRECEDENCE</code> as fallback
     */
    protected int getOrder(Object obj) {
        return (obj instanceof Ordered ? ((Ordered) obj).getOrder() : Ordered.LOWEST_PRECEDENCE);
    }

}

