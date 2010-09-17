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

package net.sf.hibernate.jconsole.ui;

import net.sf.hibernate.jconsole.HibernateContext;
import net.sf.hibernate.jconsole.ui.widgets.RefreshableJPanel;
import net.sf.hibernate.jconsole.ui.widgets.RefreshableJSplitPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Contains the Main content shown on the main tab.
 *
 * @author Juergen_Kellerer, 21.11.2009
 * @version 1.0
 */
public class MainContent extends RefreshableJPanel {

	MainLoadChart loadChart = new MainLoadChart();
	JTabbedPane tabs = new JTabbedPane();
	RefreshableJSplitPane splitPane = new RefreshableJSplitPane(JSplitPane.VERTICAL_SPLIT, loadChart, tabs);

	public MainContent() {
		add(BorderLayout.CENTER, splitPane);

		loadChart.setBorder(new EmptyBorder(4, 4, 4, 4));
		loadChart.setPreferredSize(new Dimension(400, 130));

		tabs.add(QueriesTab.NAME, new QueriesTab());
		tabs.add(CollectionsTab.NAME, new CollectionsTab());
		tabs.add(EntitiesTab.NAME, new EntitiesTab());
		tabs.add(SecondLevelCacheTab.NAME, new SecondLevelCacheTab());
	}

	/**
	 * {@inheritDoc}
	 */
	public void refresh(HibernateContext context) {
		super.refresh(context);
		refreshComponents(tabs.getComponents(), context);
	}
}
