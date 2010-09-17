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
import net.sf.hibernate.jconsole.formatters.EntityHighlighter;
import net.sf.hibernate.jconsole.stats.EntityStatistics;
import net.sf.hibernate.jconsole.ui.widgets.*;

import java.util.Map;
import java.util.Vector;

/**
 * Implements a JTable containing all hibernate entities.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public class EntitiesTable extends AbstractRefreshableJTable<EntityStatistics> {

	private static final Column[] COLUMNS = {
			new Column("Entity", "The entity class.", Comparable.class),
			new Column("Performance", "The performance of the entity. Less DB loads lead to higher performance.",
					Comparable.class),
			new Column("Loads", "The number of entity loads (from cache, query or DB)", Long.class),
			new Column("Optimistic Faults", "The amount of optimistic lookup failures.", Long.class),
			new Column("Modifications", null, Comparable.class),
	};

	long maxLoaded;
	long maxModificationCount;

	EntityHighlighter highlighter = new EntityHighlighter();
	NotAvailableBarTableCell notAvailable = new NotAvailableBarTableCell();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Vector toTableRow(String entity, EntityStatistics s) {
		Vector<Object> v = new Vector<Object>(COLUMNS.length);

		v.add(new TableCellJLabel(entity, null, highlighter));

		v.add(s.getLoadCount() == 0 ? notAvailable : new EntityPerformanceTableCell(s));
		v.add(s.getLoadCount());
		v.add(s.getOptimisticFailureCount());

		v.add(new ModificationsTableCell(maxModificationCount,
				s.getInsertCount(), s.getUpdateCount(), s.getDeleteCount()));

		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, EntityStatistics> toTableData(AbstractStatisticsContext context) {
		Map<String, EntityStatistics> source = context.getEntityStatistics();

		maxLoaded = maxModificationCount = 0;
		for (EntityStatistics s : source.values()) {
			maxLoaded = Math.max(maxLoaded, s.getLoadCount());
			maxModificationCount = Math.max(maxModificationCount,
					(s.getInsertCount() + s.getUpdateCount() + s.getDeleteCount()));
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
