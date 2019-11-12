package com.rocket.summer.framework.data.domain;

import com.rocket.summer.framework.util.Assert;

import java.util.Objects;

/**
 * Simple value object to work with ranges.
 *
 * @author Oliver Gierke
 * @since 1.10
 */
public final class Range<T extends Comparable<T>> {

    /**
     * The lower bound of the range.
     */
    private final T lowerBound;

    /**
     * The upper bound of the range.
     */
    private final T upperBound;

    /**
     * Whether the lower bound is considered inclusive.
     */
    private final boolean lowerInclusive;

    /**
     * Whether the lower bound is considered inclusive.
     */
    private final boolean upperInclusive;

    /**
     * Creates a new {@link Range} with the given lower and upper bound. Treats the given values as inclusive bounds. Use
     * {@link #Range(Comparable, Comparable, boolean, boolean)} to configure different bound behavior.
     *
     * @see #Range(Comparable, Comparable, boolean, boolean)
     * @param lowerBound can be {@literal null} in case upperBound is not {@literal null}.
     * @param upperBound can be {@literal null} in case lowerBound is not {@literal null}.
     */
    public Range(T lowerBound, T upperBound) {
        this(lowerBound, upperBound, true, true);
    }

    /**
     * Creates a new {@link Range} with the given lower and upper bound as well as the given inclusive/exclusive
     * semantics.
     *
     * @param lowerBound can be {@literal null}.
     * @param upperBound can be {@literal null}.
     * @param lowerInclusive
     * @param upperInclusive
     */
    public Range(T lowerBound, T upperBound, boolean lowerInclusive, boolean upperInclusive) {

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;
    }

    /**
     * Returns whether the {@link Range} contains the given value.
     *
     * @param value must not be {@literal null}.
     * @return
     */
    public boolean contains(T value) {

        Assert.notNull(value, "Reference value must not be null!");

        boolean greaterThanLowerBound = lowerBound == null ? true
                : lowerInclusive ? lowerBound.compareTo(value) <= 0 : lowerBound.compareTo(value) < 0;
        boolean lessThanUpperBound = upperBound == null ? true
                : upperInclusive ? upperBound.compareTo(value) >= 0 : upperBound.compareTo(value) > 0;

        return greaterThanLowerBound && lessThanUpperBound;
    }

    public T getLowerBound() {
        return lowerBound;
    }

    public T getUpperBound() {
        return upperBound;
    }

    public boolean isLowerInclusive() {
        return lowerInclusive;
    }

    public boolean isUpperInclusive() {
        return upperInclusive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range<?> range = (Range<?>) o;
        return lowerInclusive == range.lowerInclusive &&
                upperInclusive == range.upperInclusive &&
                Objects.equals(lowerBound, range.lowerBound) &&
                Objects.equals(upperBound, range.upperBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerBound, upperBound, lowerInclusive, upperInclusive);
    }

    @Override
    public String toString() {
        return "Range{" +
                "lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                ", lowerInclusive=" + lowerInclusive +
                ", upperInclusive=" + upperInclusive +
                '}';
    }
}

