package com.rocket.summer.framework.core.type;

import java.lang.reflect.Modifier;

/**
 * {@link ClassMetadata} implementation that uses standard reflection
 * to introspect a given <code>Class</code>.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public class StandardClassMetadata implements ClassMetadata {

    private final Class introspectedClass;


    public StandardClassMetadata(Class introspectedClass) {
        this.introspectedClass = introspectedClass;
    }

    public final Class getIntrospectedClass() {
        return this.introspectedClass;
    }


    public String getClassName() {
        return getIntrospectedClass().getName();
    }

    public boolean isInterface() {
        return getIntrospectedClass().isInterface();
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getIntrospectedClass().getModifiers());
    }

    public boolean isConcrete() {
        return !(isInterface() || isAbstract());
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedClass.getModifiers());
    }

    public boolean isIndependent() {
        return (!hasEnclosingClass() ||
                (getIntrospectedClass().getDeclaringClass() != null &&
                        Modifier.isStatic(getIntrospectedClass().getModifiers())));
    }

    public boolean hasEnclosingClass() {
        return (getIntrospectedClass().getEnclosingClass() != null);
    }

    public String getEnclosingClassName() {
        Class enclosingClass = getIntrospectedClass().getEnclosingClass();
        return (enclosingClass != null ? enclosingClass.getName() : null);
    }

    public boolean hasSuperClass() {
        return (getIntrospectedClass().getSuperclass() != null);
    }

    public String getSuperClassName() {
        Class superClass = getIntrospectedClass().getSuperclass();
        return (superClass != null ? superClass.getName() : null);
    }

    public String[] getInterfaceNames() {
        Class[] ifcs = getIntrospectedClass().getInterfaces();
        String[] ifcNames = new String[ifcs.length];
        for (int i = 0; i < ifcs.length; i++) {
            ifcNames[i] = ifcs[i].getName();
        }
        return ifcNames;
    }

}
