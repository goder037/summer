package com.rocket.summer.framework.data.redis.core;

import com.rocket.summer.framework.util.StringUtils;

/**
 * Options to be used for with {@literal SCAN} command.
 *
 * @author Christoph Strobl
 * @author Thomas Darimont
 * @since 1.4
 */
public class ScanOptions {

    public static ScanOptions NONE = new ScanOptions();

    private Long count;
    private String pattern;

    private ScanOptions() {}

    /**
     * Static factory method that returns a new {@link ScanOptionsBuilder}.
     *
     * @return
     */
    public static ScanOptionsBuilder scanOptions() {
        return new ScanOptionsBuilder();
    }

    public Long getCount() {
        return count;
    }

    public String getPattern() {
        return pattern;
    }

    public String toOptionString() {

        if (this.equals(ScanOptions.NONE)) {
            return "";
        }

        String params = "";

        if (this.count != null) {
            params += (", 'count', " + count);
        }
        if (StringUtils.hasText(this.pattern)) {
            params += (", 'match' , '" + this.pattern + "'");
        }

        return params;
    }

    /**
     * @author Christoph Strobl
     * @since 1.4
     */
    public static class ScanOptionsBuilder {

        ScanOptions options;

        public ScanOptionsBuilder() {
            options = new ScanOptions();
        }

        /**
         * Returns the current {@link ScanOptionsBuilder} configured with the given {@code count}.
         *
         * @param count
         * @return
         */
        public ScanOptionsBuilder count(long count) {
            options.count = count;
            return this;
        }

        /**
         * Returns the current {@link ScanOptionsBuilder} configured with the given {@code pattern}.
         *
         * @param pattern
         * @return
         */
        public ScanOptionsBuilder match(String pattern) {
            options.pattern = pattern;
            return this;
        }

        public ScanOptions build() {
            return options;
        }

    }

}

