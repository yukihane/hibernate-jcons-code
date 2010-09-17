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

package net.sf.hibernate.jconsole;

import net.sf.hibernate.jconsole.stats.StraightNameMappingProxy;

import static net.sf.hibernate.jconsole.Methods.*;

/**
 * Is a local context that is used to cache and exchange statistical information.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public class HibernateContext extends AbstractStatisticsContext {

	private static final long serialVersionUID = -7295609157873741739L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getEntityStatisticsFor(String name) throws Exception {
		return getEntityStatistics.invoke(getConnection(), name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getCollectionStatisticsFor(String name) throws Exception {
		return getCollectionStatistics.invoke(getConnection(), name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getQueryStatisticsFor(String name) throws Exception {
		return getQueryStatistics.invoke(getConnection(), name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getCacheStatisticsFor(String name) throws Exception {
		return getSecondLevelCacheStatistics.invoke(getConnection(), name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected <T> T proxyFor(Class<T> interfaceClass, Object delegate) {
		if (delegate == null)
			delegate = new Object();
		return StraightNameMappingProxy.newInstance(interfaceClass, delegate, 0L);
	}
}
