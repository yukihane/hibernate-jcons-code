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
import net.sf.hibernate.jconsole.stats.SecondLevelCacheStatistics;
import net.sf.hibernate.jconsole.ui.widgets.*;

import java.util.Collections;
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
			new Column("Entity", "The entity java class.", Comparable.class),
			new Column("Performance", "<html>Displays the ratio between <i>lightweight</i> load <i>(without an additional DB query)</i>" +
					"<br/>and any operation that issues an additional read or write on the database.</html>", Comparable.class),

			new Column("Access Count", "The number of times the entity was accessed.", Long.class),
			new Column("Loads", "<html>The number of times the entity was loaded <i>without</i><br/>" +
					"requiring an additional DB query.</html>", Long.class),
			new Column("Fetches", "<html>The number of times that a <i>separate DB query</i><br/>" +
					"was required to retrieve the entity.</html>", Long.class),

			new Column("Optimistic Faults", "<html>The number of times a concurrent modification<br/>" +
					"caused an optimistic lock failure inside the DB.</html>", Long.class),
			new Column("Modifications", null, Comparable.class),
	};

	long maxLoaded;
	long maxModificationCount;
	Map<String, SecondLevelCacheStatistics> cacheStatistics = Collections.emptyMap();


	final EntityHighlighter highlighter = new EntityHighlighter();
	final NotAvailableBarTableCell loadNotAvailable = new NotAvailableBarTableCell(
			"This entity is always fetched using a separate DB query.");

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Vector toTableRow(String entity, EntityStatistics s) {
		Vector<Object> v = new Vector<Object>(COLUMNS.length);

		final SecondLevelCacheStatistics cs = cacheStatistics.get(entity);
		long cacheHits = cs == null ? 0 : cs.getHitCount();

		v.add(new TableCellJLabel(entity, null, highlighter));

		v.add(s.getLoadCount() + cacheHits == 0 ? loadNotAvailable : new EntityPerformanceTableCell(s, cacheHits));
		v.add(s.getLoadCount() + s.getFetchCount() + cacheHits);
		v.add(s.getLoadCount());
		v.add(s.getFetchCount());
		v.add(s.getOptimisticFailureCount());

		v.add(new ModificationsTableCell(maxModificationCount, s.getInsertCount(), s.getUpdateCount(), s.getDeleteCount()));

		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, EntityStatistics> toTableData(AbstractStatisticsContext context) {
		final Map<String, EntityStatistics> source = context.getEntityStatistics();

		cacheStatistics = context.getCacheStatistics();
		maxLoaded = maxModificationCount = 0;
		for (EntityStatistics s : source.values()) {
			maxLoaded = Math.max(maxLoaded, s.getLoadCount());
			maxModificationCount = Math.max(maxModificationCount, (s.getInsertCount() + s.getUpdateCount() + s.getDeleteCount()));
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
