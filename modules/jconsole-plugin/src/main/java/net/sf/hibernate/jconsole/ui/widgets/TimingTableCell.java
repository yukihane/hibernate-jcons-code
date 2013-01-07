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

import static net.sf.hibernate.jconsole.ui.widgets.AbstractJTable.msToSeconds;

/**
 * Is a table cell that shows the timing (avg, max and min time).
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public class TimingTableCell extends LineBarTableCell {
	public TimingTableCell(double maxTotalAvg, double totalAvg, long avg, long max, long min) {
		super();
		setStyle(Style.GREEN);

		setValue(Math.max(0, totalAvg / maxTotalAvg));
		setLabelValue(msToSeconds((long) totalAvg));
		setLabelFormat("%2.3f s");

		setToolTipText(String.format("<html>" +
				"Single execution time: <b>%2.3f</b>s<br/>" +
				"(min: <b>%2.3f</b>s / max: <b>%2.3f</b>s)</html>",
				msToSeconds(avg), msToSeconds(min), msToSeconds(max)));
	}
}
