package com.rocket.summer.framework.core.type.filter;

import com.rocket.summer.framework.core.type.ClassMetadata;
import com.rocket.summer.framework.core.type.classreading.MetadataReader;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * Type filter that is aware of traversing over hierarchy.
 *
 * <p>This filter is useful when matching needs to be made based on potentially the
 * whole class/interface hierarchy. The algorithm employed uses succeed-fast
 * strategy i.e. if at anytime a match is declared, no further processing is
 * carried out.
 *
 * @author Ramnivas Laddad
 * @author Mark Fisher
 * @since 2.5
 */
public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter {

    private final boolean considerInherited;

    private final boolean considerInterfaces;


    protected AbstractTypeHierarchyTraversingFilter(boolean considerInherited, boolean considerInterfaces) {
        this.considerInherited = considerInherited;
        this.considerInterfaces = considerInterfaces;
    }


    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException {

        // This method optimizes avoiding unnecessary creation of ClassReaders
        // as well as visiting over those readers.
        if (matchSelf(metadataReader)) {
            return true;
        }
        ClassMetadata metadata = metadataReader.getClassMetadata();
        if (matchClassName(metadata.getClassName())) {
            return true;
        }

        if (!this.considerInherited) {
            return false;
        }
        if (metadata.hasSuperClass()) {
            // Optimization to avoid creating ClassReader for super class.
            Boolean superClassMatch = matchSuperClass(metadata.getSuperClassName());
            if (superClassMatch != null) {
                if (superClassMatch.booleanValue()) {
                    return true;
                }
            }
            else {
                // Need to read super class to determine a match...
                if (match(metadata.getSuperClassName(), metadataReaderFactory)) {
                    return true;
                }
            }
        }

        if (!this.considerInterfaces) {
            return false;
        }
        for (String ifc : metadata.getInterfaceNames()) {
            // Optimization to avoid creating ClassReader for super class
            Boolean interfaceMatch = matchInterface(ifc);
            if (interfaceMatch != null) {
                if (interfaceMatch.booleanValue()) {
                    return true;
                }
            }
            else {
                // Need to read interface to determine a match...
                if (match(ifc, metadataReaderFactory)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean match(String className, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return match(metadataReaderFactory.getMetadataReader(className), metadataReaderFactory);
    }

    /**
     * Override this to match self characteristics alone. Typically,
     * the implementation will use a visitor to extract information
     * to perform matching.
     */
    protected boolean matchSelf(MetadataReader metadataReader) {
        return false;
    }

    /**
     * Override this to match on type name.
     */
    protected boolean matchClassName(String className) {
        return false;
    }

    /**
     * Override this to match on super type name.
     */
    protected Boolean matchSuperClass(String superClassName) {
        return null;
    }

    /**
     * Override this to match on interface type name.
     */
    protected Boolean matchInterface(String interfaceNames) {
        return null;
    }

}

