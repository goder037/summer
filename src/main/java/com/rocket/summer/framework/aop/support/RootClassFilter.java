package com.rocket.summer.framework.aop.support;

import java.io.Serializable;

import com.rocket.summer.framework.aop.ClassFilter;

/**
 * Simple ClassFilter implementation that passes classes (and optionally subclasses).
 *
 * @author Rod Johnson
 */
@SuppressWarnings("serial")
public class RootClassFilter implements ClassFilter, Serializable {

    private Class<?> clazz;


    public RootClassFilter(Class<?> clazz) {
        this.clazz = clazz;
    }


    @Override
    public boolean matches(Class<?> candidate) {
        return this.clazz.isAssignableFrom(candidate);
    }

}

