package com.rocket.summer.framework.cache.annotation;

import java.util.ArrayList;
import java.util.List;

import com.rocket.summer.framework.context.annotation.AdviceMode;
import com.rocket.summer.framework.context.annotation.AdviceModeImportSelector;
import com.rocket.summer.framework.context.annotation.AutoProxyRegistrar;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Selects which implementation of {@link AbstractCachingConfiguration} should
 * be used based on the value of {@link EnableCaching#mode} on the importing
 * {@code @Configuration} class.
 *
 * <p>Detects the presence of JSR-107 and enables JCache support accordingly.
 *
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1
 * @see EnableCaching
 * @see ProxyCachingConfiguration
 */
public class CachingConfigurationSelector extends AdviceModeImportSelector<EnableCaching> {

    private static final String PROXY_JCACHE_CONFIGURATION_CLASS =
            "com.rocket.summer.framework.cache.jcache.config.ProxyJCacheConfiguration";

    private static final String CACHE_ASPECT_CONFIGURATION_CLASS_NAME =
            "com.rocket.summer.framework.cache.aspectj.AspectJCachingConfiguration";

    private static final String JCACHE_ASPECT_CONFIGURATION_CLASS_NAME =
            "com.rocket.summer.framework.cache.aspectj.AspectJJCacheConfiguration";


    private static final boolean jsr107Present = ClassUtils.isPresent(
            "javax.cache.Cache", CachingConfigurationSelector.class.getClassLoader());

    private static final boolean jcacheImplPresent = ClassUtils.isPresent(
            PROXY_JCACHE_CONFIGURATION_CLASS, CachingConfigurationSelector.class.getClassLoader());


    /**
     * Returns {@link ProxyCachingConfiguration} or {@code AspectJCachingConfiguration}
     * for {@code PROXY} and {@code ASPECTJ} values of {@link EnableCaching#mode()},
     * respectively. Potentially includes corresponding JCache configuration as well.
     */
    @Override
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return getProxyImports();
            case ASPECTJ:
                return getAspectJImports();
            default:
                return null;
        }
    }

    /**
     * Return the imports to use if the {@link AdviceMode} is set to {@link AdviceMode#PROXY}.
     * <p>Take care of adding the necessary JSR-107 import if it is available.
     */
    private String[] getProxyImports() {
        List<String> result = new ArrayList<String>(3);
        result.add(AutoProxyRegistrar.class.getName());
        result.add(ProxyCachingConfiguration.class.getName());
        if (jsr107Present && jcacheImplPresent) {
            result.add(PROXY_JCACHE_CONFIGURATION_CLASS);
        }
        return StringUtils.toStringArray(result);
    }

    /**
     * Return the imports to use if the {@link AdviceMode} is set to {@link AdviceMode#ASPECTJ}.
     * <p>Take care of adding the necessary JSR-107 import if it is available.
     */
    private String[] getAspectJImports() {
        List<String> result = new ArrayList<String>(2);
        result.add(CACHE_ASPECT_CONFIGURATION_CLASS_NAME);
        if (jsr107Present && jcacheImplPresent) {
            result.add(JCACHE_ASPECT_CONFIGURATION_CLASS_NAME);
        }
        return StringUtils.toStringArray(result);
    }

}

