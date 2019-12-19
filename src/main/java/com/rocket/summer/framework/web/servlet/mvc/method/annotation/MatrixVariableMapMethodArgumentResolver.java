package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.annotation.MatrixVariable;
import com.rocket.summer.framework.web.bind.annotation.ValueConstants;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.RequestAttributes;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.HandlerMapping;

/**
 * Resolves arguments of type {@link Map} annotated with {@link MatrixVariable @MatrixVariable}
 * where the annotation does not specify a name. In other words the purpose of this resolver
 * is to provide access to multiple matrix variables, either all or associated with a specific
 * path variable.
 *
 * <p>When a name is specified, an argument of type Map is considered to be a single attribute
 * with a Map value, and is resolved by {@link MatrixVariableMethodArgumentResolver} instead.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class MatrixVariableMapMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        MatrixVariable matrixVariable = parameter.getParameterAnnotation(MatrixVariable.class);
        if (matrixVariable != null) {
            if (Map.class.isAssignableFrom(parameter.getParameterType())) {
                return !StringUtils.hasText(matrixVariable.name());
            }
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {

        @SuppressWarnings("unchecked")
        Map<String, MultiValueMap<String, String>> matrixVariables =
                (Map<String, MultiValueMap<String, String>>) request.getAttribute(
                        HandlerMapping.MATRIX_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        if (CollectionUtils.isEmpty(matrixVariables)) {
            return Collections.emptyMap();
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        String pathVariable = parameter.getParameterAnnotation(MatrixVariable.class).pathVar();

        if (!pathVariable.equals(ValueConstants.DEFAULT_NONE)) {
            MultiValueMap<String, String> mapForPathVariable = matrixVariables.get(pathVariable);
            if (mapForPathVariable == null) {
                return Collections.emptyMap();
            }
            map.putAll(mapForPathVariable);
        }
        else {
            for (MultiValueMap<String, String> vars : matrixVariables.values()) {
                for (String name : vars.keySet()) {
                    for (String value : vars.get(name)) {
                        map.add(name, value);
                    }
                }
            }
        }

        return (isSingleValueMap(parameter) ? map.toSingleValueMap() : map);
    }

    private boolean isSingleValueMap(MethodParameter parameter) {
        if (!MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            ResolvableType[] genericTypes = ResolvableType.forMethodParameter(parameter).getGenerics();
            if (genericTypes.length == 2) {
                return !List.class.isAssignableFrom(genericTypes[1].getRawClass());
            }
        }
        return false;
    }

}

