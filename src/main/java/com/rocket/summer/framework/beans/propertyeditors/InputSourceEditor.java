package com.rocket.summer.framework.beans.propertyeditors;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceEditor;
import com.rocket.summer.framework.util.Assert;
import org.xml.sax.InputSource;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

/**
 * Editor for {@code org.xml.sax.InputSource}, converting from a
 * Spring resource location String to a SAX InputSource object.
 *
 * <p>Supports Spring-style URL notation: any fully qualified standard URL
 * ("file:", "http:", etc) and Spring's special "classpath:" pseudo-URL.
 *
 * @author Juergen Hoeller
 * @since 3.0.3
 * @see org.xml.sax.InputSource
 * @see com.rocket.summer.framework.core.io.ResourceEditor
 * @see com.rocket.summer.framework.core.io.ResourceLoader
 * @see URLEditor
 * @see FileEditor
 */
public class InputSourceEditor extends PropertyEditorSupport {

    private final ResourceEditor resourceEditor;


    /**
     * Create a new InputSourceEditor,
     * using the default ResourceEditor underneath.
     */
    public InputSourceEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    /**
     * Create a new InputSourceEditor,
     * using the given ResourceEditor underneath.
     * @param resourceEditor the ResourceEditor to use
     */
    public InputSourceEditor(ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }


    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.resourceEditor.setAsText(text);
        Resource resource = (Resource) this.resourceEditor.getValue();
        try {
            setValue(resource != null ? new InputSource(resource.getURL().toString()) : null);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(
                    "Could not retrieve URL for " + resource + ": " + ex.getMessage());
        }
    }

    @Override
    public String getAsText() {
        InputSource value = (InputSource) getValue();
        return (value != null ? value.getSystemId() : "");
    }

}

