package com.rocket.summer.framework.boot.context.embedded.tomcat;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * {@link JarScanner} decorator allowing alternative default jar pattern matching. This
 * class extends {@link StandardJarScanner} rather than implementing the
 * {@link JarScanner} due to API changes introduced in Tomcat 8.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @see #apply(TomcatEmbeddedContext, Set)
 */
class SkipPatternJarScanner extends StandardJarScanner {

    private static final String JAR_SCAN_FILTER_CLASS = "org.apache.tomcat.JarScanFilter";

    private final JarScanner jarScanner;

    private final Set<String> patterns;

    SkipPatternJarScanner(JarScanner jarScanner, Set<String> patterns) {
        Assert.notNull(jarScanner, "JarScanner must not be null");
        Assert.notNull(patterns, "Patterns must not be null");
        this.jarScanner = jarScanner;
        this.patterns = patterns;
        setPatternToTomcat8SkipFilter();
    }

    private void setPatternToTomcat8SkipFilter() {
        if (ClassUtils.isPresent(JAR_SCAN_FILTER_CLASS, null)) {
            new Tomcat8TldSkipSetter(this).setSkipPattern(this.patterns);
        }
    }

    // For Tomcat 7 compatibility
    public void scan(ServletContext context, ClassLoader classloader,
                     JarScannerCallback callback, Set<String> jarsToSkip) {
        Method scanMethod = ReflectionUtils.findMethod(this.jarScanner.getClass(), "scan",
                ServletContext.class, ClassLoader.class, JarScannerCallback.class,
                Set.class);
        Assert.notNull(scanMethod, "Unable to find scan method");
        try {
            scanMethod.invoke(this.jarScanner, context, classloader, callback,
                    (jarsToSkip != null) ? jarsToSkip : this.patterns);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Tomcat 7 reflection failed", ex);
        }
    }

    /**
     * Apply this decorator the specified context.
     * @param context the context to apply to
     * @param patterns the jar skip patterns or {@code null} for defaults
     */
    static void apply(TomcatEmbeddedContext context, Set<String> patterns) {
        SkipPatternJarScanner scanner = new SkipPatternJarScanner(context.getJarScanner(),
                patterns);
        context.setJarScanner(scanner);
    }

    /**
     * Tomcat 8 specific logic to setup the scanner.
     */
    private static class Tomcat8TldSkipSetter {

        private final StandardJarScanner jarScanner;

        Tomcat8TldSkipSetter(StandardJarScanner jarScanner) {
            this.jarScanner = jarScanner;
        }

        public void setSkipPattern(Set<String> patterns) {
            StandardJarScanFilter filter = new StandardJarScanFilter();
            filter.setTldSkip(StringUtils.collectionToCommaDelimitedString(patterns));
            this.jarScanner.setJarScanFilter(filter);
        }

    }

}

