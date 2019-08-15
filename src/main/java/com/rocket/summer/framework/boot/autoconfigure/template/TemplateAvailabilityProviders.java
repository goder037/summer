package com.rocket.summer.framework.boot.autoconfigure.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.io.support.SpringFactoriesLoader;
import com.rocket.summer.framework.util.Assert;

/**
 * Collection of {@link TemplateAvailabilityProvider} beans that can be used to check
 * which (if any) templating engine supports a given view. Caches responses unless the
 * {@code spring.template.provider.cache} property is set to {@code false}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public class TemplateAvailabilityProviders {

    private final List<TemplateAvailabilityProvider> providers;

    private static final int CACHE_LIMIT = 1024;

    private static final TemplateAvailabilityProvider NONE = new NoTemplateAvailabilityProvider();

    /**
     * Resolved template views, returning already cached instances without a global lock.
     */
    private final Map<String, TemplateAvailabilityProvider> resolved = new ConcurrentHashMap<String, TemplateAvailabilityProvider>(
            CACHE_LIMIT);

    /**
     * Map from view name resolve template view, synchronized when accessed.
     */
    @SuppressWarnings("serial")
    private final Map<String, TemplateAvailabilityProvider> cache = new LinkedHashMap<String, TemplateAvailabilityProvider>(
            CACHE_LIMIT, 0.75f, true) {

        @Override
        protected boolean removeEldestEntry(
                Map.Entry<String, TemplateAvailabilityProvider> eldest) {
            if (size() > CACHE_LIMIT) {
                TemplateAvailabilityProviders.this.resolved.remove(eldest.getKey());
                return true;
            }
            return false;
        }

    };

    /**
     * Create a new {@link TemplateAvailabilityProviders} instance.
     * @param applicationContext the source application context
     */
    public TemplateAvailabilityProviders(ApplicationContext applicationContext) {
        this((applicationContext != null) ? applicationContext.getClassLoader() : null);
    }

    /**
     * Create a new {@link TemplateAvailabilityProviders} instance.
     * @param classLoader the source class loader
     */
    public TemplateAvailabilityProviders(ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.providers = SpringFactoriesLoader
                .loadFactories(TemplateAvailabilityProvider.class, classLoader);
    }

    /**
     * Create a new {@link TemplateAvailabilityProviders} instance.
     * @param providers the underlying providers
     */
    protected TemplateAvailabilityProviders(
            Collection<? extends TemplateAvailabilityProvider> providers) {
        Assert.notNull(providers, "Providers must not be null");
        this.providers = new ArrayList<TemplateAvailabilityProvider>(providers);
    }

    /**
     * Return the underlying providers being used.
     * @return the providers being used
     */
    public List<TemplateAvailabilityProvider> getProviders() {
        return this.providers;
    }

    /**
     * Get the provider that can be used to render the given view.
     * @param view the view to render
     * @param applicationContext the application context
     * @return a {@link TemplateAvailabilityProvider} or null
     */
    public TemplateAvailabilityProvider getProvider(String view,
                                                    ApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "ApplicationContext must not be null");
        return getProvider(view, applicationContext.getEnvironment(),
                applicationContext.getClassLoader(), applicationContext);
    }

    /**
     * Get the provider that can be used to render the given view.
     * @param view the view to render
     * @param environment the environment
     * @param classLoader the class loader
     * @param resourceLoader the resource loader
     * @return a {@link TemplateAvailabilityProvider} or null
     */
    public TemplateAvailabilityProvider getProvider(String view, Environment environment,
                                                    ClassLoader classLoader, ResourceLoader resourceLoader) {
        Assert.notNull(view, "View must not be null");
        Assert.notNull(environment, "Environment must not be null");
        Assert.notNull(classLoader, "ClassLoader must not be null");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");

        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(
                environment, "spring.template.provider.");
        if (!propertyResolver.getProperty("cache", Boolean.class, true)) {
            return findProvider(view, environment, classLoader, resourceLoader);
        }
        TemplateAvailabilityProvider provider = this.resolved.get(view);
        if (provider == null) {
            synchronized (this.cache) {
                provider = findProvider(view, environment, classLoader, resourceLoader);
                provider = (provider != null) ? provider : NONE;
                this.resolved.put(view, provider);
                this.cache.put(view, provider);
            }
        }
        return (provider != NONE) ? provider : null;
    }

    private TemplateAvailabilityProvider findProvider(String view,
                                                      Environment environment, ClassLoader classLoader,
                                                      ResourceLoader resourceLoader) {
        for (TemplateAvailabilityProvider candidate : this.providers) {
            if (candidate.isTemplateAvailable(view, environment, classLoader,
                    resourceLoader)) {
                return candidate;
            }
        }
        return null;
    }

    private static class NoTemplateAvailabilityProvider
            implements TemplateAvailabilityProvider {

        @Override
        public boolean isTemplateAvailable(String view, Environment environment,
                                           ClassLoader classLoader, ResourceLoader resourceLoader) {
            return false;
        }

    }

}

