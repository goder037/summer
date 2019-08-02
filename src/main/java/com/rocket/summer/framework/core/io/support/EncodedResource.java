package com.rocket.summer.framework.core.io.support;

import com.rocket.summer.framework.core.io.InputStreamSource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Holder that combines a {@link com.rocket.summer.framework.core.io.Resource}
 * with a specific encoding to be used for reading from the resource.
 *
 * <p>Used as argument for operations that support to read content with
 * a specific encoding (usually through a <code>java.io.Reader</code>.
 *
 * @author Juergen Hoeller
 * @since 1.2.6
 * @see java.io.Reader
 */
public class EncodedResource implements InputStreamSource {

    private final Resource resource;

    private final String encoding;

    private final Charset charset;

    /**
     * Create a new {@code EncodedResource} for the given {@code Resource},
     * not specifying an explicit encoding or {@code Charset}.
     * @param resource the {@code Resource} to hold (never {@code null})
     */
    public EncodedResource(Resource resource) {
        this(resource, null, null);
    }

    /**
     * Create a new {@code EncodedResource} for the given {@code Resource},
     * using the specified {@code encoding}.
     * @param resource the {@code Resource} to hold (never {@code null})
     * @param encoding the encoding to use for reading from the resource
     */
    public EncodedResource(Resource resource, String encoding) {
        this(resource, encoding, null);
    }

    /**
     * Create a new {@code EncodedResource} for the given {@code Resource},
     * using the specified {@code Charset}.
     * @param resource the {@code Resource} to hold (never {@code null})
     * @param charset the {@code Charset} to use for reading from the resource
     */
    public EncodedResource(Resource resource, Charset charset) {
        this(resource, null, charset);
    }

    private EncodedResource(Resource resource, String encoding, Charset charset) {
        super();
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.encoding = encoding;
        this.charset = charset;
    }

    /**
     * Open an {@code InputStream} for the specified resource, ignoring any specified
     * {@link #getCharset() Charset} or {@linkplain #getEncoding() encoding}.
     * @throws IOException if opening the InputStream failed
     * @see #requiresReader()
     * @see #getReader()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return this.resource.getInputStream();
    }

    /**
     * Determine whether a {@link Reader} is required as opposed to an {@link InputStream},
     * i.e. whether an {@linkplain #getEncoding() encoding} or a {@link #getCharset() Charset}
     * has been specified.
     * @see #getReader()
     * @see #getInputStream()
     */
    public boolean requiresReader() {
        return (this.encoding != null || this.charset != null);
    }

    /**
     * Return the Resource held.
     */
    public final Resource getResource() {
        return this.resource;
    }

    /**
     * Return the encoding to use for reading from the resource,
     * or <code>null</code> if none specified.
     */
    public final String getEncoding() {
        return this.encoding;
    }

    /**
     * Open a <code>java.io.Reader</code> for the specified resource,
     * using the specified encoding (if any).
     * @throws IOException if opening the Reader failed
     */
    public Reader getReader() throws IOException {
        if (this.encoding != null) {
            return new InputStreamReader(this.resource.getInputStream(), this.encoding);
        }
        else {
            return new InputStreamReader(this.resource.getInputStream());
        }
    }


    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EncodedResource) {
            EncodedResource otherRes = (EncodedResource) obj;
            return (this.resource.equals(otherRes.resource) &&
                    ObjectUtils.nullSafeEquals(this.encoding, otherRes.encoding));
        }
        return false;
    }

    public int hashCode() {
        return this.resource.hashCode();
    }

    public String toString() {
        return this.resource.toString();
    }

}
