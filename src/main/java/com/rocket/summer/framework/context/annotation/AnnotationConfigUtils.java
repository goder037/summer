package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.rocket.summer.framework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.rocket.summer.framework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.util.ClassUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Utility class that allows for convenient registration of common
 * {@link com.rocket.summer.framework.beans.factory.config.BeanPostProcessor} and
 * {@link com.rocket.summer.framework.beans.factory.config.BeanFactoryPostProcessor}
 * definitions for annotation-based configuration.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.5
 * @see CommonAnnotationBeanPostProcessor
 * @see com.rocket.summer.framework.context.annotation.ConfigurationClassPostProcessor;
 * @see com.rocket.summer.framework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 * @see com.rocket.summer.framework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor
 */
public class AnnotationConfigUtils {

    /**
     * The bean name of the internally managed Configuration annotation processor.
     */
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.rocket.summer.framework.context.annotation.internalConfigurationAnnotationProcessor";

    /**
     * The bean name of the internally managed BeanNameGenerator for use when processing
     * {@link Configuration} classes. Set by {@link AnnotationConfigApplicationContext}
     * and {@code AnnotationConfigWebApplicationContext} during bootstrap in order to make
     * any custom name generation strategy available to the underlying
     * {@link ConfigurationClassPostProcessor}.
     * @since 3.1.1
     */
    public static final String CONFIGURATION_BEAN_NAME_GENERATOR =
            "org.springframework.context.annotation.internalConfigurationBeanNameGenerator";

    /**
     * The bean name of the internally managed Autowired annotation processor.
     */
    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.rocket.summer.framework.context.annotation.internalAutowiredAnnotationProcessor";

    /**
     * The bean name of the internally managed Required annotation processor.
     */
    public static final String REQUIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.rocket.summer.framework.context.annotation.internalRequiredAnnotationProcessor";

    /**
     * The bean name of the internally managed JSR-250 annotation processor.
     */
    public static final String COMMON_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.rocket.summer.framework.context.annotation.internalCommonAnnotationProcessor";

    /**
     * The bean name of the internally managed JPA annotation processor.
     */
    public static final String PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME =
            "com.rocket.summer.framework.context.annotation.internalPersistenceAnnotationProcessor";


    private static final String PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME =
            "com.rocket.summer.framework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor";


    private static final boolean jsr250Present =
            ClassUtils.isPresent("javax.annotation.Resource", AnnotationConfigUtils.class.getClassLoader());

    private static final boolean jpaPresent =
            ClassUtils.isPresent("javax.persistence.EntityManagerFactory", AnnotationConfigUtils.class.getClassLoader()) &&
                    ClassUtils.isPresent(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, AnnotationConfigUtils.class.getClassLoader());


    /**
     * Register all relevant annotation post processors in the given registry.
     * @param registry the registry to operate on
     */
    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        registerAnnotationConfigProcessors(registry, null);
    }

    /**
     * Register all relevant annotation post processors in the given registry.
     * @param registry the registry to operate on
     * @param source the configuration source element (already extracted)
     * that this registration was triggered from. May be <code>null</code>.
     * @return a Set of BeanDefinitionHolders, containing all bean definitions
     * that have actually been registered by this call
     */
    public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
            BeanDefinitionRegistry registry, Object source) {

        Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<BeanDefinitionHolder>(4);

        if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
        }

        if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        }

        if (!registry.containsBeanDefinition(REQUIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(RequiredAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, REQUIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        }

        // Check for JSR-250 support, and if present add the CommonAnnotationBeanPostProcessor.
        if (jsr250Present && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        }

        // Check for JPA support, and if present add the PersistenceAnnotationBeanPostProcessor.
        if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition();
            try {
                ClassLoader cl = AnnotationConfigUtils.class.getClassLoader();
                def.setBeanClass(cl.loadClass(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME));
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalStateException(
                        "Cannot load optional framework class: " + PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, ex);
            }
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
        }

        return beanDefs;
    }

    private static BeanDefinitionHolder registerPostProcessor(
            BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {

        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(beanName, definition);
        return new BeanDefinitionHolder(definition, beanName);
    }

    static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        if (abd.getMetadata().isAnnotated(Primary.class.getName())) {
            abd.setPrimary(true);
        }
        if (abd.getMetadata().isAnnotated(Lazy.class.getName())) {
            Boolean value = (Boolean) abd.getMetadata().getAnnotationAttributes(Lazy.class.getName()).get("value");
            abd.setLazyInit(value);
        }
        if (abd.getMetadata().isAnnotated(DependsOn.class.getName())) {
            String[] value = (String[]) abd.getMetadata().getAnnotationAttributes(DependsOn.class.getName()).get("value");
            abd.setDependsOn(value);
        }
    }

    static BeanDefinitionHolder applyScopedProxyMode(
            ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {

        ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
        if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        }
        boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
        return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
    }

}

