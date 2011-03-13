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

package net.sf.hibernate.jconsole.stats;

import net.sf.hibernate.jconsole.util.DataSampler;
import net.sf.hibernate.jconsole.util.TimeboxedDataSampler;

/**
 * Extends the primary table to record the data per minute instead of deltas to the absolute counts.
 *
 * @author Juergen_Kellerer, 2010-09-20
 * @version 1.0
 */
public class PrimaryPerMinuteStatisticsTable extends PrimaryStatisticsTable {

	private static final long serialVersionUID = 3893553482534224206L;

	private static final long MS_PER_MINUTE = 60 * 1000;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSampler<Long> createSampler(Column column) {
		return new TimeboxedDataSampler<Long>(MS_PER_MINUTE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateColumnValue(Column column, DataSampler<Long> sampler) {
		return sampler.getSum();
	}
}
