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
 * Displays the relative query performance.
 *
 * @author Juergen_Kellerer, 21.11.2009
 */
public class QueryPerformanceTableCell extends PerformanceTableCell {

	public QueryPerformanceTableCell(double maxQueryPerformance, double queryPerformance) {
		super(maxQueryPerformance, Math.max(0D, maxQueryPerformance - queryPerformance));
		setToolTipText(String.format("<html>" +
				"Average time per invocation: <b>%.3f</b>s</html>", msToSeconds((long) Math.ceil(queryPerformance))));
	}
}
