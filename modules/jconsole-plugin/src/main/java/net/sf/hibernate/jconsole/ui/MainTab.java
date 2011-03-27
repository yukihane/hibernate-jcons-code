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

import net.sf.hibernate.jconsole.ui.widgets.RefreshableJPanel;

import java.awt.*;

/**
 * Implements the main tab of the plugin.
 *
 * @author Juergen_Kellerer, 2009-11-18
 * @version 1.0
 */
public class MainTab extends RefreshableJPanel {

	public static final String NAME = "Hibernate Monitor";

	MainContent mainContent;
	HibernateNotFoundContent notFoundContent = new HibernateNotFoundContent();

	public MainTab() {
		super();
		setHibernateAvailable(false);
	}

	public void setHibernateAvailable(boolean enabled) {
		// Creating the mainContent "on-demand"!
		// Note: If the hibernate jars are not in the path, an attempt to
		// create an instance of main content results in a class cast exception...
		if (enabled && mainContent == null)
			mainContent = new MainContent();

		removeAll();
		add(BorderLayout.CENTER, enabled ? mainContent : notFoundContent);
	}
}
