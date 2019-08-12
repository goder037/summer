package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

import java.lang.annotation.*;

/**
 * {@link Conditional} that matches based on the JVM version the application is running
 * on.
 *
 * @author Oliver Gierke
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.1.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnJavaCondition.class)
public @interface ConditionalOnJava {

    /**
     * Configures whether the value configured in {@link #value()} shall be considered the
     * upper exclusive or lower inclusive boundary. Defaults to
     * {@link Range#EQUAL_OR_NEWER}.
     * @return the range
     */
    Range range() default Range.EQUAL_OR_NEWER;

    /**
     * The {@link JavaVersion} to check for. Use {@link #range()} to specify whether the
     * configured value is an upper-exclusive or lower-inclusive boundary.
     * @return the java version
     */
    JavaVersion value();

    /**
     * Range options.
     */
    enum Range {

        /**
         * Equal to, or newer than the specified {@link JavaVersion}.
         */
        EQUAL_OR_NEWER,

        /**
         * Older than the specified {@link JavaVersion}.
         */
        OLDER_THAN

    }

    /**
     * Java versions.
     */
    enum JavaVersion {

        /**
         * Java 1.9.
         */
        NINE(9, "1.9", "java.security.cert.URICertStoreParameters"),

        /**
         * Java 1.8.
         */
        EIGHT(8, "1.8", "java.util.function.Function"),

        /**
         * Java 1.7.
         */
        SEVEN(7, "1.7", "java.nio.file.Files"),

        /**
         * Java 1.6.
         */
        SIX(6, "1.6", "java.util.ServiceLoader");

        private final int value;

        private final String name;

        private final boolean available;

        JavaVersion(int value, String name, String className) {
            this.value = value;
            this.name = name;
            this.available = ClassUtils.isPresent(className, getClass().getClassLoader());
        }

        /**
         * Determines if this version is within the specified range of versions.
         * @param range the range
         * @param version the bounds of the range
         * @return if this version is within the specified range
         */
        public boolean isWithin(Range range, JavaVersion version) {
            Assert.notNull(range, "Range must not be null");
            Assert.notNull(version, "Version must not be null");
            switch (range) {
                case EQUAL_OR_NEWER:
                    return this.value >= version.value;
                case OLDER_THAN:
                    return this.value < version.value;
            }
            throw new IllegalStateException("Unknown range " + range);
        }

        @Override
        public String toString() {
            return this.name;
        }

        /**
         * Returns the {@link JavaVersion} of the current runtime.
         * @return the {@link JavaVersion}
         */
        public static JavaVersion getJavaVersion() {
            for (JavaVersion candidate : JavaVersion.values()) {
                if (candidate.available) {
                    return candidate;
                }
            }
            return SIX;
        }

    }

}

