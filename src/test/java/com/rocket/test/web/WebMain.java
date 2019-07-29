package com.rocket.test.web;

import com.rocket.summer.framework.web.WebApplicationInitializer;
import com.rocket.summer.framework.web.context.support.AnnotationConfigWebApplicationContext;
import com.rocket.summer.framework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;

public class WebMain implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(Config.class);
        ctx.setServletContext(servletContext);

        Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);
    }
}
