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
import net.sf.hibernate.jconsole.formatters.CollectionHighlighter;
import net.sf.hibernate.jconsole.stats.CollectionStatistics;
import net.sf.hibernate.jconsole.stats.SecondLevelCacheStatistics;
import net.sf.hibernate.jconsole.ui.widgets.*;

import java.util.Collections;
import java.util.Map;
import java.util.Vector;

/**
 * Implements a JTable containing all hibernate collections.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public class CollectionsTable extends AbstractRefreshableJTable<CollectionStatistics> {

	private static final Column[] COLUMNS = {
			new Column("Collection Role Name", null, Comparable.class),

			new Column("Performance", "<html>Displays the ratio between <i>lightweight</i> load <i>(without an additional DB query)</i>" +
					"<br/>and any operation that issues an additional read or write on the database.</html>", Comparable.class),

			new Column("Access Count", "The number of times the collection was accessed.", Long.class),
			new Column("Loads", "<html>The number of times the collection was loaded<br/>" +
					"<i>without</i> requiring an additional DB query.</html>", Long.class),
			new Column("Fetches", "<html>The number of times that a <i>separate DB query</i><br/>" +
					"was required to retrieve the collection.</html>", Long.class),

			new Column("Recreations", "<html>The number of times a full deletion &amp; full (re-)insertion was performed.<br/>" +
					"(<i>heavyweight operation</i>)</html>", Long.class),
			new Column("Modifications", null, Comparable.class),
	};

	long maxModificationCount;
	Map<String, SecondLevelCacheStatistics> cacheStatistics = Collections.emptyMap();

	final CollectionHighlighter highlighter = new CollectionHighlighter();
	final NotAvailableBarTableCell loadNotAvailable = new NotAvailableBarTableCell(
			"This collection is always fetched using a separate DB query.");

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Vector toTableRow(String key, CollectionStatistics s) {
		Vector<Object> v = new Vector<Object>(COLUMNS.length);

		final SecondLevelCacheStatistics cs = cacheStatistics.get(key);
		long cacheHits = cs == null ? 0 : cs.getHitCount();

		v.add(new TableCellJLabel(key, null, highlighter));

		v.add(s.getLoadCount() + cacheHits == 0 ? loadNotAvailable : new EntityPerformanceTableCell(s, cacheHits));
		v.add(s.getLoadCount() + s.getFetchCount() + cacheHits);
		v.add(s.getLoadCount());
		v.add(s.getFetchCount());
		v.add(s.getRecreateCount());

		v.add(new ModificationsTableCell(maxModificationCount, 0, s.getUpdateCount(), s.getRemoveCount()));

		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, CollectionStatistics> toTableData(AbstractStatisticsContext context) {
		final Map<String, CollectionStatistics> source = context.getCollectionStatistics();

		cacheStatistics = context.getCacheStatistics();
		maxModificationCount = 0;
		for (CollectionStatistics s : source.values()) {
			maxModificationCount = Math.max(maxModificationCount, (s.getRemoveCount() + s.getUpdateCount()));
		}

		return source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Column[] getColumns() {
		return COLUMNS;
	}
}
