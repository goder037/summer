package com.rocket.summer.framework.core.convert;

import com.rocket.summer.framework.core.GenericCollectionTypeResolver;
import com.rocket.summer.framework.core.MethodParameter;

import java.lang.annotation.Annotation;

/**
 * @author Keith Donald
 * @since 3.1
 */
class BeanPropertyDescriptor extends AbstractDescriptor {

    private final Property property;

    private final MethodParameter methodParameter;

    private final Annotation[] annotations;


    public BeanPropertyDescriptor(Property property) {
        super(property.getType());
        this.property = property;
        this.methodParameter = property.getMethodParameter();
        this.annotations = property.getAnnotations();
    }


    @Override
    public Annotation[] getAnnotations() {
        return this.annotations;
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
        return new BeanPropertyDescriptor(type, this.property, methodParameter, this.annotations);
    }


    // internal

    private BeanPropertyDescriptor(Class<?> type, Property propertyDescriptor, MethodParameter methodParameter, Annotation[] annotations) {
        super(type);
        this.property = propertyDescriptor;
        this.methodParameter = methodParameter;
        this.annotations = annotations;
    }

}
