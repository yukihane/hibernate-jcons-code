/*
 * Copyright (c) 2011
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

import static net.sf.hibernate.jconsole.AbstractStatisticsContext.HISTORY_LENGTH;
import static net.sf.hibernate.jconsole.stats.StatisticsUtil.toQueryPerformance;
import static net.sf.hibernate.jconsole.stats.StatisticsUtil.toTotalAverageTime;

/**
 * Implements a data table that records collection statistics.
 *
 * @author Juergen_Kellerer, 2010-09-20
 * @version 1.0
 */
public class QueryStatisticsTable extends AbstractStatisticsTable<Long> {

	private static final long serialVersionUID = 7974677503417064806L;

	public static final String COLUMN_PERFORMANCE = "Performance";
	public static final String COLUMN_DB_TIME = "DB Time";
	public static final String COLUMN_INVOCATIONS = "Invocations";
	public static final String COLUMN_ROWS_FETCHED = "Rows fetched";

	private String id, queryName;

	public QueryStatisticsTable() {
		super();
	}

	public QueryStatisticsTable(String queryName) {
		super(HISTORY_LENGTH, COLUMN_PERFORMANCE, COLUMN_DB_TIME, COLUMN_INVOCATIONS, COLUMN_ROWS_FETCHED);
		this.queryName = queryName;
		this.id = "Query Statistics :: " + queryName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Long calculateSampleValue(Column column, AbstractStatisticsContext context) {
		final String name = column.getName();
		final QueryStatistics stats = context.getQueryStatistics().get(queryName);

		if (stats != null) {
			if (COLUMN_DB_TIME.equals(name))
				return Math.round(toTotalAverageTime(stats) / 1000D);
			else if (COLUMN_PERFORMANCE.equals(name))
				return Math.round(toQueryPerformance(stats) * 100D);
			else if (COLUMN_INVOCATIONS.equals(name))
				return stats.getExecutionCount() + stats.getCacheHitCount();
			else if (COLUMN_ROWS_FETCHED.equals(name))
				return stats.getExecutionRowCount();
		}

		return 0L;
	}
}
