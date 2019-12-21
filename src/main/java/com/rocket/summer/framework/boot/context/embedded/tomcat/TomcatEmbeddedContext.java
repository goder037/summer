package com.rocket.summer.framework.boot.context.embedded.tomcat;

import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.catalina.Container;
import org.apache.catalina.Manager;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.session.ManagerBase;

/**
 * Tomcat {@link StandardContext} used by {@link TomcatEmbeddedServletContainer} to
 * support deferred initialization.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
class TomcatEmbeddedContext extends StandardContext {

    private TomcatStarter starter;

    private final boolean overrideLoadOnStart;

    TomcatEmbeddedContext() {
        this.overrideLoadOnStart = ReflectionUtils
                .findMethod(StandardContext.class, "loadOnStartup", Container[].class)
                .getReturnType() == boolean.class;
    }

    @Override
    public boolean loadOnStartup(Container[] children) {
        if (this.overrideLoadOnStart) {
            return true;
        }
        return super.loadOnStartup(children);
    }

    @Override
    public void setManager(Manager manager) {
        if (manager instanceof ManagerBase) {
            ((ManagerBase) manager).setSessionIdGenerator(new LazySessionIdGenerator());
        }
        super.setManager(manager);
    }

    public void deferredLoadOnStartup() {
        // Some older Servlet frameworks (e.g. Struts, BIRT) use the Thread context class
        // loader to create servlet instances in this phase. If they do that and then try
        // to initialize them later the class loader may have changed, so wrap the call to
        // loadOnStartup in what we think its going to be the main webapp classloader at
        // runtime.
        ClassLoader classLoader = getLoader().getClassLoader();
        ClassLoader existingLoader = null;
        if (classLoader != null) {
            existingLoader = ClassUtils.overrideThreadContextClassLoader(classLoader);
        }

        if (this.overrideLoadOnStart) {
            // Earlier versions of Tomcat used a version that returned void. If that
            // version is used our overridden loadOnStart method won't have been called
            // and the original will have already run.
            super.loadOnStartup(findChildren());
        }
        if (existingLoader != null) {
            ClassUtils.overrideThreadContextClassLoader(existingLoader);
        }
    }

    public void setStarter(TomcatStarter starter) {
        this.starter = starter;
    }

    public TomcatStarter getStarter() {
        return this.starter;
    }

}

