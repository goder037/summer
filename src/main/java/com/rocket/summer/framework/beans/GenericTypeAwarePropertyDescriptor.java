package com.rocket.summer.framework.beans;

import com.rocket.summer.framework.core.BridgeMethodResolver;
import com.rocket.summer.framework.core.GenericTypeResolver;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Extension of the standard JavaBeans PropertyDescriptor class,
 * overriding <code>getPropertyType()</code> such that a generically
 * declared type will be resolved against the containing bean class.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 */
class GenericTypeAwarePropertyDescriptor extends PropertyDescriptor {

    private final Class beanClass;

    private final Method readMethod;

    private final Method writeMethod;

    private final Class propertyEditorClass;

    private Class propertyType;

    private MethodParameter writeMethodParameter;


    public GenericTypeAwarePropertyDescriptor(Class beanClass, String propertyName,
                                              Method readMethod, Method writeMethod, Class propertyEditorClass)
            throws IntrospectionException {

        super(propertyName, null, null);
        this.beanClass = beanClass;
        Method readMethodToUse = BridgeMethodResolver.findBridgedMethod(readMethod);
        Method writeMethodToUse = BridgeMethodResolver.findBridgedMethod(writeMethod);
        if (writeMethodToUse == null && readMethodToUse != null) {
            // Fallback: Original JavaBeans introspection might not have found matching setter
            // method due to lack of bridge method resolution, in case of the getter using a
            // covariant return type whereas the setter is defined for the concrete property type.
            writeMethodToUse = ClassUtils.getMethodIfAvailable(this.beanClass,
                    "set" + StringUtils.capitalize(getName()), new Class[] {readMethodToUse.getReturnType()});
        }
        this.readMethod = readMethodToUse;
        this.writeMethod = writeMethodToUse;
        this.propertyEditorClass = propertyEditorClass;
    }


    public Method getReadMethod() {
        return this.readMethod;
    }

    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public Class getPropertyEditorClass() {
        return this.propertyEditorClass;
    }

    public synchronized Class getPropertyType() {
        if (this.propertyType == null) {
            if (this.readMethod != null) {
                this.propertyType = GenericTypeResolver.resolveReturnType(this.readMethod, this.beanClass);
            }
            else {
                MethodParameter writeMethodParam = getWriteMethodParameter();
                if (writeMethodParam != null) {
                    this.propertyType = writeMethodParam.getParameterType();
                }
                else {
                    this.propertyType = super.getPropertyType();
                }
            }
        }
        return this.propertyType;
    }

    public synchronized MethodParameter getWriteMethodParameter() {
        if (this.writeMethod == null) {
            return null;
        }
        if (this.writeMethodParameter == null) {
            this.writeMethodParameter = new MethodParameter(this.writeMethod, 0);
            GenericTypeResolver.resolveParameterType(this.writeMethodParameter, this.beanClass);
        }
        return this.writeMethodParameter;
    }

}
