package com.rocket.summer.framework.expression;

/**
 * Instances of a type comparator should be able to compare pairs of objects for equality.
 * The specification of the return value is the same as for {@link java.lang.Comparable}.
 *
 * @author Andy Clement
 * @since 3.0
 * @see java.lang.Comparable
 */
public interface TypeComparator {

    /**
     * Return {@code true} if the comparator can compare these two objects.
     * @param firstObject the first object
     * @param secondObject the second object
     * @return {@code true} if the comparator can compare these objects
     */
    boolean canCompare(Object firstObject, Object secondObject);

    /**
     * Compare two given objects.
     * @param firstObject the first object
     * @param secondObject the second object
     * @return 0 if they are equal, <0 if the first is smaller than the second,
     * or >0 if the first is larger than the second
     * @throws EvaluationException if a problem occurs during comparison
     * (or if they are not comparable in the first place)
     */
    int compare(Object firstObject, Object secondObject) throws EvaluationException;

}
