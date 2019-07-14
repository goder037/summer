package com.rocket.summer.framework.core;

import com.rocket.summer.framework.util.Assert;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Base class for decorating ClassLoaders such as {@link OverridingClassLoader}
 * and {@link org.springframework.instrument.classloading.ShadowingClassLoader},
 * providing common handling of excluded packages and classes.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 2.5.2
 */
public abstract class DecoratingClassLoader extends ClassLoader {

    private final Set excludedPackages = new HashSet();

    private final Set excludedClasses = new HashSet();

    private final Object exclusionMonitor = new Object();


    /**
     * Create a new DecoratingClassLoader with no parent ClassLoader.
     */
    public DecoratingClassLoader() {
    }

    /**
     * Create a new DecoratingClassLoader using the given parent ClassLoader
     * for delegation.
     */
    public DecoratingClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Determine whether the specified class is excluded from decoration
     * by this class loader.
     * <p>The default implementation checks against excluded packages and classes.
     * @param className the class name to check
     * @return whether the specified class is eligible
     * @see #excludePackage
     * @see #excludeClass
     */
    protected boolean isExcluded(String className) {
        synchronized (this.exclusionMonitor) {
            if (this.excludedClasses.contains(className)) {
                return true;
            }
            for (Iterator it = this.excludedPackages.iterator(); it.hasNext();) {
                String packageName = (String) it.next();
                if (className.startsWith(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add a package name to exclude from decoration (e.g. overriding).
     * <p>Any class whose fully-qualified name starts with the name registered
     * here will be handled by the parent ClassLoader in the usual fashion.
     * @param packageName the package name to exclude
     */
    public void excludePackage(String packageName) {
        Assert.notNull(packageName, "Package name must not be null");
        synchronized (this.exclusionMonitor) {
            this.excludedPackages.add(packageName);
        }
    }

    /**
     * Add a class name to exclude from decoration (e.g. overriding).
     * <p>Any class name registered here will be handled by the parent
     * ClassLoader in the usual fashion.
     * @param className the class name to exclude
     */
    public void excludeClass(String className) {
        Assert.notNull(className, "Class name must not be null");
        synchronized (this.exclusionMonitor) {
            this.excludedClasses.add(className);
        }
    }
}
