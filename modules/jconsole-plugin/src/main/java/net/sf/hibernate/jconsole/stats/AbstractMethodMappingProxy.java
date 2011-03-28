/*
 * Copyright (c) 2010
 *
 * This file is part of HibernateJConsole.
 *
 *     HibernateJConsole is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     HibernateJConsole is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with HibernateJConsole.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.hibernate.jconsole.stats;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements an dynamic abstraction layer that maps
 *
 * @author Juergen_Kellerer, 2010-09-16
 * @version 1.0
 */
public abstract class AbstractMethodMappingProxy implements InvocationHandler, Serializable {

	private static final Map<Class<?>, Map<Class<?>, Map<Method, Method>>> classMapping =
			new HashMap<Class<?>, Map<Class<?>, Map<Method, Method>>>();

	final Object delegate;
	final Object defaultValue;
	final Class<?> interfaceClass;
	final Map<Method, Method> mapping;

	protected AbstractMethodMappingProxy(Class<?> interfaceClass, Object delegate, Object defaultValue) {
		this.interfaceClass = interfaceClass;
		this.delegate = delegate;
		this.defaultValue = defaultValue;
		mapping = getMethodMapping(interfaceClass, delegate.getClass());
	}

	/**
	 * Is called in order to resolve the method to call on the delegate when the interface method is called.
	 *
	 * @param interfaceMethod the interface method.
	 * @param targetClass	 the target (delegate) class.
	 * @return the target method to call on the delegate.
	 * @throws NoSuchMethodException in case of no method exists that matches.
	 */
	protected abstract Method findTargetMethod(Method interfaceMethod, Class<?> targetClass)
			throws NoSuchMethodException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Method target = mapping.get(method);
		try {
			return target == null ? defaultValue : target.invoke(delegate, args);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof NullPointerException)
				return defaultValue;
			throw e;
		}
	}

	private Map<Method, Method> getMethodMapping(Class<?> forInterface, Class<?> forClass) {
		synchronized (classMapping) {
			Map<Class<?>, Map<Method, Method>> mapping = classMapping.get(forInterface);
			if (mapping == null) {
				mapping = new HashMap<Class<?>, Map<Method, Method>>();
				classMapping.put(forInterface, mapping);
			}

			Map<Method, Method> methodMapping = mapping.get(forClass);
			if (methodMapping == null) {
				methodMapping = createMethodMapping(forInterface, forClass);
				mapping.put(forClass, methodMapping);
			}

			return methodMapping;
		}
	}

	private Map<Method, Method> createMethodMapping(Class<?> forInterface, Class<?> forClass) {
		Map<Method, Method> mapping = new HashMap<Method, Method>();
		for (Method method : forInterface.getMethods()) {
			try {
				mapping.put(method, findTargetMethod(method, forClass));
			} catch (NoSuchMethodException e) {
				System.err.println("Failed finding a callable delegate for interface method: " + method);
			}
		}
		return mapping;
	}
}
