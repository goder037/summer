package com.rocket.summer.framework.data.repository.query;

import static java.lang.String.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.data.domain.Pageable;
import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.repository.util.QueryExecutionConverters;
import com.rocket.summer.framework.data.util.ClassTypeInformation;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.util.Assert;

/**
 * Class to abstract a single parameter of a query method. It is held in the context of a {@link Parameters} instance.
 *
 * @author Oliver Gierke
 * @author Jens Schauder
 */
public class Parameter {

    @SuppressWarnings("unchecked") static final List<Class<?>> TYPES = Arrays.asList(Pageable.class, Sort.class);

    private static final String NAMED_PARAMETER_TEMPLATE = ":%s";
    private static final String POSITION_PARAMETER_TEMPLATE = "?%s";

    private final MethodParameter parameter;
    private final Class<?> parameterType;
    private final boolean isDynamicProjectionParameter;

    /**
     * Creates a new {@link Parameter} for the given {@link MethodParameter}.
     *
     * @param parameter must not be {@literal null}.
     */
    protected Parameter(MethodParameter parameter) {

        Assert.notNull(parameter, "MethodParameter must not be null!");

        this.parameter = parameter;
        this.parameterType = potentiallyUnwrapParameterType(parameter);
        this.isDynamicProjectionParameter = isDynamicProjectionParameter(parameter);
    }

    /**
     * Returns whether the parameter is a special parameter.
     *
     * @return
     * @see #TYPES
     */
    public boolean isSpecialParameter() {
        return isDynamicProjectionParameter || TYPES.contains(parameter.getParameterType());
    }

    /**
     * Returns whether the {@link Parameter} is to be bound to a query.
     *
     * @return
     */
    public boolean isBindable() {
        return !isSpecialParameter();
    }

    /**
     * Returns whether the current {@link Parameter} is the one used for dynamic projections.
     *
     * @return
     */
    public boolean isDynamicProjectionParameter() {
        return isDynamicProjectionParameter;
    }

    /**
     * Returns the placeholder to be used for the parameter. Can either be a named one or positional.
     *
     * @return
     */
    public String getPlaceholder() {

        if (isNamedParameter()) {
            return format(NAMED_PARAMETER_TEMPLATE, getName());
        } else {
            return format(POSITION_PARAMETER_TEMPLATE, getIndex());
        }
    }

    /**
     * Returns the position index the parameter is bound to in the context of its surrounding {@link Parameters}.
     *
     * @return
     */
    public int getIndex() {
        return parameter.getParameterIndex();
    }

    /**
     * Returns whether the parameter is annotated with {@link Param}.
     *
     * @return
     */
    public boolean isNamedParameter() {
        return !isSpecialParameter() && getName() != null;
    }

    /**
     * Returns the name of the parameter (through {@link Param} annotation) or null if none can be found.
     *
     * @return
     */
    public String getName() {

        Param annotation = parameter.getParameterAnnotation(Param.class);
        return annotation == null ? parameter.getParameterName() : annotation.value();
    }

    /**
     * Returns the type of the {@link Parameter}.
     *
     * @return the type
     */
    public Class<?> getType() {
        return parameterType;
    }

    /**
     * Returns whether the parameter is named explicitly, i.e. annotated with {@link Param}.
     *
     * @return
     * @since 1.11
     */
    public boolean isExplicitlyNamed() {
        return parameter.hasParameterAnnotation(Param.class);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return format("%s:%s", isNamedParameter() ? getName() : "#" + getIndex(), getType().getName());
    }

    /**
     * Returns whether the {@link Parameter} is a {@link Pageable} parameter.
     *
     * @return
     */
    boolean isPageable() {
        return Pageable.class.isAssignableFrom(getType());
    }

    /**
     * Returns whether the {@link Parameter} is a {@link Sort} parameter.
     *
     * @return
     */
    boolean isSort() {
        return Sort.class.isAssignableFrom(getType());
    }

    /**
     * Returns whether the given {@link MethodParameter} is a dynamic projection parameter, which means it carries a
     * dynamic type parameter which is identical to the type parameter of the actually returned type.
     * <p>
     * <code>
     * <T> Collection<T> findBy…(…, Class<T> type);
     * </code>
     *
     * @param parameter must not be {@literal null}.
     * @return
     */
    private static boolean isDynamicProjectionParameter(MethodParameter parameter) {

        Method method = parameter.getMethod();

        ClassTypeInformation<?> ownerType = ClassTypeInformation.from(parameter.getDeclaringClass());
        TypeInformation<?> parameterTypes = ownerType.getParameterTypes(method).get(parameter.getParameterIndex());

        if (!parameterTypes.getType().equals(Class.class)) {
            return false;
        }

        TypeInformation<?> bound = parameterTypes.getTypeArguments().get(0);
        TypeInformation<Object> returnType = ClassTypeInformation.fromReturnTypeOf(method);

        return bound.equals(QueryExecutionConverters.unwrapWrapperTypes(returnType));
    }

    /**
     * Returns the component type if the given {@link MethodParameter} is a wrapper type.
     *
     * @param parameter must not be {@literal null}.
     * @return
     */
    private static Class<?> potentiallyUnwrapParameterType(MethodParameter parameter) {

        Class<?> originalType = parameter.getParameterType();

        return QueryExecutionConverters.supports(originalType)
                ? ResolvableType.forMethodParameter(parameter).getGeneric(0).getRawClass() : originalType;
    }
}

