package com.rocket.summer.framework.data.type.classreading;

import com.rocket.summer.framework.core.type.classreading.MetadataReader;
import com.rocket.summer.framework.data.type.MethodsMetadata;

/**
 * Extension to {@link MetadataReader} for accessing class metadata and method metadata as read by an ASM
 *
 * @author Mark Paluch
 * @since 2.1
 * @since 1.11.11
 */
public interface MethodsMetadataReader extends MetadataReader {

    /**
     * @return the {@link MethodsMetadata} for methods in the class file.
     */
    MethodsMetadata getMethodsMetadata();
}

