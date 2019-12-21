package com.rocket.summer.framework.util.comparator;

import java.util.Comparator;

/**
 * Comparator that adapts Comparables to the Comparator interface.
 * Mainly for internal use in other Comparators, when supposed
 * to work on Comparables.
 *
 * @author Keith Donald
 * @since 1.2.2
 * @see Comparable
 */
public class ComparableComparator<T extends Comparable<T>> implements Comparator<T> {

    @SuppressWarnings("rawtypes")
    public static final ComparableComparator INSTANCE = new ComparableComparator();

    @Override
    public int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }

}

