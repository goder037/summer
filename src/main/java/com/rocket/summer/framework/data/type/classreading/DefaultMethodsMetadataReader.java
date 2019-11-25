package com.rocket.summer.framework.data.type.classreading;

import lombok.Getter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.rocket.summer.framework.core.NestedIOException;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.ClassMetadata;
import com.rocket.summer.framework.core.type.MethodMetadata;
import com.rocket.summer.framework.core.type.classreading.AnnotationMetadataReadingVisitor;
import com.rocket.summer.framework.core.type.classreading.MethodMetadataReadingVisitor;
import com.rocket.summer.framework.data.type.MethodsMetadata;
import com.rocket.summer.framework.util.Assert;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * {@link MethodsMetadataReader} implementation based on an ASM .
 *
 * @author Mark Paluch
 * @auhtor Oliver Gierke
 * @since 2.1
 * @since 1.11.11
 */
@Getter
class DefaultMethodsMetadataReader implements MethodsMetadataReader {

    private final Resource resource;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;
    private final MethodsMetadata methodsMetadata;

    DefaultMethodsMetadataReader(Resource resource, ClassLoader classLoader) throws IOException {

        this.resource = resource;

        ClassReader classReader = null;
        InputStream is = null;

        try {

            is = new BufferedInputStream(getResource().getInputStream());
            classReader = new ClassReader(is);

        } catch (IllegalArgumentException ex) {

            throw new NestedIOException("ASM ClassReader failed to parse class file - "
                    + "probably due to a new Java class file version that isn't supported yet: " + getResource(), ex);

        } finally {

            if (is != null) {
                is.close();
            }
        }

        MethodsMetadataReadingVisitor visitor = new MethodsMetadataReadingVisitor(classLoader);
        classReader.accept(visitor, ClassReader.SKIP_DEBUG);

        classMetadata = visitor;
        annotationMetadata = visitor;
        methodsMetadata = visitor;
    }

    /**
     * ASM class visitor which looks for the class name and implemented types as well as for the methods defined in the
     * class, exposing them through the {@link MethodsMetadata} interface.
     *
     * @author Mark Paluch
     * @since 2.1
     * @since 1.11.11
     * @see ClassMetadata
     * @see MethodMetadata
     * @see MethodMetadataReadingVisitor
     */
    static class MethodsMetadataReadingVisitor extends AnnotationMetadataReadingVisitor implements MethodsMetadata {

        /**
         * Construct a new {@link MethodsMetadataReadingVisitor} given {@link ClassLoader}.
         *
         * @param classLoader may be {@literal null}.
         */
        MethodsMetadataReadingVisitor(ClassLoader classLoader) {
            super(classLoader);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.core.type.classreading.AnnotationMetadataReadingVisitor#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
         */
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            // Skip bridge methods - we're only interested in original user methods.
            // On JDK 8, we'd otherwise run into double detection of the same method...
            if ((access & Opcodes.ACC_BRIDGE) != 0) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }

            // Skip constructors
            if (name.equals("<init>")) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }

            MethodMetadataReadingVisitor visitor = new MethodMetadataReadingVisitor(name, access, getClassName(),
                    Type.getReturnType(desc).getClassName(), this.classLoader, this.methodMetadataSet);

            this.methodMetadataSet.add(visitor);
            return visitor;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.util.MethodsMetadata#getMethods()
         */
        @Override
        public Set<MethodMetadata> getMethods() {
            return Collections.unmodifiableSet(methodMetadataSet);
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.util.MethodsMetadata#getMethods(String)
         */
        @Override
        public Set<MethodMetadata> getMethods(String name) {

            Assert.hasText(name, "Method name must not be null or empty");

            Set<MethodMetadata> result = new LinkedHashSet<MethodMetadata>(4);

            for (MethodMetadata metadata : methodMetadataSet) {
                if (metadata.getMethodName().equals(name)) {
                    result.add(metadata);
                }
            }

            return Collections.unmodifiableSet(result);
        }
    }
}

