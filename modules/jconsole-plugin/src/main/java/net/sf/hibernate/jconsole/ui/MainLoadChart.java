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

package net.sf.hibernate.jconsole.ui;

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.ui.widgets.charts.AbstractChart2D;
import net.sf.hibernate.jconsole.ui.widgets.charts.Chart2DPanel;
import net.sf.hibernate.jconsole.util.DataTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the load chart shown on top of the child tabs in the main page.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public class MainLoadChart extends Chart2DPanel {

	static Map<String, String> labels = new HashMap<String, String>();

	static {
		labels.put("UnCachedQueries", "Queries on DB");
	}

	public MainLoadChart() {
		super(new AbstractChart2D() {
			{
				setFirstColorIndex(4);
			}

			@Override
			protected DataTable getDataTable(AbstractStatisticsContext context) {
				return context.getStatisticsTable();
			}

			@Override
			protected String getLegendForColumn(DataTable.Column column) {
				String name = labels.get(column.getName());
				return name != null ? name : column.getName();
			}
		});
	}
}
