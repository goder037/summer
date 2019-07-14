package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceEditor;
import com.rocket.summer.framework.util.Assert;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

/**
 * One-way PropertyEditor, which can convert from a text string to a
 * <code>java.io.InputStream</code>, allowing InputStream properties
 * to be set directly as a text string.
 *
 * <p>Supports Spring-style URL notation: any fully qualified standard URL
 * ("file:", "http:", etc) and Spring's special "classpath:" pseudo-URL.
 *
 * <p>Note that in the default usage, the stream is not closed by Spring itself!
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 * @see java.io.InputStream
 * @see org.springframework.core.io.ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see URLEditor
 * @see FileEditor
 */
public class InputStreamEditor extends PropertyEditorSupport {

    private final ResourceEditor resourceEditor;


    /**
     * Create a new InputStreamEditor,
     * using the default ResourceEditor underneath.
     */
    public InputStreamEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    /**
     * Create a new InputStreamEditor,
     * using the given ResourceEditor underneath.
     * @param resourceEditor the ResourceEditor to use
     */
    public InputStreamEditor(ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }


    public void setAsText(String text) throws IllegalArgumentException {
        this.resourceEditor.setAsText(text);
        Resource resource = (Resource) this.resourceEditor.getValue();
        try {
            setValue(resource != null ? resource.getInputStream() : null);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(
                    "Could not retrieve InputStream for " + resource + ": " + ex.getMessage());
        }
    }

    /**
     * This implementation returns <code>null</code> to indicate that
     * there is no appropriate text representation.
     */
    public String getAsText() {
        return null;
    }

}
