package com.rocket.test.web;

import com.rocket.summer.framework.web.context.request.RequestContextListener;
import com.rocket.summer.framework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

public class WebProjectConfigInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {

        initializeSpringConfig(container);

        initializeLog4jConfig(container);

        initializeSpringMVCConfig(container);

        registerServlet(container);

        registerListener(container);

        registerFilter(container);
    }

    private void initializeSpringConfig(ServletContext container) {
        // Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfiguration.class);
        // Manage the life cycle of the root application context
        container.addListener(new ContextLoaderListener(rootContext));
    }

    private void initializeSpringMVCConfig(ServletContext container) {
        // Create the spring rest servlet's Spring application context
        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(RestServiceConfiguration.class);

        // Register and map the spring rest servlet
        ServletRegistration.Dynamic dispatcher = container.addServlet("SpringMvc",
                new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(2);
        dispatcher.setAsyncSupported(true);
        dispatcher.addMapping("/springmvc/*");
    }

    private void initializeLog4jConfig(ServletContext container) {
        // Log4jConfigListener
        container.setInitParameter("log4jConfigLocation", "file:${rdm.home}/log4j.properties");
        container.addListener(Log4jConfigListener.class);
        PropertyConfigurator.configureAndWatch(System.getProperty("rdm.home") + "/log4j.properties", 60);
    }

    private void registerServlet(ServletContext container) {

        initializeStaggingServlet(container);
    }

    private void registerFilter(ServletContext container) {
        initializeSAMLFilter(container);
    }

    private void registerListener(ServletContext container) {
        container.addListener(RequestContextListener.class);
    }

    private void initializeSAMLFilter(ServletContext container) {
        FilterRegistration.Dynamic filterRegistration = container.addFilter("SAMLFilter", DelegatingFilterProxy.class);
        filterRegistration.addMappingForUrlPatterns(null, false, "/*");
        filterRegistration.setAsyncSupported(true);
    }

    private void initializeStaggingServlet(ServletContext container) {
        StaggingServlet staggingServlet = new StaggingServlet();
        ServletRegistration.Dynamic dynamic = container.addServlet("staggingServlet", staggingServlet);
        dynamic.setLoadOnStartup(3);
        dynamic.addMapping("*.stagging");
    }
}
