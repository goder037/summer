package com.rocket.summer.framework.boot.autoconfigure.websocket;

import java.lang.reflect.Constructor;

import org.apache.catalina.Context;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import com.rocket.summer.framework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;

/**
 * {@link WebSocketContainerCustomizer} for {@link TomcatEmbeddedServletContainerFactory}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.2.0
 */
public class TomcatWebSocketContainerCustomizer
        extends WebSocketContainerCustomizer<TomcatEmbeddedServletContainerFactory> {

    private static final String TOMCAT_7_LISTENER_TYPE = "org.apache.catalina.deploy.ApplicationListener";

    private static final String TOMCAT_8_LISTENER_TYPE = "org.apache.tomcat.util.descriptor.web.ApplicationListener";

    private static final String WS_LISTENER = "org.apache.tomcat.websocket.server.WsContextListener";

    @Override
    public void doCustomize(TomcatEmbeddedServletContainerFactory tomcatContainer) {
        tomcatContainer.addContextCustomizers(new TomcatContextCustomizer() {
            @Override
            public void customize(Context context) {
                addListener(context, findListenerType());
            }
        });
    }

    private Class<?> findListenerType() {
        if (ClassUtils.isPresent(TOMCAT_7_LISTENER_TYPE, null)) {
            return ClassUtils.resolveClassName(TOMCAT_7_LISTENER_TYPE, null);
        }
        if (ClassUtils.isPresent(TOMCAT_8_LISTENER_TYPE, null)) {
            return ClassUtils.resolveClassName(TOMCAT_8_LISTENER_TYPE, null);
        }
        // With Tomcat 8.0.8 ApplicationListener is not required
        return null;
    }

    /**
     * Instead of registering the WsSci directly as a ServletContainerInitializer, we use
     * the ApplicationListener provided by Tomcat. Unfortunately the ApplicationListener
     * class moved packages in Tomcat 8 and been deleted in 8.0.8 so we have to use
     * reflection.
     * @param context the current context
     * @param listenerType the type of listener to add
     */
    private void addListener(Context context, Class<?> listenerType) {
        Class<? extends Context> contextClass = context.getClass();
        if (listenerType == null) {
            ReflectionUtils.invokeMethod(ClassUtils.getMethod(contextClass,
                    "addApplicationListener", String.class), context, WS_LISTENER);

        }
        else {
            Constructor<?> constructor = ClassUtils
                    .getConstructorIfAvailable(listenerType, String.class, boolean.class);
            Object instance = BeanUtils.instantiateClass(constructor, WS_LISTENER, false);
            ReflectionUtils.invokeMethod(ClassUtils.getMethod(contextClass,
                    "addApplicationListener", listenerType), context, instance);
        }
    }

}

