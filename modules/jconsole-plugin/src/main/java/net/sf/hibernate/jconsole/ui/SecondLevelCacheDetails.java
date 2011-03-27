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

package net.sf.hibernate.jconsole.ui;

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.stats.SecondLevelCacheStatistics;
import net.sf.hibernate.jconsole.util.DataTable;

/**
 * Implements the details panel below the cache table.
 *
 * @author juergen, 27.03.11
 */
public class SecondLevelCacheDetails extends AbstractChartViewDetails<SecondLevelCacheStatistics> {

	/**
	 * Creates an instance of SecondLevelCacheDetails for the given table.
	 *
	 * @param cacheTable the table to create the details for.
	 */
	public SecondLevelCacheDetails(SecondLevelCacheTable cacheTable) {
		super(cacheTable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTable getDataTableFor(AbstractStatisticsContext context, Object selection) {
		return context.getCacheStatisticsTables().get(String.valueOf(selection));
	}
}
