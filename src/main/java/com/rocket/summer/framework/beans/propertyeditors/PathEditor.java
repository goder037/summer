package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceEditor;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.util.Assert;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Editor for {@code java.nio.file.Path}, to directly populate a Path
 * property instead of using a String property as bridge.
 *
 * <p>Based on {@link Paths#get(URI)}'s resolution algorithm, checking
 * registered NIO file system providers, including the default file system
 * for "file:..." paths. Also supports Spring-style URL notation: any fully
 * qualified standard URL and Spring's special "classpath:" pseudo-URL, as
 * well as Spring's context-specific relative file paths. As a fallback, a
 * path will be resolved in the file system via {@code Paths#get(String)}
 * if no existing context-relative resource could be found.
 *
 * @author Juergen Hoeller
 * @since 4.3.2
 * @see java.nio.file.Path
 * @see Paths#get(URI)
 * @see ResourceEditor
 * @see com.rocket.summer.framework.core.io.ResourceLoader
 * @see FileEditor
 * @see URLEditor
 */
public class PathEditor extends PropertyEditorSupport {

    private final ResourceEditor resourceEditor;


    /**
     * Create a new PathEditor, using the default ResourceEditor underneath.
     */
    public PathEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    /**
     * Create a new PathEditor, using the given ResourceEditor underneath.
     * @param resourceEditor the ResourceEditor to use
     */
    public PathEditor(ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }


    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        boolean nioPathCandidate = !text.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX);
        if (nioPathCandidate && !text.startsWith("/")) {
            try {
                URI uri = new URI(text);
                if (uri.getScheme() != null) {
                    nioPathCandidate = false;
                    // Let's try NIO file system providers via Paths.get(URI)
                    setValue(Paths.get(uri).normalize());
                    return;
                }
            }
            catch (URISyntaxException ex) {
                // Not a valid URI: Let's try as Spring resource location.
            }
            catch (FileSystemNotFoundException ex) {
                // URI scheme not registered for NIO:
                // Let's try URL protocol handlers via Spring's resource mechanism.
            }
        }

        this.resourceEditor.setAsText(text);
        Resource resource = (Resource) this.resourceEditor.getValue();
        if (resource == null) {
            setValue(null);
        }
        else if (!resource.exists() && nioPathCandidate) {
            setValue(Paths.get(text).normalize());
        }
        else {
            try {
                setValue(resource.getFile().toPath());
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("Failed to retrieve file for " + resource, ex);
            }
        }
    }

    @Override
    public String getAsText() {
        Path value = (Path) getValue();
        return (value != null ? value.toString() : "");
    }

}
