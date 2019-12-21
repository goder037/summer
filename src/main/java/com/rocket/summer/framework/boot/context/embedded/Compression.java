package com.rocket.summer.framework.boot.context.embedded;

/**
 * Simple container-independent abstraction for compression configuration.
 *
 * @author Ivan Sopov
 * @author Andy Wilkinson
 * @since 1.3.0
 */
public class Compression {

    /**
     * If response compression is enabled.
     */
    private boolean enabled = false;

    /**
     * Comma-separated list of MIME types that should be compressed.
     */
    private String[] mimeTypes = new String[] { "text/html", "text/xml", "text/plain",
            "text/css", "text/javascript", "application/javascript" };

    /**
     * Comma-separated list of user agents for which responses should not be compressed.
     */
    private String[] excludedUserAgents = null;

    /**
     * Minimum response size that is required for compression to be performed.
     */
    private int minResponseSize = 2048;

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String[] getMimeTypes() {
        return this.mimeTypes;
    }

    public void setMimeTypes(String[] mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public int getMinResponseSize() {
        return this.minResponseSize;
    }

    public void setMinResponseSize(int minSize) {
        this.minResponseSize = minSize;
    }

    public String[] getExcludedUserAgents() {
        return this.excludedUserAgents;
    }

    public void setExcludedUserAgents(String[] excludedUserAgents) {
        this.excludedUserAgents = excludedUserAgents;
    }

}
