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

import java.text.NumberFormat;

/**
 * Implements the vertical axis of the chart.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public class NumberAxis extends ChartAxis {

	private static final long serialVersionUID = -7950432156415559446L;

	private NumberFormat numberFormat = NumberFormat.getNumberInstance();


	/**
	 * Constructs a new number axis for the given data table.
	 *
	 * @param dataTable The datatable to construct the number axis for.
	 */
	public NumberAxis(DataTable dataTable) {
		super(20, Orientation.vertical, dataTable.getMinValue(), dataTable.getMaxValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getAxisLabel(double value) {
		numberFormat.setMaximumFractionDigits(
				value < 10 ? 6 : (value < 100 ? 4 : (value < 10000 ? 2 : 0)));
		numberFormat.setMaximumIntegerDigits(8);
		return numberFormat.format(value);
	}

	/**
	 * Returns the numberformat instance that is used to format axis lables.
	 *
	 * @return the numberformat instance that is used to format axis lables.
	 */
	public NumberFormat getNumberFormat() {
		return numberFormat;
	}
}
