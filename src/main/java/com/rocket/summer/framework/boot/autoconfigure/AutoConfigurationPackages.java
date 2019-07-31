package com.rocket.summer.framework.boot.autoconfigure;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConstructorArgumentValues;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.GenericBeanDefinition;
import com.rocket.summer.framework.boot.context.annotation.DeterminableImports;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.Order;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Class for storing auto-configuration packages for reference later (e.g. by JPA entity
 * scanner).
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Oliver Gierke
 */
public abstract class AutoConfigurationPackages {

    private static final Log logger = LogFactory.getLog(AutoConfigurationPackages.class);

    private static final String BEAN = AutoConfigurationPackages.class.getName();

    /**
     * Determine if the auto-configuration base packages for the given bean factory are
     * available.
     * @param beanFactory the source bean factory
     * @return true if there are auto-config packages available
     */
    public static boolean has(BeanFactory beanFactory) {
        return beanFactory.containsBean(BEAN) && !get(beanFactory).isEmpty();
    }

    /**
     * Return the auto-configuration base packages for the given bean factory.
     * @param beanFactory the source bean factory
     * @return a list of auto-configuration packages
     * @throws IllegalStateException if auto-configuration is not enabled
     */
    public static List<String> get(BeanFactory beanFactory) {
        try {
            return beanFactory.getBean(BEAN, BasePackages.class).get();
        }
        catch (NoSuchBeanDefinitionException ex) {
            throw new IllegalStateException(
                    "Unable to retrieve @EnableAutoConfiguration base packages");
        }
    }

    /**
     * Programmatically registers the auto-configuration package names. Subsequent
     * invocations will add the given package names to those that have already been
     * registered. You can use this method to manually define the base packages that will
     * be used for a given {@link BeanDefinitionRegistry}. Generally it's recommended that
     * you don't call this method directly, but instead rely on the default convention
     * where the package name is set from your {@code @EnableAutoConfiguration}
     * configuration class or classes.
     * @param registry the bean definition registry
     * @param packageNames the package names to set
     */
    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        if (registry.containsBeanDefinition(BEAN)) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
            ConstructorArgumentValues constructorArguments = beanDefinition
                    .getConstructorArgumentValues();
            constructorArguments.addIndexedArgumentValue(0,
                    addBasePackages(constructorArguments, packageNames));
        }
        else {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(BasePackages.class);
            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,
                    packageNames);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(BEAN, beanDefinition);
        }
    }

    private static String[] addBasePackages(
            ConstructorArgumentValues constructorArguments, String[] packageNames) {
        String[] existing = (String[]) constructorArguments
                .getIndexedArgumentValue(0, String[].class).getValue();
        Set<String> merged = new LinkedHashSet<String>();
        merged.addAll(Arrays.asList(existing));
        merged.addAll(Arrays.asList(packageNames));
        return merged.toArray(new String[merged.size()]);
    }

    /**
     * {@link ImportBeanDefinitionRegistrar} to store the base package from the importing
     * configuration.
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata,
                                            BeanDefinitionRegistry registry) {
            register(registry, new PackageImport(metadata).getPackageName());
        }

        @Override
        public Set<Object> determineImports(AnnotationMetadata metadata) {
            return Collections.<Object>singleton(new PackageImport(metadata));
        }

    }

    /**
     * Wrapper for a package import.
     */
    private static final class PackageImport {

        private final String packageName;

        PackageImport(AnnotationMetadata metadata) {
            this.packageName = ClassUtils.getPackageName(metadata.getClassName());
        }

        public String getPackageName() {
            return this.packageName;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.packageName.equals(((PackageImport) obj).packageName);
        }

        @Override
        public int hashCode() {
            return this.packageName.hashCode();
        }

        @Override
        public String toString() {
            return "Package Import " + this.packageName;
        }

    }

    /**
     * Holder for the base package (name may be null to indicate no scanning).
     */
    static final class BasePackages {

        private final List<String> packages;

        private boolean loggedBasePackageInfo;

        BasePackages(String... names) {
            List<String> packages = new ArrayList<String>();
            for (String name : names) {
                if (StringUtils.hasText(name)) {
                    packages.add(name);
                }
            }
            this.packages = packages;
        }

        public List<String> get() {
            if (!this.loggedBasePackageInfo) {
                if (this.packages.isEmpty()) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("@EnableAutoConfiguration was declared on a class "
                                + "in the default package. Automatic @Repository and "
                                + "@Entity scanning is not enabled.");
                    }
                }
                else {
                    if (logger.isDebugEnabled()) {
                        String packageNames = StringUtils
                                .collectionToCommaDelimitedString(this.packages);
                        logger.debug("@EnableAutoConfiguration was declared on a class "
                                + "in the package '" + packageNames
                                + "'. Automatic @Repository and @Entity scanning is "
                                + "enabled.");
                    }
                }
                this.loggedBasePackageInfo = true;
            }
            return this.packages;
        }

    }

}
