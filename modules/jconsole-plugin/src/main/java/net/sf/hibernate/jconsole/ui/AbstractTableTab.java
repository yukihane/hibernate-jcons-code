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

package net.sf.hibernate.jconsole.ui;

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.Refreshable;
import net.sf.hibernate.jconsole.ui.widgets.RefreshableJPanel;
import net.sf.hibernate.jconsole.ui.widgets.RefreshableJSplitPane;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract implementations of a tab used to display a table in a tab.
 *
 * @author juergen_kellerer, 2011-03-27
 */
public class AbstractTableTab extends RefreshableJPanel {

	protected RefreshableJSplitPane splitPane;
	protected Refreshable table;

	public AbstractTableTab() {
		super(new BorderLayout());
	}

	void init(AbstractChartViewDetails tableDetails) {
		table = tableDetails.getTable();

		splitPane = new RefreshableJSplitPane(JSplitPane.VERTICAL_SPLIT,
				new JScrollPane(tableDetails.getTable()), tableDetails);

		splitPane.setResizeWeight(1);

		add(BorderLayout.CENTER, splitPane);
	}

	@Override
	public void refresh(AbstractStatisticsContext context) {
		super.refresh(context);
		table.refresh(context);
	}
}
