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

package net.sf.hibernate.jconsole.ui.widgets;

import net.sf.hibernate.jconsole.stats.CollectionStatistics;
import net.sf.hibernate.jconsole.stats.EntityStatistics;

/**
 * Displays the relative entity building performance.
 * <p/>
 * Entities have more performance when they are just loaded not fetched.
 *
 * @author Juergen_Kellerer, 21.11.2009
 */
public class EntityPerformanceTableCell extends PerformanceTableCell {

	public EntityPerformanceTableCell(CollectionStatistics s, long cacheHits) {
		this(s.getLoadCount() + s.getFetchCount() + cacheHits,
				Math.max(0, (s.getLoadCount() + cacheHits) - (s.getRemoveCount() + s.getRecreateCount() + s.getUpdateCount())));
	}

	public EntityPerformanceTableCell(EntityStatistics s, long cacheHits) {
		this(s.getLoadCount() + s.getFetchCount() + cacheHits,
				Math.max(0, (s.getLoadCount() + cacheHits) - (s.getDeleteCount() + s.getInsertCount() + s.getUpdateCount())));
	}

	public EntityPerformanceTableCell(double totalLoaded, double fastLoads) {
		super(totalLoaded, fastLoads);
		setToolTipText(String.format("<html>" +
				"Accessed: <b>%.0f</b> / additional DB queries: <b>%.0f</b></html>", totalLoaded, totalLoaded - fastLoads));
	}
}
