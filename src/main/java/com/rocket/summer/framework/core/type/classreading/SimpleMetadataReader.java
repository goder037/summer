package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.ClassMetadata;
import org.objectweb.asm.ClassReader;

/**
 * {@link MetadataReader} implementation based on an ASM
 * {@link org.objectweb.asm.ClassReader}.
 *
 * <p>Package-visible in order to allow for repackaging the ASM library
 * without effect on users of the <code>core.type</code> package.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
class SimpleMetadataReader implements MetadataReader {

    private final ClassReader classReader;

    private final ClassLoader classLoader;


    public SimpleMetadataReader(ClassReader classReader, ClassLoader classLoader) {
        this.classReader = classReader;
        this.classLoader = classLoader;
    }


    public ClassMetadata getClassMetadata() {
        ClassMetadataReadingVisitor visitor = new ClassMetadataReadingVisitor();
        this.classReader.accept(visitor, true);
        return visitor;
    }

    public AnnotationMetadata getAnnotationMetadata() {
        AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(this.classLoader);
        this.classReader.accept(visitor, true);
        return visitor;
    }

}
