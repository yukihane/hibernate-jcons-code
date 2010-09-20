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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enumerates all used names.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public enum Names {

	clear(Type.method),
	logSummary(Type.method),

	getEntityStatistics(Type.method),
	getCollectionStatistics(Type.method),
	getSecondLevelCacheStatistics(Type.method),
	getQueryStatistics(Type.method),

	CloseStatementCount,
	CollectionFetchCount,
	CollectionLoadCount,
	CollectionRecreateCount,
	CollectionRemoveCount,
	CollectionRoleNames,
	CollectionUpdateCount,

	ConnectCount,
	PrepareStatementCount,
	FlushCount,
	OptimisticFailureCount,

	SessionCloseCount,
	SessionFactoryJNDIName,
	SessionOpenCount,

	TransactionCount,
	SuccessfulTransactionCount,

	EntityDeleteCount,
	EntityFetchCount,
	EntityInsertCount,
	EntityLoadCount,
	EntityNames,
	EntityUpdateCount,

	Queries,
	QueryCacheHitCount,
	QueryCacheMissCount,
	QueryCachePutCount,
	QueryExecutionCount,
	QueryExecutionMaxTime,
	QueryExecutionMaxTimeQueryString,

	SecondLevelCacheRegionNames,
	SecondLevelCacheHitCount,
	SecondLevelCacheMissCount,
	SecondLevelCachePutCount,;

	public enum Type {
		method, attribute
	}

	private static final List<Names> allAttributes, allMethods;

	static {
		List<Names> names = new ArrayList<Names>(values().length);
		List<Names> methods = new ArrayList<Names>();

		for (Names name : values()) {
			switch (name.getType()) {
				case attribute:
					names.add(name);
					break;
				case method:
					methods.add(name);
					break;
			}
		}

		allMethods = Collections.unmodifiableList(methods);
		allAttributes = Collections.unmodifiableList(names);
	}

	private Type type = Type.attribute;

	private Names() {
	}

	private Names(Type type) {
		this.type = type;
	}

	/**
	 * Returns the type of the name.
	 *
	 * @return the type of the name.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns all method names as list.
	 *
	 * @return all method names as list.
	 */
	public static List<Names> getAllMethods() {
		return allMethods;
	}

	/**
	 * Returns all attribute names as list.
	 *
	 * @return all attribute names as list.
	 */
	public static List<Names> getAllAttributes() {
		return allAttributes;
	}
}
