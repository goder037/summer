package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.bind.annotation.InitBinder;
import com.rocket.summer.framework.web.bind.support.DefaultDataBinderFactory;
import com.rocket.summer.framework.web.bind.support.WebBindingInitializer;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.HandlerMethod;
import com.rocket.summer.framework.web.method.support.InvocableHandlerMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Adds initialization to a WebDataBinder via {@code @InitBinder} methods.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class InitBinderDataBinderFactory extends DefaultDataBinderFactory {

    private final List<InvocableHandlerMethod> binderMethods;

    /**
     * Create a new instance.
     * @param binderMethods {@code @InitBinder} methods, or {@code null}
     * @param initializer for global data binder intialization
     */
    public InitBinderDataBinderFactory(List<InvocableHandlerMethod> binderMethods, WebBindingInitializer initializer) {
        super(initializer);
        this.binderMethods = (binderMethods != null) ? binderMethods : new ArrayList<InvocableHandlerMethod>();
    }

    /**
     * Initialize a WebDataBinder with {@code @InitBinder} methods.
     * If the {@code @InitBinder} annotation specifies attributes names, it is
     * invoked only if the names include the target object name.
     * @throws Exception if one of the invoked @{@link InitBinder} methods fail.
     */
    @Override
    public void initBinder(WebDataBinder binder, NativeWebRequest request) throws Exception {
        for (InvocableHandlerMethod binderMethod : this.binderMethods) {
            if (isBinderMethodApplicable(binderMethod, binder)) {
                Object returnValue = binderMethod.invokeForRequest(request, null, binder);
                if (returnValue != null) {
                    throw new IllegalStateException("@InitBinder methods should return void: " + binderMethod);
                }
            }
        }
    }

    /**
     * Return {@code true} if the given {@code @InitBinder} method should be
     * invoked to initialize the given WebDataBinder.
     * <p>The default implementation checks if target object name is included
     * in the attribute names specified in the {@code @InitBinder} annotation.
     */
    protected boolean isBinderMethodApplicable(HandlerMethod initBinderMethod, WebDataBinder binder) {
        InitBinder annot = initBinderMethod.getMethodAnnotation(InitBinder.class);
        Collection<String> names = Arrays.asList(annot.value());
        return (names.size() == 0 || names.contains(binder.getObjectName()));
    }

}
