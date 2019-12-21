package com.rocket.summer.framework.boot.context;

import com.rocket.summer.framework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.context.annotation.ComponentScan;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.PriorityOrdered;
import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.context.ApplicationContextInitializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * {@link ApplicationContextInitializer} to report warnings for common misconfiguration
 * mistakes.
 *
 * @author Phillip Webb
 * @since 1.2.0
 */
public class ConfigurationWarningsApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Log logger = LogFactory
            .getLog(ConfigurationWarningsApplicationContextInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        context.addBeanFactoryPostProcessor(
                new ConfigurationWarningsPostProcessor(getChecks()));
    }

    /**
     * Returns the checks that should be applied.
     * @return the checks to apply
     */
    protected Check[] getChecks() {
        return new Check[] { new ComponentScanPackageCheck() };
    }

    /**
     * {@link BeanDefinitionRegistryPostProcessor} to report warnings.
     */
    protected static final class ConfigurationWarningsPostProcessor
            implements PriorityOrdered, BeanDefinitionRegistryPostProcessor {

        private Check[] checks;

        public ConfigurationWarningsPostProcessor(Check[] checks) {
            this.checks = checks;
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE - 1;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                throws BeansException {
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                throws BeansException {
            for (Check check : this.checks) {
                String message = check.getWarning(registry);
                if (StringUtils.hasLength(message)) {
                    warn(message);
                }
            }

        }

        private void warn(String message) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("%n%n** WARNING ** : %s%n%n", message));
            }
        }

    }

    /**
     * A single check that can be applied.
     */
    protected interface Check {

        /**
         * Returns a warning if the check fails or {@code null} if there are no problems.
         * @param registry the {@link BeanDefinitionRegistry}
         * @return a warning message or {@code null}
         */
        String getWarning(BeanDefinitionRegistry registry);

    }

    /**
     * {@link Check} for {@code @ComponentScan} on problematic package.
     */
    protected static class ComponentScanPackageCheck implements Check {

        private static final Set<String> PROBLEM_PACKAGES;

        static {
            Set<String> packages = new HashSet<String>();
            packages.add("com.rocket.summer.framework");
            packages.add("org");
            PROBLEM_PACKAGES = Collections.unmodifiableSet(packages);
        }

        @Override
        public String getWarning(BeanDefinitionRegistry registry) {
            Set<String> scannedPackages = getComponentScanningPackages(registry);
            List<String> problematicPackages = getProblematicPackages(scannedPackages);
            if (problematicPackages.isEmpty()) {
                return null;
            }
            return "Your ApplicationContext is unlikely to "
                    + "start due to a @ComponentScan of "
                    + StringUtils.collectionToDelimitedString(problematicPackages, ", ")
                    + ".";
        }

        protected Set<String> getComponentScanningPackages(
                BeanDefinitionRegistry registry) {
            Set<String> packages = new LinkedHashSet<String>();
            String[] names = registry.getBeanDefinitionNames();
            for (String name : names) {
                BeanDefinition definition = registry.getBeanDefinition(name);
                if (definition instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedDefinition = (AnnotatedBeanDefinition) definition;
                    addComponentScanningPackages(packages,
                            annotatedDefinition.getMetadata());
                }
            }
            return packages;
        }

        private void addComponentScanningPackages(Set<String> packages,
                                                  AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata
                    .getAnnotationAttributes(ComponentScan.class.getName(), true));
            if (attributes != null) {
                addPackages(packages, attributes.getStringArray("value"));
                addPackages(packages, attributes.getStringArray("basePackages"));
                addClasses(packages, attributes.getStringArray("basePackageClasses"));
                if (packages.isEmpty()) {
                    packages.add(ClassUtils.getPackageName(metadata.getClassName()));
                }
            }
        }

        private void addPackages(Set<String> packages, String[] values) {
            if (values != null) {
                Collections.addAll(packages, values);
            }
        }

        private void addClasses(Set<String> packages, String[] values) {
            if (values != null) {
                for (String value : values) {
                    packages.add(ClassUtils.getPackageName(value));
                }
            }
        }

        private List<String> getProblematicPackages(Set<String> scannedPackages) {
            List<String> problematicPackages = new ArrayList<String>();
            for (String scannedPackage : scannedPackages) {
                if (isProblematicPackage(scannedPackage)) {
                    problematicPackages.add(getDisplayName(scannedPackage));
                }
            }
            return problematicPackages;
        }

        private boolean isProblematicPackage(String scannedPackage) {
            if (scannedPackage == null || scannedPackage.isEmpty()) {
                return true;
            }
            return PROBLEM_PACKAGES.contains(scannedPackage);
        }

        private String getDisplayName(String scannedPackage) {
            if (scannedPackage == null || scannedPackage.isEmpty()) {
                return "the default package";
            }
            return "'" + scannedPackage + "'";
        }

    }

}
