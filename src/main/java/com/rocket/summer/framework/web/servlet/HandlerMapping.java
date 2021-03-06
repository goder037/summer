package com.rocket.summer.framework.web.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to be implemented by objects that define a mapping between
 * requests and handler objects.
 *
 * <p>This class can be implemented by application developers, although this is not
 * necessary, as {@link com.rocket.summer.framework.web.servlet.handler.BeanNameUrlHandlerMapping}
 * and {@link com.rocket.summer.framework.web.servlet.handler.SimpleUrlHandlerMapping}
 * are included in the framework. The former is the default if no
 * HandlerMapping bean is registered in the application context.
 *
 * <p>HandlerMapping implementations can support mapped interceptors but do not
 * have to. A handler will always be wrapped in a {@link HandlerExecutionChain}
 * instance, optionally accompanied by some {@link HandlerInterceptor} instances.
 * The DispatcherServlet will first call each HandlerInterceptor's
 * <code>preHandle</code> method in the given order, finally invoking the handler
 * itself if all <code>preHandle</code> methods have returned <code>true</code>.
 *
 * <p>The ability to parameterize this mapping is a powerful and unusual
 * capability of this MVC framework. For example, it is possible to write
 * a custom mapping based on session state, cookie state or many other
 * variables. No other MVC framework seems to be equally flexible.
 *
 * <p>Note: Implementations can implement the {@link com.rocket.summer.framework.core.Ordered}
 * interface to be able to specify a sorting order and thus a priority for getting
 * applied by DispatcherServlet. Non-Ordered instances get treated as lowest priority.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.rocket.summer.framework.core.Ordered
 * @see com.rocket.summer.framework.web.servlet.handler.AbstractHandlerMapping
 * @see com.rocket.summer.framework.web.servlet.handler.BeanNameUrlHandlerMapping
 * @see com.rocket.summer.framework.web.servlet.handler.SimpleUrlHandlerMapping
 */
public interface HandlerMapping {

    /**
     * Name of the {@link HttpServletRequest} attribute that contains the path
     * within the handler mapping, in case of a pattern match, or the full
     * relevant URI (typically within the DispatcherServlet's mapping) else.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations. URL-based HandlerMappings will
     * typically support it, but handlers should not necessarily expect
     * this request attribute to be present in all scenarios.
     */
    String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = HandlerMapping.class.getName() + ".pathWithinHandlerMapping";

    /**
     * Name of the {@link HttpServletRequest} attribute that contains the set of producible MediaTypes
     * applicable to the mapped handler.
     * <p>Note: This attribute is not required to be supported by all HandlerMapping implementations.
     * Handlers should not necessarily expect this request attribute to be present in all scenarios.
     */
    String PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE = HandlerMapping.class.getName() + ".producibleMediaTypes";

    /**
     * Name of the boolean {@link HttpServletRequest} attribute that indicates
     * whether type-level mappings should be inspected.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations.
     */
    String INTROSPECT_TYPE_LEVEL_MAPPING = HandlerMapping.class.getName() + ".introspectTypeLevelMapping";

    /**
     * Name of the {@link HttpServletRequest} attribute that contains the
     * best matching pattern within the handler mapping.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations. URL-based HandlerMappings will
     * typically support it, but handlers should not necessarily expect
     * this request attribute to be present in all scenarios.
     */
    String BEST_MATCHING_PATTERN_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingPattern";

    /**
     * Name of the {@link HttpServletRequest} attribute that contains the URI
     * templates map, mapping variable names to values.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations. URL-based HandlerMappings will
     * typically support it, but handlers should not necessarily expect
     * this request attribute to be present in all scenarios.
     */
    String URI_TEMPLATE_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".uriTemplateVariables";

    /**
     * Return a handler and any interceptors for this request. The choice may be made
     * on request URL, session state, or any factor the implementing class chooses.
     * <p>The returned HandlerExecutionChain contains a handler Object, rather than
     * even a tag interface, so that handlers are not constrained in any way.
     * For example, a HandlerAdapter could be written to allow another framework's
     * handler objects to be used.
     * <p>Returns <code>null</code> if no match was found. This is not an error.
     * The DispatcherServlet will query all registered HandlerMapping beans to find
     * a match, and only decide there is an error if none can find a handler.
     * @param request current HTTP request
     * @return a HandlerExecutionChain instance containing handler object and
     * any interceptors, or <code>null</code> if no mapping found
     * @throws Exception if there is an internal error
     */
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

}

