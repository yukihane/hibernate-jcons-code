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

/**
 * Implements a data table that records collection statistics.
 *
 * @author Juergen_Kellerer, 2010-09-20
 * @version 1.0
 */
public class EntityStatisticsTable extends AbstractStatisticsTable<Long> {

	private static final long serialVersionUID = 7974677503417064806L;

	public static final String COLUMN_LOADS = "Loads";
	public static final String COLUMN_FETCHES = "Fetches";
	public static final String COLUMN_MODIFICATIONS = "Modifications";

	private String id, entityName;

	public EntityStatisticsTable() {
		super();
	}

	public EntityStatisticsTable(String entityName) {
		super(HISTORY_LENGTH, COLUMN_LOADS, COLUMN_FETCHES, COLUMN_MODIFICATIONS);
		this.entityName = entityName;
		this.id = "Entity Statistics :: " + entityName;
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
		final EntityStatistics stats = context.getEntityStatistics().get(entityName);

		if (stats != null) {
			if (COLUMN_LOADS.equals(name))
				return stats.getLoadCount();
			else if (COLUMN_FETCHES.equals(name))
				return stats.getFetchCount();
			else if (COLUMN_MODIFICATIONS.equals(name))
				return stats.getInsertCount() + stats.getUpdateCount() + stats.getDeleteCount();
		}

		return 0L;
	}
}
