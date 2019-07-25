package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.web.servlet.HandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores registrations of view controllers. A view controller does nothing more than return a specified
 * view name. It saves you from having to write a controller when you want to forward the request straight
 * through to a view such as a JSP.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @since 3.1
 */
public class ViewControllerRegistry {

    private final List<ViewControllerRegistration> registrations = new ArrayList<ViewControllerRegistration>();

    private int order = 1;

    public ViewControllerRegistration addViewController(String urlPath) {
        ViewControllerRegistration registration = new ViewControllerRegistration(urlPath);
        registrations.add(registration);
        return registration;
    }

    /**
     * Specify the order to use for ViewControllers mappings relative to other {@link HandlerMapping}s
     * configured in the Spring MVC application context. The default value for view controllers is 1,
     * which is 1 higher than the value used for annotated controllers.
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Returns a handler mapping with the mapped ViewControllers; or {@code null} in case of no registrations.
     */
    protected AbstractHandlerMapping getHandlerMapping() {
        if (registrations.isEmpty()) {
            return null;
        }

        Map<String, Object> urlMap = new LinkedHashMap<String, Object>();
        for (ViewControllerRegistration registration : registrations) {
            urlMap.put(registration.getUrlPath(), registration.getViewController());
        }

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(order);
        handlerMapping.setUrlMap(urlMap);
        return handlerMapping;
    }

}
