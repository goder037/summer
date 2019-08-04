package com.rocket.summer.framework.expression.spel.support;

import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypeLocator;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;
import com.rocket.summer.framework.util.ClassUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple implementation of {@link TypeLocator} that uses the context ClassLoader
 * (or any ClassLoader set upon it). It supports 'well-known' packages: So if a
 * type cannot be found, it will try the registered imports to locate it.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class StandardTypeLocator implements TypeLocator {

    private final ClassLoader classLoader;

    private final List<String> knownPackagePrefixes = new LinkedList<String>();


    /**
     * Create a StandardTypeLocator for the default ClassLoader
     * (typically, the thread context ClassLoader).
     */
    public StandardTypeLocator() {
        this(ClassUtils.getDefaultClassLoader());
    }

    /**
     * Create a StandardTypeLocator for the given ClassLoader.
     * @param classLoader the ClassLoader to delegate to
     */
    public StandardTypeLocator(ClassLoader classLoader) {
        this.classLoader = classLoader;
        // Similar to when writing regular Java code, it only knows about java.lang by default
        registerImport("java.lang");
    }


    /**
     * Register a new import prefix that will be used when searching for unqualified types.
     * Expected format is something like "java.lang".
     * @param prefix the prefix to register
     */
    public void registerImport(String prefix) {
        this.knownPackagePrefixes.add(prefix);
    }

    /**
     * Remove that specified prefix from this locator's list of imports.
     * @param prefix the prefix to remove
     */
    public void removeImport(String prefix) {
        this.knownPackagePrefixes.remove(prefix);
    }

    /**
     * Return a list of all the import prefixes registered with this StandardTypeLocator.
     * @return a list of registered import prefixes
     */
    public List<String> getImportPrefixes() {
        return Collections.unmodifiableList(this.knownPackagePrefixes);
    }


    /**
     * Find a (possibly unqualified) type reference - first using the type name as-is,
     * then trying any registered prefixes if the type name cannot be found.
     * @param typeName the type to locate
     * @return the class object for the type
     * @throws EvaluationException if the type cannot be found
     */
    @Override
    public Class<?> findType(String typeName) throws EvaluationException {
        String nameToLookup = typeName;
        try {
            return ClassUtils.forName(nameToLookup, this.classLoader);
        }
        catch (ClassNotFoundException ey) {
            // try any registered prefixes before giving up
        }
        for (String prefix : this.knownPackagePrefixes) {
            try {
                nameToLookup = prefix + '.' + typeName;
                return ClassUtils.forName(nameToLookup, this.classLoader);
            }
            catch (ClassNotFoundException ex) {
                // might be a different prefix
            }
        }
        throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
    }

}

