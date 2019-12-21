package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.beans.BeanMetadataElement;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.Assert;

/**
 * Representation of an import that has been processed during the parsing process.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ReaderEventListener#importProcessed(ImportDefinition)
 */
public class ImportDefinition implements BeanMetadataElement {

    private final String importedResource;

    private final Resource[] actualResources;

    private final Object source;


    /**
     * Create a new ImportDefinition.
     * @param importedResource the location of the imported resource
     */
    public ImportDefinition(String importedResource) {
        this(importedResource, null, null);
    }

    /**
     * Create a new ImportDefinition.
     * @param importedResource the location of the imported resource
     * @param source the source object (may be <code>null</code>)
     */
    public ImportDefinition(String importedResource, Object source) {
        this(importedResource, null, source);
    }

    /**
     * Create a new ImportDefinition.
     * @param importedResource the location of the imported resource
     * @param source the source object (may be <code>null</code>)
     */
    public ImportDefinition(String importedResource, Resource[] actualResources, Object source) {
        Assert.notNull(importedResource, "Imported resource must not be null");
        this.importedResource = importedResource;
        this.actualResources = actualResources;
        this.source = source;
    }


    /**
     * Return the location of the imported resource.
     */
    public final String getImportedResource() {
        return this.importedResource;
    }

    public final Resource[] getActualResources() {
        return this.actualResources;
    }

    public final Object getSource() {
        return this.source;
    }

}
