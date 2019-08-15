package com.rocket.summer.framework.boot.diagnostics;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator;
import com.rocket.summer.framework.core.io.support.SpringFactoriesLoader;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to trigger {@link FailureAnalyzer} and {@link FailureAnalysisReporter}
 * instances loaded from {@code spring.factories}.
 * <p>
 * A {@code FailureAnalyzer} that requires access to the {@link BeanFactory} in order to
 * perform its analysis can implement {@code BeanFactoryAware} to have the
 * {@code BeanFactory} injected prior to {@link FailureAnalyzer#analyze(Throwable)} being
 * called.
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @since 1.4.0
 */
public final class FailureAnalyzers {

    private static final Log logger = LogFactory.getLog(FailureAnalyzers.class);

    private final ClassLoader classLoader;

    private final List<FailureAnalyzer> analyzers;

    /**
     * Create a new {@link FailureAnalyzers} instance.
     * @param context the source application context
     * @since 1.4.1
     */
    public FailureAnalyzers(ConfigurableApplicationContext context) {
        this(context, null);
    }

    FailureAnalyzers(ConfigurableApplicationContext context, ClassLoader classLoader) {
        Assert.notNull(context, "Context must not be null");
        this.classLoader = (classLoader != null) ? classLoader : context.getClassLoader();
        this.analyzers = loadFailureAnalyzers(this.classLoader);
        prepareFailureAnalyzers(this.analyzers, context);
    }

    private List<FailureAnalyzer> loadFailureAnalyzers(ClassLoader classLoader) {
        List<String> analyzerNames = SpringFactoriesLoader
                .loadFactoryNames(FailureAnalyzer.class, classLoader);
        List<FailureAnalyzer> analyzers = new ArrayList<FailureAnalyzer>();
        for (String analyzerName : analyzerNames) {
            try {
                Constructor<?> constructor = ClassUtils.forName(analyzerName, classLoader)
                        .getDeclaredConstructor();
                ReflectionUtils.makeAccessible(constructor);
                analyzers.add((FailureAnalyzer) constructor.newInstance());
            }
            catch (Throwable ex) {
                logger.trace("Failed to load " + analyzerName, ex);
            }
        }
        AnnotationAwareOrderComparator.sort(analyzers);
        return analyzers;
    }

    private void prepareFailureAnalyzers(List<FailureAnalyzer> analyzers,
                                         ConfigurableApplicationContext context) {
        for (FailureAnalyzer analyzer : analyzers) {
            prepareAnalyzer(context, analyzer);
        }
    }

    private void prepareAnalyzer(ConfigurableApplicationContext context,
                                 FailureAnalyzer analyzer) {
        if (analyzer instanceof BeanFactoryAware) {
            ((BeanFactoryAware) analyzer).setBeanFactory(context.getBeanFactory());
        }
    }

    /**
     * Analyze and report the specified {@code failure}.
     * @param failure the failure to analyze
     * @return {@code true} if the failure was handled
     */
    public boolean analyzeAndReport(Throwable failure) {
        FailureAnalysis analysis = analyze(failure, this.analyzers);
        return report(analysis, this.classLoader);
    }

    private FailureAnalysis analyze(Throwable failure, List<FailureAnalyzer> analyzers) {
        for (FailureAnalyzer analyzer : analyzers) {
            try {
                FailureAnalysis analysis = analyzer.analyze(failure);
                if (analysis != null) {
                    return analysis;
                }
            }
            catch (Throwable ex) {
                logger.debug("FailureAnalyzer " + analyzer + " failed", ex);
            }
        }
        return null;
    }

    private boolean report(FailureAnalysis analysis, ClassLoader classLoader) {
        List<FailureAnalysisReporter> reporters = SpringFactoriesLoader
                .loadFactories(FailureAnalysisReporter.class, classLoader);
        if (analysis == null || reporters.isEmpty()) {
            return false;
        }
        for (FailureAnalysisReporter reporter : reporters) {
            reporter.report(analysis);
        }
        return true;
    }

}
