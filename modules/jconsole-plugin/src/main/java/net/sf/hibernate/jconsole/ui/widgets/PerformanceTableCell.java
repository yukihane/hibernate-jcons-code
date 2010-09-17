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

import static net.sf.hibernate.jconsole.ui.widgets.AbstractJTable.round;

/**
 * Is a table cell that shows the timinig (avg, max and min time) when
 * considering the cache rate.
 *
 * @author Juergen_Kellerer, 21.11.2009
 */
public class PerformanceTableCell extends LineBarTableCell {

	public PerformanceTableCell(double maxPerformance, double performance) {
		setStyle(Style.ORANGE);

		double p = performance / maxPerformance;

		setValue(p);
		setLabelValue(round(p * 100D));
		setLabelFormat("%2.2f%%");
	}
}
