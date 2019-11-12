package com.rocket.summer.framework.data.geo;

import com.rocket.summer.framework.data.annotation.PersistenceConstructor;
import com.rocket.summer.framework.util.Assert;

/**
 * Represents a geospatial circle value
 *
 * @author Mark Pollack
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @since 1.8
 */
public class Circle implements Shape {

    private static final long serialVersionUID = 5215611530535947924L;

    private final Point center;
    private final Distance radius;

    /**
     * Creates a new {@link Circle} from the given {@link Point} and radius.
     *
     * @param center must not be {@literal null}.
     * @param radius must not be {@literal null} and it's value greater or equal to zero.
     */
    @PersistenceConstructor
    public Circle(Point center, Distance radius) {

        Assert.notNull(center, "Center point must not be null!");
        Assert.notNull(radius, "Radius must not be null!");
        Assert.isTrue(radius.getValue() >= 0, "Radius must not be negative!");

        this.center = center;
        this.radius = radius;
    }

    /**
     * Creates a new {@link Circle} from the given {@link Point} and radius.
     *
     * @param center must not be {@literal null}.
     * @param radius's value must be greater or equal to zero.
     */
    public Circle(Point center, double radius) {
        this(center, new Distance(radius));
    }

    /**
     * Creates a new {@link Circle} from the given coordinates and radius as {@link Distance} with a
     * {@link Metrics#NEUTRAL}.
     *
     * @param centerX
     * @param centerY
     * @param radius must be greater or equal to zero.
     */
    public Circle(double centerX, double centerY, double radius) {
        this(new Point(centerX, centerY), new Distance(radius));
    }

    /**
     * Returns the center of the {@link Circle}.
     *
     * @return will never be {@literal null}.
     */
    public Point getCenter() {
        return center;
    }

    /**
     * Returns the radius of the {@link Circle}.
     *
     * @return
     */
    public Distance getRadius() {
        return radius;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Circle: [center=%s, radius=%s]", center, radius);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Circle)) {
            return false;
        }

        Circle that = (Circle) obj;

        return this.center.equals(that.center) && this.radius.equals(that.radius);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;

        result += 31 * center.hashCode();
        result += 31 * radius.hashCode();

        return result;
    }
}
