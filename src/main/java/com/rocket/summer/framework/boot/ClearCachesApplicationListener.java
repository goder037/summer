package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.event.ContextRefreshedEvent;
import com.rocket.summer.framework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * {@link ApplicationListener} to cleanup caches once the context is loaded.
 *
 * @author Phillip Webb
 */
class ClearCachesApplicationListener
        implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ReflectionUtils.clearCache();
        clearClassLoaderCaches(Thread.currentThread().getContextClassLoader());
    }

    private void clearClassLoaderCaches(ClassLoader classLoader) {
        if (classLoader == null) {
            return;
        }
        try {
            Method clearCacheMethod = classLoader.getClass()
                    .getDeclaredMethod("clearCache");
            clearCacheMethod.invoke(classLoader);
        }
        catch (Exception ex) {
            // Ignore
        }
        clearClassLoaderCaches(classLoader.getParent());
    }

}

