/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rocket.summer.framework.beans.factory.config;

import java.lang.reflect.InvocationTargetException;

import com.rocket.summer.framework.beans.TypeConverter;
import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.beans.support.ArgumentConvertingMethodInvoker;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * Simple method invoker bean: just invoking a target method, not expecting a result
 * to expose to the container (in contrast to {@link MethodInvokingFactoryBean}).
 *
 * <p>This invoker supports any kind of target method. A static method may be specified
 * by setting the {@link #setTargetMethod targetMethod} property to a String representing
 * the static method name, with {@link #setTargetClass targetClass} specifying the Class
 * that the static method is defined on. Alternatively, a target instance method may be
 * specified, by setting the {@link #setTargetObject targetObject} property as the target
 * object, and the {@link #setTargetMethod targetMethod} property as the name of the
 * method to call on that target object. Arguments for the method invocation may be
 * specified by setting the {@link #setArguments arguments} property.
 *
 * <p>This class depends on {@link #afterPropertiesSet()} being called once
 * all properties have been set, as per the InitializingBean contract.
 *
 * <p>An example (in an XML based bean factory definition) of a bean definition
 * which uses this class to call a static initialization method:
 *
 * <pre class="code">
 * &lt;bean id="myObject" class="com.rocket.summer.framework.beans.factory.config.MethodInvokingBean">
 *   &lt;property name="staticMethod" value="com.whatever.MyClass.init"/>
 * &lt;/bean></pre>
 *
 * <p>An example of calling an instance method to start some server bean:
 *
 * <pre class="code">
 * &lt;bean id="myStarter" class="com.rocket.summer.framework.beans.factory.config.MethodInvokingBean">
 *   &lt;property name="targetObject" ref="myServer"/>
 *   &lt;property name="targetMethod" value="start"/>
 * &lt;/bean></pre>
 *
 * @author Juergen Hoeller
 * @since 4.0.3
 * @see MethodInvokingFactoryBean
 * @see com.rocket.summer.framework.util.MethodInvoker
 */
public class MethodInvokingBean extends ArgumentConvertingMethodInvoker
		implements BeanClassLoaderAware, BeanFactoryAware, InitializingBean {

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private ConfigurableBeanFactory beanFactory;


	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
		return ClassUtils.forName(className, this.beanClassLoader);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	/**
	 * Obtain the TypeConverter from the BeanFactory that this bean runs in,
	 * if possible.
	 * @see ConfigurableBeanFactory#getTypeConverter()
	 */
	@Override
	protected TypeConverter getDefaultTypeConverter() {
		if (this.beanFactory != null) {
			return this.beanFactory.getTypeConverter();
		}
		else {
			return super.getDefaultTypeConverter();
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		prepare();
		invokeWithTargetException();
	}

	/**
	 * Perform the invocation and convert InvocationTargetException
	 * into the underlying target exception.
	 */
	protected Object invokeWithTargetException() throws Exception {
		try {
			return invoke();
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof Exception) {
				throw (Exception) ex.getTargetException();
			}
			if (ex.getTargetException() instanceof Error) {
				throw (Error) ex.getTargetException();
			}
			throw ex;
		}
	}

}
