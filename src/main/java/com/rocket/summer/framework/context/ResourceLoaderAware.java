package com.rocket.summer.framework.context;

import com.rocket.summer.framework.core.io.ResourceLoader;

/**
 * Interface to be implemented by any object that wishes to be notified of
 * the <b>ResourceLoader</b> (typically the ApplicationContext) that it runs in.
 * This is an alternative to a full ApplicationContext dependency via the
 * ApplicationContextAware interface.
 *
 * <p>Note that Resource dependencies can also be exposed as bean properties
 * of type Resource, populated via Strings with automatic type conversion by
 * the bean factory. This removes the need for implementing any callback
 * interface just for the purpose of accessing a specific file resource.
 *
 * <p>You typically need a ResourceLoader when your application object has
 * to access a variety of file resources whose names are calculated. A good
 * strategy is to make the object use a DefaultResourceLoader but still
 * implement ResourceLoaderAware to allow for overriding when running in an
 * ApplicationContext. See ReloadableResourceBundleMessageSource for an example.
 *
 * <p>A passed-in ResourceLoader can also be checked for the
 * <b>ResourcePatternResolver</b> interface and cast accordingly, to be able
 * to resolve resource patterns into arrays of Resource objects. This will always
 * work when running in an ApplicationContext (the context interface extends
 * ResourcePatternResolver). Use a PathMatchingResourcePatternResolver as default.
 * See also the <code>ResourcePatternUtils.getResourcePatternResolver</code> method.
 *
 * <p>As alternative to a ResourcePatternResolver dependency, consider exposing
 * bean properties of type Resource array, populated via pattern Strings with
 * automatic type conversion by the bean factory.
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see ApplicationContextAware
 * @see com.rocket.summer.framework.beans.factory.InitializingBean
 * @see com.rocket.summer.framework.core.io.Resource
 * @see com.rocket.summer.framework.core.io.support.ResourcePatternResolver
 * @see com.rocket.summer.framework.core.io.support.ResourcePatternUtils#getResourcePatternResolver
 * @see com.rocket.summer.framework.core.io.DefaultResourceLoader
 * @see com.rocket.summer.framework.core.io.support.PathMatchingResourcePatternResolver
 * @see com.rocket.summer.framework.context.support.ReloadableResourceBundleMessageSource
 */
public interface ResourceLoaderAware {

    /**
     * Set the ResourceLoader that this object runs in.
     * <p>This might be a ResourcePatternResolver, which can be checked
     * through <code>instanceof ResourcePatternResolver</code>. See also the
     * <code>ResourcePatternUtils.getResourcePatternResolver</code> method.
     * <p>Invoked after population of normal bean properties but before an init callback
     * like InitializingBean's <code>afterPropertiesSet</code> or a custom init-method.
     * Invoked before ApplicationContextAware's <code>setApplicationContext</code>.
     * @param resourceLoader ResourceLoader object to be used by this object
     * @see com.rocket.summer.framework.core.io.support.ResourcePatternResolver
     * @see com.rocket.summer.framework.core.io.support.ResourcePatternUtils#getResourcePatternResolver
     */
    void setResourceLoader(ResourceLoader resourceLoader);

}

