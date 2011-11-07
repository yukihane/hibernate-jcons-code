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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a reflective proxy that uses straight simple name mappings to attach to the interfaces.
 *
 * @author Juergen_Kellerer, 2010-09-16
 * @version 1.0
 */
public final class StraightNameMappingProxy extends AbstractMethodMappingProxy {

	private static final long serialVersionUID = 3119660935285415944L;

	private static final Map<Class<?>, Constructor<?>> proxies = new HashMap<Class<?>, Constructor<?>>();

	/**
	 * Constructs a new name mapping proxy class.
	 *
	 * @param interfaceClass the interface defining the methods.
	 * @param delegate       the delegate used to query the methods.
	 * @param defaultValue   the default value to return if the delegate does not define a matching method.
	 * @return a new proxy class that dynamically maps method calls on the delegate.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> interfaceClass, Object delegate, Object defaultValue) {
		try {
			Constructor<?> proxyConstructor;
			synchronized (proxies) {
				proxyConstructor = proxies.get(interfaceClass);
				if (proxyConstructor == null) {
					proxyConstructor = Proxy.getProxyClass(interfaceClass.getClassLoader(), interfaceClass).
							getConstructor(InvocationHandler.class);
					proxies.put(interfaceClass, proxyConstructor);
				}
			}
			return (T) proxyConstructor.newInstance(new StraightNameMappingProxy(interfaceClass, delegate, defaultValue));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private StraightNameMappingProxy(Class<?> interfaceClass, Object delegate, Object defaultValue) {
		super(interfaceClass, delegate, defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Method findTargetMethod(Method interfaceMethod, Class<?> targetClass) throws NoSuchMethodException {
		Method target = targetClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
		if (!interfaceMethod.getReturnType().isAssignableFrom(target.getReturnType()))
			throw new NoSuchMethodException("Name matches, but return type is not compatible");
		return target;
	}
}
