package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.LocalVariableTableParameterNameDiscoverer;
import com.rocket.summer.framework.core.ParameterNameDiscoverer;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.http.converter.ByteArrayHttpMessageConverter;
import com.rocket.summer.framework.http.converter.HttpMessageConverter;
import com.rocket.summer.framework.http.converter.StringHttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.SourceHttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import com.rocket.summer.framework.web.bind.annotation.RequestMapping;
import com.rocket.summer.framework.web.bind.support.DefaultSessionAttributeStore;
import com.rocket.summer.framework.web.bind.support.SessionAttributeStore;
import com.rocket.summer.framework.web.bind.support.WebBindingInitializer;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.ServletWebRequest;
import com.rocket.summer.framework.web.context.request.WebRequest;
import com.rocket.summer.framework.web.method.HandlerMethod;
import com.rocket.summer.framework.web.method.HandlerMethodSelector;
import com.rocket.summer.framework.web.method.annotation.*;
import com.rocket.summer.framework.web.method.support.*;
import com.rocket.summer.framework.web.servlet.ModelAndView;
import com.rocket.summer.framework.web.servlet.View;
import com.rocket.summer.framework.web.servlet.mvc.annotation.ModelAndViewResolver;
import com.rocket.summer.framework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import com.rocket.summer.framework.web.servlet.mvc.support.RedirectAttributes;
import com.rocket.summer.framework.web.servlet.support.RequestContextUtils;
import com.rocket.summer.framework.web.ui.ModelMap;
import com.rocket.summer.framework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An {@link AbstractHandlerMethodAdapter} that supports {@link HandlerMethod}s
 * with the signature -- method argument and return types, defined in
 * {@code @RequestMapping}.
 *
 * <p>Support for custom argument and return value types can be added via
 * {@link #setCustomArgumentResolvers} and {@link #setCustomReturnValueHandlers}.
 * Or alternatively to re-configure all argument and return value types use
 * {@link #setArgumentResolvers} and {@link #setReturnValueHandlers(List)}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see HandlerMethodArgumentResolver
 * @see HandlerMethodReturnValueHandler
 */
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter implements BeanFactoryAware,
        InitializingBean {

    private List<HandlerMethodArgumentResolver> customArgumentResolvers;

    private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;

    private List<ModelAndViewResolver> modelAndViewResolvers;

    private List<HttpMessageConverter<?>> messageConverters;

    private WebBindingInitializer webBindingInitializer;

    private int cacheSecondsForSessionAttributeHandlers = 0;

    private boolean synchronizeOnSession = false;

    private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private ConfigurableBeanFactory beanFactory;

    private SessionAttributeStore sessionAttributeStore = new DefaultSessionAttributeStore();

    private boolean ignoreDefaultModelOnRedirect = false;

    private final Map<Class<?>, SessionAttributesHandler> sessionAttributesHandlerCache =
            new ConcurrentHashMap<Class<?>, SessionAttributesHandler>();

    private HandlerMethodArgumentResolverComposite argumentResolvers;

    private HandlerMethodArgumentResolverComposite initBinderArgumentResolvers;

    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    private final Map<Class<?>, Set<Method>> dataBinderFactoryCache = new ConcurrentHashMap<Class<?>, Set<Method>>();

    private final Map<Class<?>, Set<Method>> modelFactoryCache = new ConcurrentHashMap<Class<?>, Set<Method>>();

    /**
     * Default constructor.
     */
    public RequestMappingHandlerAdapter() {

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false); // See SPR-7316

        this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(stringHttpMessageConverter);
        this.messageConverters.add(new SourceHttpMessageConverter<Source>());
        this.messageConverters.add(new XmlAwareFormHttpMessageConverter());
    }

    /**
     * Provide resolvers for custom argument types. Custom resolvers are ordered
     * after built-in ones. To override the built-in support for argument
     * resolution use {@link #setArgumentResolvers} instead.
     */
    public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.customArgumentResolvers = argumentResolvers;
    }

    /**
     * Return the custom argument resolvers, or {@code null}.
     */
    public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
        return this.customArgumentResolvers;
    }

    /**
     * Configure the complete list of supported argument types thus overriding
     * the resolvers that would otherwise be configured by default.
     */
    public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (argumentResolvers == null) {
            this.argumentResolvers = null;
        }
        else {
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.argumentResolvers.addResolvers(argumentResolvers);
        }
    }

    /**
     * Return the configured argument resolvers, or possibly {@code null} if
     * not initialized yet via {@link #afterPropertiesSet()}.
     */
    public HandlerMethodArgumentResolverComposite getArgumentResolvers() {
        return this.argumentResolvers;
    }

    /**
     * Configure the supported argument types in {@code @InitBinder} methods.
     */
    public void setInitBinderArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (argumentResolvers == null) {
            this.initBinderArgumentResolvers = null;
        }
        else {
            this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.initBinderArgumentResolvers.addResolvers(argumentResolvers);
        }
    }

    /**
     * Return the argument resolvers for {@code @InitBinder} methods, or possibly
     * {@code null} if not initialized yet via {@link #afterPropertiesSet()}.
     */
    public HandlerMethodArgumentResolverComposite getInitBinderArgumentResolvers() {
        return this.initBinderArgumentResolvers;
    }

    /**
     * Provide handlers for custom return value types. Custom handlers are
     * ordered after built-in ones. To override the built-in support for
     * return value handling use {@link #setReturnValueHandlers}.
     */
    public void setCustomReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        this.customReturnValueHandlers = returnValueHandlers;
    }

    /**
     * Return the custom return value handlers, or {@code null}.
     */
    public List<HandlerMethodReturnValueHandler> getCustomReturnValueHandlers() {
        return this.customReturnValueHandlers;
    }

    /**
     * Configure the complete list of supported return value types thus
     * overriding handlers that would otherwise be configured by default.
     */
    public void setReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        if (returnValueHandlers == null) {
            this.returnValueHandlers = null;
        }
        else {
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
            this.returnValueHandlers.addHandlers(returnValueHandlers);
        }
    }

    /**
     * Return the configured handlers, or possibly {@code null} if not
     * initialized yet via {@link #afterPropertiesSet()}.
     */
    public HandlerMethodReturnValueHandlerComposite getReturnValueHandlers() {
        return this.returnValueHandlers;
    }

    /**
     * Provide custom {@link ModelAndViewResolver}s.
     * <p><strong>Note:</strong> This method is available for backwards
     * compatibility only. However, it is recommended to re-write a
     * {@code ModelAndViewResolver} as {@link HandlerMethodReturnValueHandler}.
     * An adapter between the two interfaces is not possible since the
     * {@link HandlerMethodReturnValueHandler#supportsReturnType} method
     * cannot be implemented. Hence {@code ModelAndViewResolver}s are limited
     * to always being invoked at the end after all other return value
     * handlers have been given a chance.
     * <p>A {@code HandlerMethodReturnValueHandler} provides better access to
     * the return type and controller method information and can be ordered
     * freely relative to other return value handlers.
     */
    public void setModelAndViewResolvers(List<ModelAndViewResolver> modelAndViewResolvers) {
        this.modelAndViewResolvers = modelAndViewResolvers;
    }

    /**
     * Return the configured {@link ModelAndViewResolver}s, or {@code null}.
     */
    public List<ModelAndViewResolver> getModelAndViewResolvers() {
        return modelAndViewResolvers;
    }

    /**
     * Provide the converters to use in argument resolvers and return value
     * handlers that support reading and/or writing to the body of the
     * request and response.
     */
    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * Return the configured message body converters.
     */
    public List<HttpMessageConverter<?>> getMessageConverters() {
        return messageConverters;
    }

    /**
     * Provide a WebBindingInitializer with "global" initialization to apply
     * to every DataBinder instance.
     */
    public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
        this.webBindingInitializer = webBindingInitializer;
    }

    /**
     * Return the configured WebBindingInitializer, or {@code null}.
     */
    public WebBindingInitializer getWebBindingInitializer() {
        return webBindingInitializer;
    }

    /**
     * Specify the strategy to store session attributes with. The default is
     * {@link org.springframework.web.bind.support.DefaultSessionAttributeStore},
     * storing session attributes in the HttpSession with the same attribute
     * name as in the model.
     */
    public void setSessionAttributeStore(SessionAttributeStore sessionAttributeStore) {
        this.sessionAttributeStore = sessionAttributeStore;
    }

    /**
     * Cache content produced by <code>@SessionAttributes</code> annotated handlers
     * for the given number of seconds. Default is 0, preventing caching completely.
     * <p>In contrast to the "cacheSeconds" property which will apply to all general
     * handlers (but not to <code>@SessionAttributes</code> annotated handlers),
     * this setting will apply to <code>@SessionAttributes</code> handlers only.
     * @see #setCacheSeconds
     * @see org.springframework.web.bind.annotation.SessionAttributes
     */
    public void setCacheSecondsForSessionAttributeHandlers(int cacheSecondsForSessionAttributeHandlers) {
        this.cacheSecondsForSessionAttributeHandlers = cacheSecondsForSessionAttributeHandlers;
    }

    /**
     * Set if controller execution should be synchronized on the session,
     * to serialize parallel invocations from the same client.
     * <p>More specifically, the execution of the <code>handleRequestInternal</code>
     * method will get synchronized if this flag is "true". The best available
     * session mutex will be used for the synchronization; ideally, this will
     * be a mutex exposed by HttpSessionMutexListener.
     * <p>The session mutex is guaranteed to be the same object during
     * the entire lifetime of the session, available under the key defined
     * by the <code>SESSION_MUTEX_ATTRIBUTE</code> constant. It serves as a
     * safe reference to synchronize on for locking on the current session.
     * <p>In many cases, the HttpSession reference itself is a safe mutex
     * as well, since it will always be the same object reference for the
     * same active logical session. However, this is not guaranteed across
     * different servlet containers; the only 100% safe way is a session mutex.
     * @see org.springframework.web.util.HttpSessionMutexListener
     * @see org.springframework.web.util.WebUtils#getSessionMutex(javax.servlet.http.HttpSession)
     */
    public void setSynchronizeOnSession(boolean synchronizeOnSession) {
        this.synchronizeOnSession = synchronizeOnSession;
    }

    /**
     * Set the ParameterNameDiscoverer to use for resolving method parameter
     * names if needed (e.g. for default attribute names). Default is a
     * {@link org.springframework.core.LocalVariableTableParameterNameDiscoverer}.
     */
    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    /**
     * By default the content of the "default" model is used both during
     * rendering and redirect scenarios. Alternatively a controller method
     * can declare a {@link RedirectAttributes} argument and use it to provide
     * attributes for a redirect.
     * <p>Setting this flag to {@code true} guarantees the "default" model is
     * never used in a redirect scenario even if a RedirectAttributes argument
     * is not declared. Setting it to {@code false} means the "default" model
     * may be used in a redirect if the controller method doesn't declare a
     * RedirectAttributes argument.
     * <p>The default setting is {@code false} but new applications should
     * consider setting it to {@code true}.
     * @see RedirectAttributes
     */
    public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
        this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
    }

    /**
     * {@inheritDoc}
     * <p>A {@link ConfigurableBeanFactory} is expected for resolving
     * expressions in method argument default values.
     */
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }
    }

    /**
     * Return the owning factory of this bean instance, or {@code null}.
     */
    protected ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void afterPropertiesSet() {
        if (this.argumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.initBinderArgumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
            this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
    }

    /**
     * Return the list of argument resolvers to use including built-in resolvers
     * and custom resolvers provided via {@link #setCustomArgumentResolvers}.
     */
    private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

        // Annotation-based argument resolution
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new PathVariableMethodArgumentResolver());
        resolvers.add(new ServletModelAttributeMethodProcessor(false));
        resolvers.add(new RequestResponseBodyMethodProcessor(getMessageConverters()));
        resolvers.add(new RequestPartMethodArgumentResolver(getMessageConverters()));
        resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new RequestHeaderMapMethodArgumentResolver());
        resolvers.add(new ServletCookieValueMethodArgumentResolver(getBeanFactory()));
        resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));

        // Type-based argument resolution
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        resolvers.add(new HttpEntityMethodProcessor(getMessageConverters()));
        resolvers.add(new RedirectAttributesMethodArgumentResolver());
        resolvers.add(new ModelMethodProcessor());
        resolvers.add(new MapMethodProcessor());
        resolvers.add(new ErrorsMethodArgumentResolver());
        resolvers.add(new SessionStatusMethodArgumentResolver());
        resolvers.add(new UriComponentsBuilderMethodArgumentResolver());

        // Custom arguments
        if (getCustomArgumentResolvers() != null) {
            resolvers.addAll(getCustomArgumentResolvers());
        }

        // Catch-all
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
        resolvers.add(new ServletModelAttributeMethodProcessor(true));

        return resolvers;
    }

    /**
     * Return the list of argument resolvers to use for {@code @InitBinder}
     * methods including built-in and custom resolvers.
     */
    private List<HandlerMethodArgumentResolver> getDefaultInitBinderArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

        // Annotation-based argument resolution
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new PathVariableMethodArgumentResolver());
        resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));

        // Type-based argument resolution
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());

        // Custom arguments
        if (getCustomArgumentResolvers() != null) {
            resolvers.addAll(getCustomArgumentResolvers());
        }

        // Catch-all
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));

        return resolvers;
    }

    /**
     * Return the list of return value handlers to use including built-in and
     * custom handlers provided via {@link #setReturnValueHandlers}.
     */
    private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

        // Single-purpose return value types
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(getMessageConverters()));

        // Annotation-based return value types
        handlers.add(new ModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters()));

        // Multi-purpose return value types
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());

        // Custom return value types
        if (getCustomReturnValueHandlers() != null) {
            handlers.addAll(getCustomReturnValueHandlers());
        }

        // Catch-all
        if (!CollectionUtils.isEmpty(getModelAndViewResolvers())) {
            handlers.add(new ModelAndViewResolverMethodReturnValueHandler(getModelAndViewResolvers()));
        }
        else {
            handlers.add(new ModelAttributeMethodProcessor(true));
        }

        return handlers;
    }

    /**
     * Always return {@code true} since any method argument and return value
     * type will be processed in some way. A method argument not recognized
     * by any HandlerMethodArgumentResolver is interpreted as a request parameter
     * if it is a simple type, or as a model attribute otherwise. A return value
     * not recognized by any HandlerMethodReturnValueHandler will be interpreted
     * as a model attribute.
     */
    @Override
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        return true;
    }

    /**
     * This implementation always returns -1. An {@code @RequestMapping}
     * method can calculate the lastModified value, call
     * {@link WebRequest#checkNotModified(long)}, and return {@code null}
     * if the result of that call is {@code true}.
     */
    @Override
    protected long getLastModifiedInternal(HttpServletRequest request, HandlerMethod handlerMethod) {
        return -1;
    }

    @Override
    protected final ModelAndView handleInternal(HttpServletRequest request,
                                                HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

        if (getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
            // Always prevent caching in case of session attribute management.
            checkAndPrepare(request, response, this.cacheSecondsForSessionAttributeHandlers, true);
        }
        else {
            // Uses configured default cacheSeconds setting.
            checkAndPrepare(request, response, true);
        }

        // Execute invokeHandlerMethod in synchronized block if required.
        if (this.synchronizeOnSession) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object mutex = WebUtils.getSessionMutex(session);
                synchronized (mutex) {
                    return invokeHandlerMethod(request, response, handlerMethod);
                }
            }
        }

        return invokeHandlerMethod(request, response, handlerMethod);
    }

    /**
     * Return the {@link SessionAttributesHandler} instance for the given
     * handler type, never {@code null}.
     */
    private SessionAttributesHandler getSessionAttributesHandler(HandlerMethod handlerMethod) {
        Class<?> handlerType = handlerMethod.getBeanType();
        SessionAttributesHandler sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);
        if (sessionAttrHandler == null) {
            synchronized(this.sessionAttributesHandlerCache) {
                sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);
                if (sessionAttrHandler == null) {
                    sessionAttrHandler = new SessionAttributesHandler(handlerType, sessionAttributeStore);
                    this.sessionAttributesHandlerCache.put(handlerType, sessionAttrHandler);
                }
            }
        }
        return sessionAttrHandler;
    }

    /**
     * Invoke the {@link RequestMapping} handler method preparing a {@link ModelAndView} if view resolution is required.
     */
    private ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response,
                                             HandlerMethod handlerMethod) throws Exception {

        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
        ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);
        ServletInvocableHandlerMethod requestMappingMethod = createRequestMappingMethod(handlerMethod, binderFactory);

        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
        modelFactory.initModel(webRequest, mavContainer, requestMappingMethod);
        mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);

        requestMappingMethod.invokeAndHandle(webRequest, mavContainer);
        modelFactory.updateModel(webRequest, mavContainer);

        if (mavContainer.isRequestHandled()) {
            return null;
        }
        else {
            ModelMap model = mavContainer.getModel();
            ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model);
            if (!mavContainer.isViewReference()) {
                mav.setView((View) mavContainer.getView());
            }
            if (model instanceof RedirectAttributes) {
                Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
                RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
            }
            return mav;
        }
    }

    private ServletInvocableHandlerMethod createRequestMappingMethod(HandlerMethod handlerMethod,
                                                                     WebDataBinderFactory binderFactory) {
        ServletInvocableHandlerMethod requestMethod;
        requestMethod = new ServletInvocableHandlerMethod(handlerMethod);
        requestMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        requestMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        requestMethod.setDataBinderFactory(binderFactory);
        requestMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        return requestMethod;
    }

    private ModelFactory getModelFactory(HandlerMethod handlerMethod, WebDataBinderFactory binderFactory) {
        SessionAttributesHandler sessionAttrHandler = getSessionAttributesHandler(handlerMethod);
        Class<?> handlerType = handlerMethod.getBeanType();
        Set<Method> methods = this.modelFactoryCache.get(handlerType);
        if (methods == null) {
            methods = HandlerMethodSelector.selectMethods(handlerType, MODEL_ATTRIBUTE_METHODS);
            this.modelFactoryCache.put(handlerType, methods);
        }
        List<InvocableHandlerMethod> attrMethods = new ArrayList<InvocableHandlerMethod>();
        for (Method method : methods) {
            InvocableHandlerMethod attrMethod = new InvocableHandlerMethod(handlerMethod.getBean(), method);
            attrMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
            attrMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
            attrMethod.setDataBinderFactory(binderFactory);
            attrMethods.add(attrMethod);
        }
        return new ModelFactory(attrMethods, binderFactory, sessionAttrHandler);
    }

    private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod) throws Exception {
        Class<?> handlerType = handlerMethod.getBeanType();
        Set<Method> methods = this.dataBinderFactoryCache.get(handlerType);
        if (methods == null) {
            methods = HandlerMethodSelector.selectMethods(handlerType, INIT_BINDER_METHODS);
            this.dataBinderFactoryCache.put(handlerType, methods);
        }
        List<InvocableHandlerMethod> binderMethods = new ArrayList<InvocableHandlerMethod>();
        for (Method method : methods) {
            InvocableHandlerMethod binderMethod = new InvocableHandlerMethod(handlerMethod.getBean(), method);
            binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
            binderMethod.setDataBinderFactory(new DefaultDataBinderFactory(this.webBindingInitializer));
            binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
            binderMethods.add(binderMethod);
        }
        return createDataBinderFactory(binderMethods);
    }

    /**
     * Template method to create a new ServletRequestDataBinderFactory instance.
     * <p>The default implementation creates a ServletRequestDataBinderFactory.
     * This can be overridden for custom ServletRequestDataBinder subclasses.
     * @param binderMethods {@code @InitBinder} methods
     * @return the ServletRequestDataBinderFactory instance to use
     * @throws Exception in case of invalid state or arguments
     */
    protected ServletRequestDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods)
            throws Exception {

        return new ServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
    }

    /**
     * MethodFilter that matches {@link InitBinder @InitBinder} methods.
     */
    public static final ReflectionUtils.MethodFilter INIT_BINDER_METHODS = new ReflectionUtils.MethodFilter() {

        public boolean matches(Method method) {
            return AnnotationUtils.findAnnotation(method, InitBinder.class) != null;
        }
    };

    /**
     * MethodFilter that matches {@link ModelAttribute @ModelAttribute} methods.
     */
    public static final MethodFilter MODEL_ATTRIBUTE_METHODS = new MethodFilter() {

        public boolean matches(Method method) {
            return ((AnnotationUtils.findAnnotation(method, RequestMapping.class) == null) &&
                    (AnnotationUtils.findAnnotation(method, ModelAttribute.class) != null));
        }
    };

}
