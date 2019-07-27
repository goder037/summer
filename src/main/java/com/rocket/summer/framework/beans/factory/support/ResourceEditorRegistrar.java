package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.PropertyEditorRegistrar;
import com.rocket.summer.framework.beans.PropertyEditorRegistry;
import com.rocket.summer.framework.beans.propertyeditors.*;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceEditor;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.io.support.ResourceArrayPropertyEditor;
import com.rocket.summer.framework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.InputStream;
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
 * @since 2.0
 */
public class ResourceEditorRegistrar implements PropertyEditorRegistrar {

    private final ResourceLoader resourceLoader;


    /**
     * Create a new ResourceEditorRegistrar for the given ResourceLoader
     * @param resourceLoader the ResourceLoader (or ResourcePatternResolver)
     * to create editors for (usually an ApplicationContext)
     * @see com.rocket.summer.framework.core.io.support.ResourcePatternResolver
     * @see com.rocket.summer.framework.context.ApplicationContext
     */
    public ResourceEditorRegistrar(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    /**
     * Populate the given bean factory with the following resource editors:
     * ResourceEditor, InputStreamEditor, FileEditor, URLEditor, ClassEditor, URIEditor.
     * <p>In case of a {@link com.rocket.summer.framework.core.io.support.ResourcePatternResolver},
     * a ResourceArrayPropertyEditor will be registered as well.
     * @see com.rocket.summer.framework.core.io.ResourceEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.InputStreamEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.FileEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.URLEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.ClassEditor
     * @see com.rocket.summer.framework.beans.propertyeditors.URIEditor
     * @see com.rocket.summer.framework.core.io.support.ResourceArrayPropertyEditor
     */
    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        ResourceEditor baseEditor = new ResourceEditor(this.resourceLoader);
        registry.registerCustomEditor(Resource.class, baseEditor);
        registry.registerCustomEditor(InputStream.class, new InputStreamEditor(baseEditor));
        registry.registerCustomEditor(File.class, new FileEditor(baseEditor));
        registry.registerCustomEditor(URL.class, new URLEditor(baseEditor));

        ClassLoader classLoader = this.resourceLoader.getClassLoader();
        registry.registerCustomEditor(Class.class, new ClassEditor(classLoader));
        registry.registerCustomEditor(URI.class, new URIEditor(classLoader));

        if (this.resourceLoader instanceof ResourcePatternResolver) {
            registry.registerCustomEditor(Resource[].class,
                    new ResourceArrayPropertyEditor((ResourcePatternResolver) this.resourceLoader));
        }
    }

}
