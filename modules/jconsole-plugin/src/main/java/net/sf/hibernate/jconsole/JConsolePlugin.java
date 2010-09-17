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

package net.sf.hibernate.jconsole;

import net.sf.hibernate.jconsole.ui.MainTab;
import net.sf.hibernate.jconsole.util.ClasspathUtil;
import net.sf.hibernate.jconsole.util.UIUtils;

import javax.management.InstanceNotFoundException;
import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Map;

/**
 * Is the entry point of the plugin.
 *
 * @author Juergen_Kellerer, 2009-11-18
 * @version 1.0
 */
public class JConsolePlugin extends com.sun.tools.jconsole.JConsolePlugin {

	static final String HIBERNATE_VALIDATION_CLASS = System.getProperty("hibernate.class",
			"org.hibernate.stat.CollectionStatistics");
	static final String HIBERNATE_CLASSPATH = System.getProperty("hibernate.classpath");
	static final String[] HIBERNATE_DEFAULT_SEARCHPATH =
			System.getProperty("hibernate.searchpath", ".;lib").split(";+");

	static final FilenameFilter HIBERNATE_FILTER = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			name = name.toLowerCase();
			return (!name.contains("hibernate-jconsole") && name.contains("hibernate") && name.endsWith(".jar")) ||
					new File(dir, name).isDirectory();
		}
	};

	// Add hibernate classpath

	static {
		String[] searchPath = HIBERNATE_CLASSPATH == null ?
				HIBERNATE_DEFAULT_SEARCHPATH : new String[]{HIBERNATE_CLASSPATH};

		for (String s : searchPath) {
			File file = new File(s);
			if (ClasspathUtil.addJars(file, false, HIBERNATE_FILTER))
				break;
		}
	}

	// Adjust tooltips to last longer (the default dismissal is to fast)

	static {
		ToolTipManager.sharedInstance().setDismissDelay(60 * 1000);
	}

	private class Updater extends SwingWorker<Object, Object> {
		@Override
		protected Object doInBackground() throws Exception {
			try {
				context.refreshAll();
			} catch (InstanceNotFoundException e) {
				mainTab.setHibernateAvailable(false);
			} catch (Exception e) {
				UIUtils.displayErrorMessage(null, e);
				throw e;
			}
			return null;
		}

		@Override
		protected void done() {
			for (JPanel panel : getTabs().values()) {
				if (panel instanceof Refreshable)
					((Refreshable) panel).refresh(context);
			}
		}
	}

	private final MainTab mainTab = new MainTab();
	private final Map<String, JPanel> tabs = Collections.singletonMap(MainTab.NAME, (JPanel) mainTab);
	private AbstractStatisticsContext context;

	public JConsolePlugin() {
		try {
			// Lookup the hibernate classes inside the system class loader, making them
			// available to the plugin class loader is not enough!
			ClassLoader.getSystemClassLoader().loadClass(HIBERNATE_VALIDATION_CLASS);
			try {
				context = (AbstractStatisticsContext)
						Class.forName("net.sf.hibernate.jconsole.hibernate.HibernateContext").newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException ignore) {
		}
		mainTab.setHibernateAvailable(context != null);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, JPanel> getTabs() {
		return tabs;
	}

	/**
	 * {@inheritDoc}
	 */
	public SwingWorker<?, ?> newSwingWorker() {
		if (context == null)
			return null;

		switch (getContext().getConnectionState()) {
			case CONNECTING:
			case DISCONNECTED:
				context.setConnection(null);
				return null;

			default:
				context.setConnection(getContext().getMBeanServerConnection());
				return new Updater();
		}
	}
}
