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

package net.sf.hibernate.jconsole.ui;

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.formatters.QueryHighlighter;
import net.sf.hibernate.jconsole.formatters.ToolTipQueryHighlighter;
import net.sf.hibernate.jconsole.stats.QueryStatistics;
import net.sf.hibernate.jconsole.ui.widgets.*;

import java.util.Map;
import java.util.Vector;

import static net.sf.hibernate.jconsole.ui.widgets.HitrateTableCell.toHitRate;
import static net.sf.hibernate.jconsole.ui.widgets.QueryPerformanceTableCell.toQueryPerformance;

/**
 * Implements a JTable containing all hibernate queries.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public class QueriesTable extends AbstractRefreshableJTable<QueryStatistics> {

	static final Column[] COLUMNS = {
			new Column("Query", "The HQL query.", Comparable.class),
			new Column("Cached", "The percentage of queries that were retrieved from cache rather than the DB.",
					Comparable.class),
			new Column("Performance",
					"The relative performance, calculated out of average execution time and execution count.",
					Comparable.class),
			new Column("DB Time", "The total average time spent inside the DB.", Comparable.class),
			new Column("Invocations", "The total amount of invocations (cached & direct).", Comparable.class),
			new Column("Rows fetched", "Is the total of rows returned from the DB.", Long.class),
	};

	static double toTotalAverageTime(QueryStatistics statistics) {
		double avg = statistics.getExecutionAvgTime() == 0 ? 0.1D : statistics.getExecutionAvgTime();
		return ((double) statistics.getExecutionCount() * avg);
	}

	long maxExecutionCount;
	double maxQueryPerformance;
	double maxTotalAverageTime;

	QueryHighlighter highlighter = new QueryHighlighter();
	ToolTipQueryHighlighter toolTipHighlighter = new ToolTipQueryHighlighter();
	NotAvailableBarTableCell notAvailable = new NotAvailableBarTableCell();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Vector toTableRow(String key, QueryStatistics s) {
		Vector<Object> v = new Vector<Object>(COLUMNS.length);

		v.add(new TableCellJLabel(key, toolTipHighlighter.highlight(key), highlighter));

		if (s.getCachePutCount() == 0 && s.getCacheHitCount() == 0)
			v.add(notAvailable);
		else
			v.add(new HitrateTableCell(s.getCacheHitCount(), s.getCacheMissCount(), s.getCachePutCount()));


		double databaseTime = toTotalAverageTime(s);
		double cacheHitRate = toHitRate(s.getCacheHitCount(), s.getCacheMissCount());

		v.add(new QueryPerformanceTableCell(maxQueryPerformance, toQueryPerformance(s)));

		v.add(new TimingTableCell(maxTotalAverageTime, databaseTime,
				s.getExecutionAvgTime(), s.getExecutionMaxTime(), s.getExecutionMinTime()));

		v.add(new ExecutionCountTableCell(maxExecutionCount,
				s.getExecutionCount() + s.getCacheHitCount(), s.getExecutionCount()));
		v.add(s.getExecutionRowCount());

		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, QueryStatistics> toTableData(AbstractStatisticsContext context) {
		maxTotalAverageTime = maxQueryPerformance = maxExecutionCount = 0;

		for (QueryStatistics s : context.getQueryStatistics().values()) {
			maxExecutionCount = Math.max(maxExecutionCount, s.getExecutionCount() + s.getCacheHitCount());
			maxTotalAverageTime = Math.max(maxTotalAverageTime, toTotalAverageTime(s));
			maxQueryPerformance = Math.max(maxQueryPerformance, toQueryPerformance(s));
		}

		return context.getQueryStatistics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Column[] getColumns() {
		return COLUMNS;
	}
}
