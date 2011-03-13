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

import net.sf.hibernate.jconsole.AbstractStatisticsContext;

import java.util.Map;

import static net.sf.hibernate.jconsole.AbstractStatisticsContext.HISTORY_LENGTH;
import static net.sf.hibernate.jconsole.stats.Names.*;

/**
 * Implements the primary table that is used to display metrics.
 *
 * @author Juergen_Kellerer, 2010-09-20
 * @version 1.0
 */
public class PrimaryStatisticsTable extends AbstractStatisticsTable<Long> {

	private static final long serialVersionUID = 2792974148567590691L;

	public static final String COLUMN_QUERIES = "Queries";
	public static final String COLUMN_UNCACHEDQUERIES = "UnCachedQueries";
	public static final String COLUMN_LOADS = "Loads";
	public static final String COLUMN_FETCHES = "Fetches";
	public static final String COLUMN_MODIFICATIONS = "Modifications";

	public PrimaryStatisticsTable() {
		super(HISTORY_LENGTH, COLUMN_QUERIES, COLUMN_UNCACHEDQUERIES,
				COLUMN_LOADS, COLUMN_FETCHES, COLUMN_MODIFICATIONS);
	}

	protected PrimaryStatisticsTable(int maxEntries, String... columns) {
		super(maxEntries, columns);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return "Primary Statistics";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Long calculateSampleValue(Column column, AbstractStatisticsContext context) {
		final Map<Names, Object> attributes = context.getAttributes();
		final String name = column.getName();

		if (COLUMN_QUERIES.equals(name))
			return (Long) attributes.get(QueryExecutionCount) + (Long) attributes.get(QueryCacheHitCount);
		else if (COLUMN_UNCACHEDQUERIES.equals(name))
			return (Long) attributes.get(QueryExecutionCount);
		else if (COLUMN_LOADS.equals(name))
			return (Long) attributes.get(CollectionLoadCount) + (Long) attributes.get(EntityLoadCount);
		else if (COLUMN_FETCHES.equals(name))
			return (Long) attributes.get(CollectionFetchCount) + (Long) attributes.get(EntityFetchCount);
		else if (COLUMN_MODIFICATIONS.equals(name)) {
			return (Long) attributes.get(Names.CollectionRemoveCount) +
					(Long) attributes.get(Names.CollectionUpdateCount) +
					(Long) attributes.get(Names.EntityDeleteCount) +
					(Long) attributes.get(Names.EntityUpdateCount) +
					(Long) attributes.get(Names.EntityInsertCount);
		}

		return 0L;
	}
}
