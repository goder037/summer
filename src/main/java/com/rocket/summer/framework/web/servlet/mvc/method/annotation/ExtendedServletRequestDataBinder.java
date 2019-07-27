package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.beans.MutablePropertyValues;
import com.rocket.summer.framework.web.bind.ServletRequestDataBinder;
import com.rocket.summer.framework.web.servlet.HandlerMapping;

import javax.servlet.ServletRequest;
import java.util.Map;

/**
 * Subclass of {@link ServletRequestDataBinder} that adds URI template variables
 * to the values used for data binding.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ExtendedServletRequestDataBinder extends ServletRequestDataBinder {

    /**
     * Create a new instance, with default object name.
     * @param target the target object to bind onto (or <code>null</code>
     * if the binder is just used to convert a plain parameter value)
     * @see #DEFAULT_OBJECT_NAME
     */
    public ExtendedServletRequestDataBinder(Object target) {
        super(target);
    }

    /**
     * Create a new instance.
     * @param target the target object to bind onto (or <code>null</code>
     * if the binder is just used to convert a plain parameter value)
     * @param objectName the name of the target object
     * @see #DEFAULT_OBJECT_NAME
     */
    public ExtendedServletRequestDataBinder(Object target, String objectName) {
        super(target, objectName);
    }

    /**
     * Merge URI variables into the property values to use for data binding.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVars = (Map<String, String>) request.getAttribute(attr);
        if (uriVars != null) {
            for (Map.Entry<String, String> entry : uriVars.entrySet()) {
                if (mpvs.contains(entry.getKey())) {
                    logger.warn("Skipping URI variable '" + entry.getKey()
                            + "' since the request contains a bind value with the same name.");
                }
                else {
                    mpvs.addPropertyValue(entry.getKey(), entry.getValue());
                }
            }
        }
    }

}
