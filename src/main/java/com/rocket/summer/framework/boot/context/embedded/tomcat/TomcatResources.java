package com.rocket.summer.framework.boot.context.embedded.tomcat;

import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;

import javax.naming.directory.DirContext;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Abstraction to add resources that works with both Tomcat 8 and 7.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
abstract class TomcatResources {

    private final Context context;

    TomcatResources(Context context) {
        this.context = context;
    }

    void addResourceJars(List<URL> resourceJarUrls) {
        for (URL url : resourceJarUrls) {
            try {
                String path = url.getPath();
                if (path.endsWith(".jar") || path.endsWith(".jar!/")) {
                    String jar = url.toString();
                    if (!jar.startsWith("jar:")) {
                        // A jar file in the file system. Convert to Jar URL.
                        jar = "jar:" + jar + "!/";
                    }
                    addJar(jar);
                }
                else if ("jar".equals(url.getProtocol())) {
                    addJar(url.toString());
                }
                else {
                    addDir(new File(url.toURI()).getAbsolutePath(), url);
                }
            }
            catch (URISyntaxException ex) {
                throw new IllegalStateException(
                        "Failed to create File from URL '" + url + "'");
            }
        }
    }

    protected final Context getContext() {
        return this.context;
    }

    /**
     * Called to add a JAR to the resources.
     * @param jar the URL spec for the jar
     */
    protected abstract void addJar(String jar);

    /**
     * Called to add a dir to the resource.
     * @param dir the dir
     * @param url the URL
     */
    protected abstract void addDir(String dir, URL url);

    /**
     * Return a {@link TomcatResources} instance for the currently running Tomcat version.
     * @param context the tomcat context
     * @return a {@link TomcatResources} instance.
     */
    public static TomcatResources get(Context context) {
        if (ClassUtils.isPresent("org.apache.catalina.deploy.ErrorPage", null)) {
            return new Tomcat7Resources(context);
        }
        return new Tomcat8Resources(context);
    }

    /**
     * {@link TomcatResources} for Tomcat 7.
     */
    private static class Tomcat7Resources extends TomcatResources {

        private final Method addResourceJarUrlMethod;

        Tomcat7Resources(Context context) {
            super(context);
            this.addResourceJarUrlMethod = ReflectionUtils.findMethod(context.getClass(),
                    "addResourceJarUrl", URL.class);
        }

        @Override
        protected void addJar(String jar) {
            URL url = getJarUrl(jar);
            if (url != null) {
                try {
                    this.addResourceJarUrlMethod.invoke(getContext(), url);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }

        private URL getJarUrl(String jar) {
            try {
                return new URL(jar);
            }
            catch (MalformedURLException ex) {
                // Ignore
                return null;
            }
        }

        @Override
        protected void addDir(String dir, URL url) {
            if (getContext() instanceof StandardContext) {
                try {
                    Class<?> fileDirContextClass = Class
                            .forName("org.apache.naming.resources.FileDirContext");
                    Method setDocBaseMethod = ReflectionUtils
                            .findMethod(fileDirContextClass, "setDocBase", String.class);
                    Object fileDirContext = fileDirContextClass.newInstance();
                    setDocBaseMethod.invoke(fileDirContext, dir);
                    Method addResourcesDirContextMethod = ReflectionUtils.findMethod(
                            StandardContext.class, "addResourcesDirContext",
                            DirContext.class);
                    addResourcesDirContextMethod.invoke(getContext(), fileDirContext);
                }
                catch (Exception ex) {
                    throw new IllegalStateException("Tomcat 7 reflection failed", ex);
                }
            }
        }

    }

    /**
     * {@link TomcatResources} for Tomcat 8.
     */
    static class Tomcat8Resources extends TomcatResources {

        Tomcat8Resources(Context context) {
            super(context);
        }

        @Override
        protected void addJar(String jar) {
            addResourceSet(jar);
        }

        @Override
        protected void addDir(String dir, URL url) {
            addResourceSet(url.toString());
        }

        private void addResourceSet(String resource) {
            try {
                if (isInsideNestedJar(resource)) {
                    // It's a nested jar but we now don't want the suffix because Tomcat
                    // is going to try and locate it as a root URL (not the resource
                    // inside it)
                    resource = resource.substring(0, resource.length() - 2);
                }
                URL url = new URL(resource);
                String path = "/META-INF/resources";
                getContext().getResources().createWebResourceSet(
                        WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", url, path);
            }
            catch (Exception ex) {
                // Ignore (probably not a directory)
            }
        }

        private boolean isInsideNestedJar(String dir) {
            return dir.indexOf("!/") < dir.lastIndexOf("!/");
        }

    }

}

