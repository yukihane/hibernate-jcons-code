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

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.stats.EntityStatistics;
import net.sf.hibernate.jconsole.util.DataTable;

/**
 * Implements the details panel below the query table.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public class EntityDetails extends AbstractChartViewDetails<EntityStatistics> {

	/**
	 * Creates an instance of EntityDetails for the given table.
	 *
	 * @param entitiesTable the table to create the details for.
	 */
	public EntityDetails(EntitiesTable entitiesTable) {
		super(entitiesTable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTable getDataTableFor(AbstractStatisticsContext context, Object selection) {
		return context.getEntityStatisticsTables().get(String.valueOf(selection));
	}
}