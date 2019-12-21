package com.rocket.summer.framework.cglib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FastConstructor extends FastMember
{
    FastConstructor(FastClass fc, Constructor constructor) {
        super(fc, constructor, fc.getIndex(constructor.getParameterTypes()));
    }

    public Class[] getParameterTypes() {
        return ((Constructor)member).getParameterTypes();
    }

    public Class[] getExceptionTypes() {
        return ((Constructor)member).getExceptionTypes();
    }

    public Object newInstance() throws InvocationTargetException {
        return fc.newInstance(index, null);
    }

    public Object newInstance(Object[] args) throws InvocationTargetException {
        return fc.newInstance(index, args);
    }

    public Constructor getJavaConstructor() {
        return (Constructor)member;
    }
}

