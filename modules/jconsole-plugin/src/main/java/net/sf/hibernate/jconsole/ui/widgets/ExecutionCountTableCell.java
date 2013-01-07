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

/**
 * Is a table cell that shows the execution count.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public class ExecutionCountTableCell extends LineBarTableCell {
	public ExecutionCountTableCell(long maxExecutionCount, long executionCount) {
		this(maxExecutionCount, executionCount, executionCount);
	}

	public ExecutionCountTableCell(long maxExecutionCount, long executionCount, long directExecutionCount) {
		super();
		setStyle(Style.GREEN);

		setValue(Math.max(0, (double) executionCount / (double) maxExecutionCount));
		setLabelValue(executionCount);
		setLabelFormat("%.0f");

		setToolTipText(String.format("<html>" +
				"Cached: <b>%d</b> / Direct: <b>%d</b></html>", executionCount - directExecutionCount, directExecutionCount));
	}
}
