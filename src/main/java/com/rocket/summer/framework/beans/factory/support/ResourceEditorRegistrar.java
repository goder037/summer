package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.PropertyEditorRegistrar;
import com.rocket.summer.framework.beans.PropertyEditorRegistry;
import com.rocket.summer.framework.beans.PropertyEditorRegistrySupport;
import com.rocket.summer.framework.beans.propertyeditors.*;
import com.rocket.summer.framework.core.env.PropertyResolver;
import com.rocket.summer.framework.core.io.ContextResource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceEditor;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.io.support.ResourceArrayPropertyEditor;
import com.rocket.summer.framework.core.io.support.ResourcePatternResolver;
import com.rocket.summer.framework.util.ClassUtils;
import org.xml.sax.InputSource;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

/**
 * PropertyEditorRegistrar implementation that populates a given
 * {@link com.rocket.summer.framework.beans.PropertyEditorRegistry}
 * (typically a {@link com.rocket.summer.framework.beans.BeanWrapper} used for bean
 * creation within an {@link com.rocket.summer.framework.context.ApplicationContext})
 * with resource editors. Used by
 * {@link com.rocket.summer.framework.context.support.AbstractApplicationContext}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.0
 */
public class ResourceEditorRegistrar implements PropertyEditorRegistrar {

    private static Class<?> pathClass;

    static {
        try {
            pathClass = ClassUtils.forName("java.nio.file.Path", ResourceEditorRegistrar.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            // Java 7 Path class not available
            pathClass = null;
        }
    }


    private final PropertyResolver propertyResolver;

    private final ResourceLoader resourceLoader;


    /**
     * Create a new ResourceEditorRegistrar for the given {@link ResourceLoader}
     * and {@link PropertyResolver}.
     * @param resourceLoader the ResourceLoader (or ResourcePatternResolver)
     * to create editors for (usually an ApplicationContext)
     * @param propertyResolver the PropertyResolver (usually an Environment)
     * @see com.rocket.summer.framework.core.env.Environment
     * @see com.rocket.summer.framework.core.io.support.ResourcePatternResolver
     * @see com.rocket.summer.framework.context.ApplicationContext
     */
    public ResourceEditorRegistrar(ResourceLoader resourceLoader, PropertyResolver propertyResolver) {
        this.resourceLoader = resourceLoader;
        this.propertyResolver = propertyResolver;
    }


    /**
     * Populate the given {@code registry} with the following resource editors:
     * ResourceEditor, InputStreamEditor, InputSourceEditor, FileEditor, URLEditor,
     * URIEditor, ClassEditor, ClassArrayEditor.
     * <p>If this registrar has been configured with a {@link ResourcePatternResolver},
     * a ResourceArrayPropertyEditor will be registered as well.
     * @see com.rocket.summer.framework.core.io.ResourceEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.InputStreamEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.InputSourceEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.FileEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.URLEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.URIEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.ClassEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.ClassArrayEditor
     * @see com.rocket.summer.framework.core.io.support.ResourceArrayPropertyEditor
     */
    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        ResourceEditor baseEditor = new ResourceEditor(this.resourceLoader, this.propertyResolver);
        doRegisterEditor(registry, Resource.class, baseEditor);
        doRegisterEditor(registry, ContextResource.class, baseEditor);
        doRegisterEditor(registry, InputStream.class, new InputStreamEditor(baseEditor));
        doRegisterEditor(registry, InputSource.class, new InputSourceEditor(baseEditor));
        doRegisterEditor(registry, File.class, new FileEditor(baseEditor));
        if (pathClass != null) {
            doRegisterEditor(registry, pathClass, new PathEditor(baseEditor));
        }
        doRegisterEditor(registry, Reader.class, new ReaderEditor(baseEditor));
        doRegisterEditor(registry, URL.class, new URLEditor(baseEditor));

        ClassLoader classLoader = this.resourceLoader.getClassLoader();
        doRegisterEditor(registry, URI.class, new URIEditor(classLoader));
        doRegisterEditor(registry, Class.class, new ClassEditor(classLoader));
        doRegisterEditor(registry, Class[].class, new ClassArrayEditor(classLoader));

        if (this.resourceLoader instanceof ResourcePatternResolver) {
            doRegisterEditor(registry, Resource[].class,
                    new ResourceArrayPropertyEditor((ResourcePatternResolver) this.resourceLoader, this.propertyResolver));
        }
    }

    /**
     * Override default editor, if possible (since that's what we really mean to do here);
     * otherwise register as a custom editor.
     */
    private void doRegisterEditor(PropertyEditorRegistry registry, Class<?> requiredType, PropertyEditor editor) {
        if (registry instanceof PropertyEditorRegistrySupport) {
            ((PropertyEditorRegistrySupport) registry).overrideDefaultEditor(requiredType, editor);
        }
        else {
            registry.registerCustomEditor(requiredType, editor);
        }
    }

}
