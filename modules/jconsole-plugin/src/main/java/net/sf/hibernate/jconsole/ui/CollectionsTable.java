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
import net.sf.hibernate.jconsole.ui.widgets.*;

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
			new Column("Collection Rolename", null, Comparable.class),
			new Column("Performance", "The performance of the collection. " +
					"Less DB loads lead to higher performance.", Comparable.class),
			new Column("Loads", "The number of loads (from cache, query or DB)", Long.class),
			new Column("Recreations", null, Long.class),
			new Column("Modifications", null, Comparable.class),
	};

	long maxModificationCount;

	CollectionHighlighter highlighter = new CollectionHighlighter();
	NotAvailableBarTableCell notAvailable = new NotAvailableBarTableCell();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Vector toTableRow(String key, CollectionStatistics s) {
		Vector<Object> v = new Vector<Object>(COLUMNS.length);

		v.add(new TableCellJLabel(key, null, highlighter));

		v.add(s.getLoadCount() == 0 ? notAvailable : new EntityPerformanceTableCell(s));
		v.add(s.getLoadCount());
		v.add(s.getRecreateCount());

		v.add(new ModificationsTableCell(maxModificationCount, 0, s.getUpdateCount(), s.getRemoveCount()));

		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, CollectionStatistics> toTableData(AbstractStatisticsContext context) {
		maxModificationCount = 0;

		Map<String, CollectionStatistics> source = context.getCollectionStatistics();
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
