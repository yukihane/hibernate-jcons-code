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

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;

/**
 * Enumerates all used names.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public enum Names {

	HibernateStatistics(Type.mbean, System.getProperty("hibernate.mbean", "Hibernate:application=Statistics")),

	clear(Type.method, null),
	logSummary(Type.method, null),

	getEntityStatistics(Type.method, null),
	getCollectionStatistics(Type.method, null),
	getSecondLevelCacheStatistics(Type.method, null),
	getQueryStatistics(Type.method, null),

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
		method, attribute, mbean
	}

	private Type type = Type.attribute;
	private ObjectName objectName;

	private Names() {
	}

	private Names(Type type, String objectName) {
		this.type = type;
		if (objectName != null)
			try {
				this.objectName = new ObjectName(objectName);
			} catch (MalformedObjectNameException e) {
				throw new IllegalArgumentException(e);
			}
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
	 * Returns the object name of this mbean or null if the name is not an mbean type.
	 *
	 * @return the object name of this mbean or null if the name is not an mbean type.
	 */
	public ObjectName getObjectName() {
		return objectName;
	}

	/**
	 * Returns all attribute names as string list.
	 *
	 * @return all attribute names as string list.
	 */
	public static List<String> getAllAttributes() {
		List<String> names = new ArrayList<String>(values().length);
		for (Names name : values())
			if (name.getType() == Type.attribute)
				names.add(name.name());
		return names;
	}
}
