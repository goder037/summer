package com.rocket.summer.framework.boot.web.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanFactoryPostProcessor;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.context.annotation.ClassPathScanningCandidateComponentProvider;
import com.rocket.summer.framework.context.annotation.ScannedGenericBeanDefinition;
import com.rocket.summer.framework.web.context.WebApplicationContext;

/**
 * {@link BeanFactoryPostProcessor} that registers beans for Servlet components found via
 * package scanning.
 *
 * @author Andy Wilkinson
 * @see ServletComponentScan
 * @see ServletComponentScanRegistrar
 */
class ServletComponentRegisteringPostProcessor
        implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static final List<ServletComponentHandler> HANDLERS;

    static {
        List<ServletComponentHandler> servletComponentHandlers = new ArrayList<ServletComponentHandler>();
        servletComponentHandlers.add(new WebServletHandler());
        servletComponentHandlers.add(new WebFilterHandler());
        servletComponentHandlers.add(new WebListenerHandler());
        HANDLERS = Collections.unmodifiableList(servletComponentHandlers);
    }

    private final Set<String> packagesToScan;

    private ApplicationContext applicationContext;

    ServletComponentRegisteringPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        if (isRunningInEmbeddedContainer()) {
            ClassPathScanningCandidateComponentProvider componentProvider = createComponentProvider();
            for (String packageToScan : this.packagesToScan) {
                scanPackage(componentProvider, packageToScan);
            }
        }
    }

    private void scanPackage(
            ClassPathScanningCandidateComponentProvider componentProvider,
            String packageToScan) {
        for (BeanDefinition candidate : componentProvider
                .findCandidateComponents(packageToScan)) {
            if (candidate instanceof ScannedGenericBeanDefinition) {
                for (ServletComponentHandler handler : HANDLERS) {
                    handler.handle(((ScannedGenericBeanDefinition) candidate),
                            (BeanDefinitionRegistry) this.applicationContext);
                }
            }
        }
    }

    private boolean isRunningInEmbeddedContainer() {
        return this.applicationContext instanceof WebApplicationContext
                && ((WebApplicationContext) this.applicationContext)
                .getServletContext() == null;
    }

    private ClassPathScanningCandidateComponentProvider createComponentProvider() {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
                false);
        componentProvider.setEnvironment(this.applicationContext.getEnvironment());
        componentProvider.setResourceLoader(this.applicationContext);
        for (ServletComponentHandler handler : HANDLERS) {
            componentProvider.addIncludeFilter(handler.getTypeFilter());
        }
        return componentProvider;
    }

    Set<String> getPackagesToScan() {
        return Collections.unmodifiableSet(this.packagesToScan);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

}

