package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.ServletRequestBindingException;
import com.rocket.summer.framework.web.bind.annotation.MatrixVariable;
import com.rocket.summer.framework.web.bind.annotation.ValueConstants;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.RequestAttributes;
import com.rocket.summer.framework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import com.rocket.summer.framework.web.servlet.HandlerMapping;

/**
 * Resolves method arguments annotated with {@link MatrixVariable @MatrixVariable}.
 *
 * <p>If the method parameter is of type Map and no name is specified, then it will
 * by resolved by the {@link MatrixVariableMapMethodArgumentResolver} instead.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 3.2
 */
public class MatrixVariableMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    public MatrixVariableMethodArgumentResolver() {
        super(null);
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(MatrixVariable.class)) {
            return false;
        }
        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            String variableName = parameter.getParameterAnnotation(MatrixVariable.class).name();
            return StringUtils.hasText(variableName);
        }
        return true;
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        MatrixVariable annotation = parameter.getParameterAnnotation(MatrixVariable.class);
        return new MatrixVariableNamedValueInfo(annotation);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Map<String, MultiValueMap<String, String>> pathParameters = (Map<String, MultiValueMap<String, String>>)
                request.getAttribute(HandlerMapping.MATRIX_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (CollectionUtils.isEmpty(pathParameters)) {
            return null;
        }

        String pathVar = parameter.getParameterAnnotation(MatrixVariable.class).pathVar();
        List<String> paramValues = null;

        if (!pathVar.equals(ValueConstants.DEFAULT_NONE)) {
            if (pathParameters.containsKey(pathVar)) {
                paramValues = pathParameters.get(pathVar).get(name);
            }
        }
        else {
            boolean found = false;
            paramValues = new ArrayList<String>();
            for (MultiValueMap<String, String> params : pathParameters.values()) {
                if (params.containsKey(name)) {
                    if (found) {
                        String paramType = parameter.getNestedParameterType().getName();
                        throw new ServletRequestBindingException(
                                "Found more than one match for URI path parameter '" + name +
                                        "' for parameter type [" + paramType + "]. Use 'pathVar' attribute to disambiguate.");
                    }
                    paramValues.addAll(params.get(name));
                    found = true;
                }
            }
        }

        if (CollectionUtils.isEmpty(paramValues)) {
            return null;
        }
        else if (paramValues.size() == 1) {
            return paramValues.get(0);
        }
        else {
            return paramValues;
        }
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new ServletRequestBindingException("Missing matrix variable '" + name +
                "' for method parameter of type " + parameter.getNestedParameterType().getSimpleName());
    }


    private static class MatrixVariableNamedValueInfo extends NamedValueInfo {

        private MatrixVariableNamedValueInfo(MatrixVariable annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }

}

