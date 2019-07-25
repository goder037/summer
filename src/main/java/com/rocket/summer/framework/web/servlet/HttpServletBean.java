package com.rocket.summer.framework.web.servlet;

import com.rocket.summer.framework.beans.*;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceEditor;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.context.support.ServletContextResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple extension of {@link javax.servlet.http.HttpServlet} which treats
 * its config parameters (<code>init-param</code> entries within the
 * <code>servlet</code> tag in <code>web.xml</code>) as bean properties.
 *
 * <p>A handy superclass for any type of servlet. Type conversion of config
 * parameters is automatic, with the corresponding setter method getting
 * invoked with the converted value. It is also possible for subclasses to
 * specify required properties. Parameters without matching bean property
 * setter will simply be ignored.
 *
 * <p>This servlet leaves request handling to subclasses, inheriting the default
 * behavior of HttpServlet (<code>doGet</code>, <code>doPost</code>, etc).
 *
 * <p>This generic servlet base class has no dependency on the Spring
 * {@link org.springframework.context.ApplicationContext} concept. Simple
 * servlets usually don't load their own context but rather access service
 * beans from the Spring root application context, accessible via the
 * filter's {@link #getServletContext() ServletContext} (see
 * {@link org.springframework.web.context.support.WebApplicationContextUtils}).
 *
 * <p>The {@link FrameworkServlet} class is a more specific servlet base
 * class which loads its own application context. FrameworkServlet serves
 * as direct base class of Spring's full-fledged {@link DispatcherServlet}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #addRequiredProperty
 * @see #initServletBean
 * @see #doGet
 * @see #doPost
 */
public abstract class HttpServletBean extends HttpServlet {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Set of required properties (Strings) that must be supplied as
     * config parameters to this servlet.
     */
    private final Set<String> requiredProperties = new HashSet<String>();


    /**
     * Subclasses can invoke this method to specify that this property
     * (which must match a JavaBean property they expose) is mandatory,
     * and must be supplied as a config parameter. This should be called
     * from the constructor of a subclass.
     * <p>This method is only relevant in case of traditional initialization
     * driven by a ServletConfig instance.
     * @param property name of the required property
     */
    protected final void addRequiredProperty(String property) {
        this.requiredProperties.add(property);
    }

    /**
     * Map config parameters onto bean properties of this servlet, and
     * invoke subclass initialization.
     * @throws ServletException if bean properties are invalid (or required
     * properties are missing), or if subclass initialization fails.
     */
    @Override
    public final void init() throws ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing servlet '" + getServletName() + "'");
        }

        // Set bean properties from init parameters.
        try {
            PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
            ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
            bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader));
            initBeanWrapper(bw);
            bw.setPropertyValues(pvs, true);
        }
        catch (BeansException ex) {
            logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", ex);
            throw ex;
        }

        // Let subclasses do whatever initialization they like.
        initServletBean();

        if (logger.isDebugEnabled()) {
            logger.debug("Servlet '" + getServletName() + "' configured successfully");
        }
    }

    /**
     * Initialize the BeanWrapper for this HttpServletBean,
     * possibly with custom editors.
     * <p>This default implementation is empty.
     * @param bw the BeanWrapper to initialize
     * @throws BeansException if thrown by BeanWrapper methods
     * @see org.springframework.beans.BeanWrapper#registerCustomEditor
     */
    protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
    }


    /**
     * Overridden method that simply returns <code>null</code> when no
     * ServletConfig set yet.
     * @see #getServletConfig()
     */
    @Override
    public final String getServletName() {
        return (getServletConfig() != null ? getServletConfig().getServletName() : null);
    }

    /**
     * Overridden method that simply returns <code>null</code> when no
     * ServletConfig set yet.
     * @see #getServletConfig()
     */
    @Override
    public final ServletContext getServletContext() {
        return (getServletConfig() != null ? getServletConfig().getServletContext() : null);
    }


    /**
     * Subclasses may override this to perform custom initialization.
     * All bean properties of this servlet will have been set before this
     * method is invoked.
     * <p>This default implementation is empty.
     * @throws ServletException if subclass initialization fails
     */
    protected void initServletBean() throws ServletException {
    }


    /**
     * PropertyValues implementation created from ServletConfig init parameters.
     */
    private static class ServletConfigPropertyValues extends MutablePropertyValues {

        /**
         * Create new ServletConfigPropertyValues.
         * @param config ServletConfig we'll use to take PropertyValues from
         * @param requiredProperties set of property names we need, where
         * we can't accept default values
         * @throws ServletException if any required properties are missing
         */
        public ServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties)
                throws ServletException {

            Set<String> missingProps = (requiredProperties != null && !requiredProperties.isEmpty()) ?
                    new HashSet<String>(requiredProperties) : null;

            Enumeration en = config.getInitParameterNames();
            while (en.hasMoreElements()) {
                String property = (String) en.nextElement();
                Object value = config.getInitParameter(property);
                addPropertyValue(new PropertyValue(property, value));
                if (missingProps != null) {
                    missingProps.remove(property);
                }
            }

            // Fail if we are still missing properties.
            if (missingProps != null && missingProps.size() > 0) {
                throw new ServletException(
                        "Initialization from ServletConfig for servlet '" + config.getServletName() +
                                "' failed; the following required properties were missing: " +
                                StringUtils.collectionToDelimitedString(missingProps, ", "));
            }
        }
    }

}
