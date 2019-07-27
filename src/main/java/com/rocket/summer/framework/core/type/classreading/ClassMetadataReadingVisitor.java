package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.type.ClassMetadata;
import com.rocket.summer.framework.util.ClassUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * ASM class visitor which looks only for the class name and implemented types,
 * exposing them through the {@link com.rocket.summer.framework.core.type.ClassMetadata}
 * interface.
 *
 * @author Rod Johnson
 * @author Costin Leau
 * @author Mark Fisher
 * @author Ramnivas Laddad
 * @since 2.5
 */
class ClassMetadataReadingVisitor extends EmptyVisitor implements ClassMetadata {

    private String className;

    private boolean isInterface;

    private boolean isAbstract;

    private boolean isFinal;

    private String enclosingClassName;

    private boolean independentInnerClass;

    private String superClassName;

    private String[] interfaces;


    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
        this.className = ClassUtils.convertResourcePathToClassName(name);
        this.isInterface = ((access & Opcodes.ACC_INTERFACE) != 0);
        this.isAbstract = ((access & Opcodes.ACC_ABSTRACT) != 0);
        this.isFinal = ((access & Opcodes.ACC_FINAL) != 0);
        if (supername != null) {
            this.superClassName = ClassUtils.convertResourcePathToClassName(supername);
        }
        this.interfaces = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            this.interfaces[i] = ClassUtils.convertResourcePathToClassName(interfaces[i]);
        }
    }

    public void visitOuterClass(String owner, String name, String desc) {
        this.enclosingClassName = ClassUtils.convertResourcePathToClassName(owner);
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (outerName != null && this.className.equals(ClassUtils.convertResourcePathToClassName(name))) {
            this.enclosingClassName = ClassUtils.convertResourcePathToClassName(outerName);
            this.independentInnerClass = ((access & Opcodes.ACC_STATIC) != 0);
        }
    }


    public String getClassName() {
        return this.className;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public boolean isConcrete() {
        return !(this.isInterface || this.isAbstract);
    }

    public boolean isIndependent() {
        return (this.enclosingClassName == null || this.independentInnerClass);
    }

    public boolean hasEnclosingClass() {
        return (this.enclosingClassName != null);
    }

    public String getEnclosingClassName() {
        return this.enclosingClassName;
    }

    public boolean hasSuperClass() {
        return (this.superClassName != null);
    }

    public String getSuperClassName() {
        return this.superClassName;
    }

    public String[] getInterfaceNames() {
        return this.interfaces;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

}

