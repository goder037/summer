package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.ClassMetadata;

/**
 * Simple facade for accessing class metadata,
 * as read by an ASM {@link org.objectweb.asm.ClassReader}.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public interface MetadataReader {

    /**
     * Read basic class metadata for the underlying class.
     */
    ClassMetadata getClassMetadata();

    /**
     * Read full annotation metadata for the underlying class.
     */
    AnnotationMetadata getAnnotationMetadata();

    /**
     * Return the resource reference for the class file.
     */
    Resource getResource();

}

