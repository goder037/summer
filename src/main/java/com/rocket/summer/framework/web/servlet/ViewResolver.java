package com.rocket.summer.framework.web.servlet;

import java.util.Locale;

/**
 * Interface to be implemented by objects that can resolve views by name.
 *
 * <p>View state doesn't change during the running of the application,
 * so implementations are free to cache views.
 *
 * <p>Implementations are encouraged to support internationalization,
 * i.e. localized view resolution.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.web.servlet.view.InternalResourceViewResolver
 * @see org.springframework.web.servlet.view.ResourceBundleViewResolver
 * @see org.springframework.web.servlet.view.XmlViewResolver
 */
public interface ViewResolver {

    /**
     * Resolve the given view by name.
     * <p>Note: To allow for ViewResolver chaining, a ViewResolver should
     * return <code>null</code> if a view with the given name is not defined in it.
     * However, this is not required: Some ViewResolvers will always attempt
     * to build View objects with the given name, unable to return <code>null</code>
     * (rather throwing an exception when View creation failed).
     * @param viewName name of the view to resolve
     * @param locale Locale in which to resolve the view.
     * ViewResolvers that support internationalization should respect this.
     * @return the View object, or <code>null</code> if not found
     * (optional, to allow for ViewResolver chaining)
     * @throws Exception if the view cannot be resolved
     * (typically in case of problems creating an actual View object)
     */
    View resolveViewName(String viewName, Locale locale) throws Exception;

}
