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

package net.sf.hibernate.jconsole.ui.widgets.charts;

import net.sf.hibernate.jconsole.util.DataTable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements a chart axis that displays the time.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public class TimeAxis extends ChartAxis {

	private static final long serialVersionUID = -5530450044150489900L;

	/**
	 * Defines the date format to use when formatting time in relation
	 * to the total time range.
	 */
	enum Format {
		//MinutesFormat("mm:ss", 30 * 60 * 1000),
		HourFormat("HH:mm:ss", 12 * 60 * 60 * 1000),
		DayFormat("MM-dd HH:mm", -1),;

		String pattern;
		long maxTimeRange;

		Format(String pattern, int maxTimeRange) {
			this.pattern = pattern;
			this.maxTimeRange = maxTimeRange;
		}

		DateFormat toDateFormat() {
			return new SimpleDateFormat(pattern);
		}
	}

	private DateFormat dateFormat;

	/**
	 * Constructs a new time axis for the given data table.
	 *
	 * @param table The datatable to construct the time axis for.
	 */
	public TimeAxis(DataTable table) {
		super(40, Orientation.horizontal, table.getMinTimestamp(), table.getMaxTimestamp());
		long timeRange = (long) getAxisRange();
		for (Format format : Format.values())
			if (format.maxTimeRange == -1 || format.maxTimeRange > timeRange) {
				dateFormat = format.toDateFormat();
				break;
			}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getAxisLabel(double value) {
		return dateFormat.format(new Date((long) value));
	}

	/**
	 * Returns the date format instance that is used by this axis to format lables.
	 *
	 * @return the date format instance that is used by this axis to format lables.
	 */
	public DateFormat getDateFormat() {
		return dateFormat;
	}
}
