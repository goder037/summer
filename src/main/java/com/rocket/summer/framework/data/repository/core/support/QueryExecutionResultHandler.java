package com.rocket.summer.framework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Map;

import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.support.DefaultConversionService;
import com.rocket.summer.framework.core.convert.support.GenericConversionService;
import com.rocket.summer.framework.data.repository.util.NullableWrapper;
import com.rocket.summer.framework.data.repository.util.QueryExecutionConverters;
import com.rocket.summer.framework.util.Assert;

/**
 * Simple domain service to convert query results into a dedicated type.
 *
 * @author Oliver Gierke
 */
class QueryExecutionResultHandler {

    private static final TypeDescriptor WRAPPER_TYPE = TypeDescriptor.valueOf(NullableWrapper.class);

    private final GenericConversionService conversionService;

    /**
     * Creates a new {@link QueryExecutionResultHandler}.
     */
    public QueryExecutionResultHandler() {

        GenericConversionService conversionService = new DefaultConversionService();
        QueryExecutionConverters.registerConvertersIn(conversionService);

        this.conversionService = conversionService;
    }

    /**
     * Post-processes the given result of a query invocation to match the return type of the given method.
     *
     * @param result can be {@literal null}.
     * @param method must not be {@literal null}.
     * @return
     */
    public Object postProcessInvocationResult(Object result, Method method) {

        Assert.notNull(method, "Method must not be null!");

        if (method.getReturnType().isInstance(result)) {
            return result;
        }

        MethodParameter parameter = new MethodParameter(method, -1);
        TypeDescriptor methodReturnTypeDescriptor = TypeDescriptor.nested(parameter, 0);

        return postProcessInvocationResult(result, methodReturnTypeDescriptor);
    }

    /**
     * Post-processes the given result of a query invocation to the given type.
     *
     * @param result can be {@literal null}.
     * @param returnTypeDesciptor can be {@literal null}, if so, no conversion is performed.
     * @return
     */
    Object postProcessInvocationResult(Object result, TypeDescriptor returnTypeDesciptor) {

        if (returnTypeDesciptor == null) {
            return result;
        }

        Class<?> expectedReturnType = returnTypeDesciptor.getType();

        if (result != null && expectedReturnType.isInstance(result)) {
            return result;
        }

        if (QueryExecutionConverters.supports(expectedReturnType)
                && conversionService.canConvert(WRAPPER_TYPE, returnTypeDesciptor)
                && !conversionService.canBypassConvert(WRAPPER_TYPE, TypeDescriptor.valueOf(expectedReturnType))) {
            return conversionService.convert(new NullableWrapper(result), expectedReturnType);
        }

        if (result != null) {
            return conversionService.canConvert(result.getClass(), expectedReturnType)
                    ? conversionService.convert(result, expectedReturnType)
                    : result;
        }

        if (Map.class.equals(expectedReturnType)) {
            return CollectionFactory.createMap(expectedReturnType, 0);
        }

        return null;
    }
}

