package com.rocket.summer.framework.data.redis.connection;

/**
 * Entity containing the parameters for the SORT operation.
 *
 * @author Costin Leau
 */
public interface SortParameters {

    /**
     * Sorting order.
     */
    public enum Order {
        ASC, DESC
    }

    /**
     * Utility class wrapping the 'LIMIT' setting.
     */
    static class Range {
        private final long start;
        private final long count;

        public Range(long start, long count) {
            this.start = start;
            this.count = count;
        }

        public long getStart() {
            return start;
        }

        public long getCount() {
            return count;
        }
    }

    /**
     * Returns the sorting order. Can be null if nothing is specified.
     *
     * @return sorting order
     */
    Order getOrder();

    /**
     * Indicates if the sorting is numeric (default) or alphabetical (lexicographical). Can be null if nothing is
     * specified.
     *
     * @return the type of sorting
     */
    Boolean isAlphabetic();

    /**
     * Returns the pattern (if set) for sorting by external keys (<tt>BY</tt>). Can be null if nothing is specified.
     *
     * @return <tt>BY</tt> pattern.
     */
    byte[] getByPattern();

    /**
     * Returns the pattern (if set) for retrieving external keys (<tt>GET</tt>). Can be null if nothing is specified.
     *
     * @return <tt>GET</tt> pattern.
     */
    byte[][] getGetPattern();

    /**
     * Returns the sorting limit (range or pagination). Can be null if nothing is specified.
     *
     * @return sorting limit/range
     */
    Range getLimit();
}

