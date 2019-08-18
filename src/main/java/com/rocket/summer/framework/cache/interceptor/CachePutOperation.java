package com.rocket.summer.framework.cache.interceptor;

/**
 * Class describing a cache 'put' operation.
 *
 * @author Costin Leau
 * @author Phillip Webb
 * @author Marcin Kamionowski
 * @since 3.1
 */
public class CachePutOperation extends CacheOperation {

    private final String unless;


    /**
     * @since 4.3
     */
    public CachePutOperation(CachePutOperation.Builder b) {
        super(b);
        this.unless = b.unless;
    }


    public String getUnless() {
        return this.unless;
    }


    /**
     * @since 4.3
     */
    public static class Builder extends CacheOperation.Builder {

        private String unless;

        public void setUnless(String unless) {
            this.unless = unless;
        }

        @Override
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(" | unless='");
            sb.append(this.unless);
            sb.append("'");
            return sb;
        }

        public CachePutOperation build() {
            return new CachePutOperation(this);
        }
    }

}

