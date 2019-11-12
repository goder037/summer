package com.rocket.summer.framework.data.redis.core.types;

import java.util.concurrent.TimeUnit;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Expiration holds a value with its associated {@link TimeUnit}.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public class Expiration {

    private long expirationTime;
    private TimeUnit timeUnit;

    /**
     * Creates new {@link Expiration}.
     *
     * @param expirationTime can be {@literal null}. Defaulted to {@link TimeUnit#SECONDS}
     * @param timeUnit
     */
    protected Expiration(long expirationTime, TimeUnit timeUnit) {

        this.expirationTime = expirationTime;
        this.timeUnit = timeUnit != null ? timeUnit : TimeUnit.SECONDS;
    }

    /**
     * Get the expiration time converted into {@link TimeUnit#MILLISECONDS}.
     *
     * @return
     */
    public long getExpirationTimeInMilliseconds() {
        return getConverted(TimeUnit.MILLISECONDS);
    }

    /**
     * Get the expiration time converted into {@link TimeUnit#SECONDS}.
     *
     * @return
     */
    public long getExpirationTimeInSeconds() {
        return getConverted(TimeUnit.SECONDS);
    }

    /**
     * Get the expiration time.
     *
     * @return
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * Get the time unit for the expiration time.
     *
     * @return
     */
    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    /**
     * Get the expiration time converted into the desired {@code targetTimeUnit}.
     *
     * @param targetTimeUnit must not {@literal null}.
     * @return
     * @throws IllegalArgumentException
     */
    public long getConverted(TimeUnit targetTimeUnit) {

        Assert.notNull(targetTimeUnit, "TargetTimeUnit must not be null!");
        return targetTimeUnit.convert(expirationTime, timeUnit);
    }

    /**
     * Creates new {@link Expiration} with {@link TimeUnit#SECONDS}.
     *
     * @param expirationTime
     * @return
     */
    public static Expiration seconds(long expirationTime) {
        return new Expiration(expirationTime, TimeUnit.SECONDS);
    }

    /**
     * Creates new {@link Expiration} with {@link TimeUnit#MILLISECONDS}.
     *
     * @param expirationTime
     * @return
     */
    public static Expiration milliseconds(long expirationTime) {
        return new Expiration(expirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates new {@link Expiration} with the provided {@link TimeUnit}. Greater units than {@link TimeUnit#SECONDS} are
     * converted to {@link TimeUnit#SECONDS}. Units smaller than {@link TimeUnit#MILLISECONDS} are converted to
     * {@link TimeUnit#MILLISECONDS} and can lose precision since {@link TimeUnit#MILLISECONDS} is the smallest granularity
     * supported by Redis.
     *
     * @param expirationTime
     * @param timeUnit can be {@literal null}. Defaulted to {@link TimeUnit#SECONDS}
     * @return
     */
    public static Expiration from(long expirationTime, TimeUnit timeUnit) {

        if (ObjectUtils.nullSafeEquals(timeUnit, TimeUnit.MICROSECONDS)
                || ObjectUtils.nullSafeEquals(timeUnit, TimeUnit.NANOSECONDS)
                || ObjectUtils.nullSafeEquals(timeUnit, TimeUnit.MILLISECONDS)) {
            return new Expiration(timeUnit.toMillis(expirationTime), TimeUnit.MILLISECONDS);
        }

        if (timeUnit != null) {
            return new Expiration(timeUnit.toSeconds(expirationTime), TimeUnit.SECONDS);
        }

        return new Expiration(expirationTime, TimeUnit.SECONDS);
    }

    /**
     * Creates new persistent {@link Expiration}.
     *
     * @return
     */
    public static Expiration persistent() {
        return new Expiration(-1, TimeUnit.SECONDS);
    }

    /**
     * @return {@literal true} if {@link Expiration} is set to persistent.
     */
    public boolean isPersistent() {
        return expirationTime == -1;
    }
}

