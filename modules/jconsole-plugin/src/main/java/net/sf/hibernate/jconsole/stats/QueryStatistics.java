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

/**
 * Query statistics (HQL and SQL).
 * <p/>
 * Note that for a cached query, the cache miss is equals to the db count.
 *
 * @author Gavin King
 * @author Alex Snaps
 */
public interface QueryStatistics extends Serializable {
	/**
	 * Returns the number of times the query was run against the database.
	 * <p/>
	 * This number does not increment when a query was fetched from the DB.
	 * See {@link org.hibernate.loader.Loader#list(org.hibernate.engine.SessionImplementor, org.hibernate.engine.QueryParameters, java.util.Set, org.hibernate.type.Type[])}
	 *
	 * @return the number of times the query was run against the database.
	 */
	long getExecutionCount();

	/**
	 * Returns the number of times the query was retrieved from the cache.
	 *
	 * @return the number of times the query was retrieved from the cache.
	 */
	long getCacheHitCount();

	/**
	 * Returns the number of times the query results were put into the cache.
	 *
	 * @return the number of times the query results were put into the cache.
	 */
	long getCachePutCount();

	/**
	 * Returns the number of times the cache was queried but the results were either not yet cached or outdated.
	 *
	 * @return the number of times the cache was queried but the results were either not yet cached or outdated.
	 */
	long getCacheMissCount();

	/**
	 * Returns the number of rows that were fetched from the DB.
	 * <p/>
	 * Note: Queries that are serviced from the query cache do not increment this value.
	 *
	 * @return the number of rows that were fetched from the DB.
	 */
	long getExecutionRowCount();

	/**
	 * Returns the average execution time for a single query when executed on the DB.
	 *
	 * @return the average execution time for a single query when executed on the DB.
	 */
	long getExecutionAvgTime();

	/**
	 * Returns the max execution time for a single query when executed on the DB.
	 *
	 * @return the max execution time for a single query when executed on the DB.
	 */
	long getExecutionMaxTime();

	/**
	 * Returns the min execution time for a single query when executed on the DB.
	 *
	 * @return the min execution time for a single query when executed on the DB.
	 */
	long getExecutionMinTime();
}
