package com.rocket.summer.framework.core.convert;

import com.rocket.summer.framework.core.GenericCollectionTypeResolver;
import com.rocket.summer.framework.core.MethodParameter;

import java.lang.annotation.Annotation;

/**
 * @author Keith Donald
 * @since 3.1
 */
class ParameterDescriptor extends AbstractDescriptor {

    private final MethodParameter methodParameter;


    public ParameterDescriptor(MethodParameter methodParameter) {
        super(methodParameter.getParameterType());
        if (methodParameter.getNestingLevel() != 1) {
            throw new IllegalArgumentException("MethodParameter argument must have its nestingLevel set to 1");
        }
        this.methodParameter = methodParameter;
    }

    private ParameterDescriptor(Class<?> type, MethodParameter methodParameter) {
        super(type);
        this.methodParameter = methodParameter;
    }


    @Override
    public Annotation[] getAnnotations() {
        if (this.methodParameter.getParameterIndex() == -1) {
            return TypeDescriptor.nullSafeAnnotations(this.methodParameter.getMethodAnnotations());
        }
        else {
            return TypeDescriptor.nullSafeAnnotations(this.methodParameter.getParameterAnnotations());
        }
    }

    @Override
    protected Class<?> resolveCollectionElementType() {
        return GenericCollectionTypeResolver.getCollectionParameterType(this.methodParameter);
    }

    @Override
    protected Class<?> resolveMapKeyType() {
        return GenericCollectionTypeResolver.getMapKeyParameterType(this.methodParameter);
    }

    @Override
    protected Class<?> resolveMapValueType() {
        return GenericCollectionTypeResolver.getMapValueParameterType(this.methodParameter);
    }

    @Override
    protected AbstractDescriptor nested(Class<?> type, int typeIndex) {
        MethodParameter methodParameter = new MethodParameter(this.methodParameter);
        methodParameter.increaseNestingLevel();
        methodParameter.setTypeIndexForCurrentLevel(typeIndex);
        return new ParameterDescriptor(type, methodParameter);
    }

}