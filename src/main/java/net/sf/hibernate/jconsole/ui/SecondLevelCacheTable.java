/*
 * Copyright (c) 2009
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

import net.sf.hibernate.jconsole.HibernateContext;
import net.sf.hibernate.jconsole.formatters.EntityHighlighter;
import net.sf.hibernate.jconsole.stats.SecondLevelCacheStatistics;
import net.sf.hibernate.jconsole.ui.widgets.AbstractRefreshableJTable;
import net.sf.hibernate.jconsole.ui.widgets.HitrateTableCell;
import net.sf.hibernate.jconsole.ui.widgets.TableCellJLabel;

import java.util.Map;
import java.util.Vector;

/**
 * Implements a JTable containing all hibernate cache regions.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public class SecondLevelCacheTable extends AbstractRefreshableJTable<SecondLevelCacheStatistics> {

	private static final Column[] COLUMNS = {
			new Column("Cache Region", null, Comparable.class),
			new Column("Hitrate", null, Comparable.class),
			new Column("Hits", null, Long.class),
			new Column("Misses", null, Long.class),
			new Column("Puts", null, Long.class),
	};

	private EntityHighlighter highlighter = new EntityHighlighter();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Vector toTableRow(String entity, SecondLevelCacheStatistics s) {
		Vector<Object> v = new Vector<Object>(COLUMNS.length);

		v.add(new TableCellJLabel(entity, null, highlighter));
		v.add(new HitrateTableCell(s.getHitCount(), s.getMissCount(), s.getPutCount()));

		v.add(s.getHitCount());
		v.add(s.getMissCount());
		v.add(s.getPutCount());

		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, SecondLevelCacheStatistics> toTableData(HibernateContext context) {
		return context.getCacheStatistics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Column[] getColumns() {
		return COLUMNS;
	}
}
