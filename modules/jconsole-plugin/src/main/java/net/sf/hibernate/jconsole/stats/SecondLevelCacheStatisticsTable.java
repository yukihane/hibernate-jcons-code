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
import static net.sf.hibernate.jconsole.stats.StatisticsUtil.toRatio;

/**
 * Implements a data table that records collection statistics.
 *
 * @author Juergen_Kellerer, 2010-09-20
 * @version 1.0
 */
public class SecondLevelCacheStatisticsTable extends AbstractStatisticsTable<Long> {

	private static final long serialVersionUID = 7974677503417064806L;

	public static final String COLUMN_HITRATE = "Hitrate";
	public static final String COLUMN_HITS = "Hits";
	public static final String COLUMN_MISSES = "Misses";
	public static final String COLUMN_MODIFICATIONS = "Modifications";
	public static final String COLUMN_CACHE_SIZE = "Cache Size";

	private String id, cacheRegionName;

	public SecondLevelCacheStatisticsTable() {
		super();
	}

	public SecondLevelCacheStatisticsTable(String cacheRegionName) {
		super(HISTORY_LENGTH, COLUMN_HITRATE, COLUMN_HITS,
				COLUMN_MISSES, COLUMN_MODIFICATIONS, COLUMN_CACHE_SIZE);
		this.cacheRegionName = cacheRegionName;
		this.id = "2ndLevelCache Statistics :: " + cacheRegionName;
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
		final SecondLevelCacheStatistics stats = context.getCacheStatistics().get(cacheRegionName);

		if (stats != null) {
			if (COLUMN_HITS.equals(name))
				return stats.getHitCount();
			else if (COLUMN_HITRATE.equals(name))
				return Math.round(toRatio(stats.getHitCount(), stats.getMissCount()) * 100D);
			else if (COLUMN_MISSES.equals(name))
				return stats.getMissCount();
			else if (COLUMN_MODIFICATIONS.equals(name))
				return stats.getPutCount();
			else if (COLUMN_CACHE_SIZE.equals(name))
				return stats.getElementCountInMemory() + stats.getElementCountOnDisk();
		}

		return 0L;
	}
}
