package com.rocket.summer.framework.core.io;

import com.rocket.summer.framework.core.NestedIOException;
import com.rocket.summer.framework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class AbstractResource implements Resource {

    /**
     * This implementation checks whether a File can be opened,
     * falling back to whether an InputStream can be opened.
     * This will cover both directories and content resources.
     */
    public boolean exists() {
        // Try file existence: can we find the file in the file system?
        try {
            return getFile().exists();
        }
        catch (IOException ex) {
            // Fall back to stream existence: can we open the stream?
            try {
                InputStream is = getInputStream();
                is.close();
                return true;
            }
            catch (Throwable isEx) {
                return false;
            }
        }
    }
    /**
     * This implementation builds a URI based on the URL returned
     * by {@link #getURL()}.
     */
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        }
        catch (URISyntaxException ex) {
            throw new NestedIOException("Invalid URI [" + url + "]", ex);
        }
    }

    /**
     * This implementation checks the timestamp of the underlying File,
     * if available.
     * @see #getFile()
     */
    public long contentLength() throws IOException {
        return getFile().length();
    }

    /**
     * This implementation always returns <code>true</code>.
     */
    @Override
    public boolean isReadable() {
        return true;
    }

    /**
     * This implementation always returns <code>false</code>.
     */
    @Override
    public boolean isOpen() {
        return false;
    }

    /**
     * This implementation throws a FileNotFoundException, assuming
     * that the resource cannot be resolved to a URL.
     */
    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    /**
     * This implementation checks the timestamp of the underlying File,
     * if available.
     * @see #getFileForLastModifiedCheck()
     */
    @Override
    public long lastModified() throws IOException {
        long lastModified = getFileForLastModifiedCheck().lastModified();
        if (lastModified == 0L) {
            throw new FileNotFoundException(getDescription() +
                    " cannot be resolved in the file system for resolving its last-modified timestamp");
        }
        return lastModified;
    }

    /**
     * Determine the File to use for timestamp checking.
     * <p>The default implementation delegates to {@link #getFile()}.
     * @return the File to use for timestamp checking (never <code>null</code>)
     * @throws IOException if the resource cannot be resolved as absolute
     * file path, i.e. if the resource is not available in a file system
     */
    protected File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    /**
     * This implementation throws a FileNotFoundException, assuming
     * that the resource cannot be resolved to an absolute file path.
     */
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    /**
     * This implementation always throws IllegalStateException,
     * assuming that the resource does not carry a filename.
     */
    public String getFilename() throws IllegalStateException {
        throw new IllegalStateException(getDescription() + " does not carry a filename");
    }


    /**
     * This implementation returns the description of this resource.
     * @see #getDescription()
     */
    public String toString() {
        return getDescription();
    }

    /**
     * This implementation compares description strings.
     * @see #getDescription()
     */
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof Resource && ((Resource) obj).getDescription().equals(getDescription())));
    }

    /**
     * This implementation returns the description's hash code.
     * @see #getDescription()
     */
    public int hashCode() {
        return getDescription().hashCode();
    }

    /**
     * This implementation throws a FileNotFoundException, assuming
     * that relative resources cannot be created for this resource.
     */
    public Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
    }
}
