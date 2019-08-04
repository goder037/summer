package com.rocket.summer.framework.expression.spel;

import com.rocket.summer.framework.core.SpringProperties;

/**
 * Configuration object for the SpEL expression parser.
 *
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Andy Clement
 * @since 3.0
 * @see com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser#SpelExpressionParser(SpelParserConfiguration)
 */
public class SpelParserConfiguration {

    private static final SpelCompilerMode defaultCompilerMode;

    static {
        String compilerMode = SpringProperties.getProperty("spring.expression.compiler.mode");
        defaultCompilerMode = (compilerMode != null ?
                SpelCompilerMode.valueOf(compilerMode.toUpperCase()) : SpelCompilerMode.OFF);
    }


    private final SpelCompilerMode compilerMode;

    private final ClassLoader compilerClassLoader;

    private final boolean autoGrowNullReferences;

    private final boolean autoGrowCollections;

    private final int maximumAutoGrowSize;


    /**
     * Create a new {@code SpelParserConfiguration} instance with default settings.
     */
    public SpelParserConfiguration() {
        this(null, null, false, false, Integer.MAX_VALUE);
    }

    /**
     * Create a new {@code SpelParserConfiguration} instance.
     * @param compilerMode the compiler mode for the parser
     * @param compilerClassLoader the ClassLoader to use as the basis for expression compilation
     */
    public SpelParserConfiguration(SpelCompilerMode compilerMode, ClassLoader compilerClassLoader) {
        this(compilerMode, compilerClassLoader, false, false, Integer.MAX_VALUE);
    }

    /**
     * Create a new {@code SpelParserConfiguration} instance.
     * @param autoGrowNullReferences if null references should automatically grow
     * @param autoGrowCollections if collections should automatically grow
     * @see #SpelParserConfiguration(boolean, boolean, int)
     */
    public SpelParserConfiguration(boolean autoGrowNullReferences, boolean autoGrowCollections) {
        this(null, null, autoGrowNullReferences, autoGrowCollections, Integer.MAX_VALUE);
    }

    /**
     * Create a new {@code SpelParserConfiguration} instance.
     * @param autoGrowNullReferences if null references should automatically grow
     * @param autoGrowCollections if collections should automatically grow
     * @param maximumAutoGrowSize the maximum size that the collection can auto grow
     */
    public SpelParserConfiguration(boolean autoGrowNullReferences, boolean autoGrowCollections, int maximumAutoGrowSize) {
        this(null, null, autoGrowNullReferences, autoGrowCollections, maximumAutoGrowSize);
    }

    /**
     * Create a new {@code SpelParserConfiguration} instance.
     * @param compilerMode the compiler mode that parsers using this configuration object should use
     * @param compilerClassLoader the ClassLoader to use as the basis for expression compilation
     * @param autoGrowNullReferences if null references should automatically grow
     * @param autoGrowCollections if collections should automatically grow
     * @param maximumAutoGrowSize the maximum size that the collection can auto grow
     */
    public SpelParserConfiguration(SpelCompilerMode compilerMode, ClassLoader compilerClassLoader,
                                   boolean autoGrowNullReferences, boolean autoGrowCollections, int maximumAutoGrowSize) {

        this.compilerMode = (compilerMode != null ? compilerMode : defaultCompilerMode);
        this.compilerClassLoader = compilerClassLoader;
        this.autoGrowNullReferences = autoGrowNullReferences;
        this.autoGrowCollections = autoGrowCollections;
        this.maximumAutoGrowSize = maximumAutoGrowSize;
    }


    /**
     * Return the configuration mode for parsers using this configuration object.
     */
    public SpelCompilerMode getCompilerMode() {
        return this.compilerMode;
    }

    /**
     * Return the ClassLoader to use as the basis for expression compilation.
     */
    public ClassLoader getCompilerClassLoader() {
        return this.compilerClassLoader;
    }

    /**
     * Return {@code true} if {@code null} references should be automatically grown.
     */
    public boolean isAutoGrowNullReferences() {
        return this.autoGrowNullReferences;
    }

    /**
     * Return {@code true} if collections should be automatically grown.
     */
    public boolean isAutoGrowCollections() {
        return this.autoGrowCollections;
    }

    /**
     * Return the maximum size that a collection can auto grow.
     */
    public int getMaximumAutoGrowSize() {
        return this.maximumAutoGrowSize;
    }

}

