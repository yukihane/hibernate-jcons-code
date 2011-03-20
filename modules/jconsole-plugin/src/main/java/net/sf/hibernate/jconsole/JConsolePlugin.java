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

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.swing.*;
import java.util.*;

/**
 * Is the entry point of the plugin.
 *
 * @author Juergen_Kellerer, 2009-11-18
 * @version 1.0
 */
public class JConsolePlugin extends com.sun.tools.jconsole.JConsolePlugin {

	// Adjust tooltips to last longer (the default dismissal is to fast)

	static {
		ToolTipManager.sharedInstance().setDismissDelay(60 * 1000);
	}

	private boolean instanceNotFoundReported;

	private class Updater extends SwingWorker<Object, Object> {
		@Override
		protected Object doInBackground() throws Exception {
			for (Map.Entry<String, AbstractStatisticsContext> entry : contexts.entrySet()) {
				try {
					entry.getValue().refresh();
				} catch (InstanceNotFoundException e) {
					tabs.get(entry.getKey()).setHibernateAvailable(false);
					if (!instanceNotFoundReported) {
						e.printStackTrace();
						instanceNotFoundReported = true;
					}
				}
			}
			return null;
		}

		@Override
		protected void done() {
			try {
				for (Map.Entry<String, MainTab> entry : tabs.entrySet()) {
					AbstractStatisticsContext context = contexts.get(entry.getKey());
					if (context != null)
						entry.getValue().refresh(context);
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	private final Map<String, MainTab> tabs = new LinkedHashMap<String, MainTab>();
	private final Map<String, AbstractStatisticsContext> contexts = new HashMap<String, AbstractStatisticsContext>();

	/**
	 * Constructs the plugin.
	 */
	public JConsolePlugin() {
		super();

		List<AbstractStatisticsContext> statisticsContexts = AbstractStatisticsContext.getAvailableContexts();
		for (AbstractStatisticsContext context : statisticsContexts) {
			String key = addMainTab(new MainTab());
			contexts.put(key, context);
		}

		if (statisticsContexts.isEmpty()) {
			MainTab mt = new MainTab();
			mt.setHibernateAvailable(false);
			addMainTab(mt);
		}
	}

	private String addMainTab(MainTab mainTab) {
		String key = MainTab.NAME;

		int i = 2;
		while (tabs.containsKey(key))
			key = MainTab.NAME.concat(" " + i);

		tabs.put(key, mainTab);
		return key;
	}

	/**
	 * Returns a map of statistics contexts that correspond to the tabs.
	 *
	 * @return a map of statistics contexts that correspond to the tabs.
	 */
	public Map<String, AbstractStatisticsContext> getStatisticsContexts() {
		return Collections.unmodifiableMap(contexts);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, JPanel> getTabs() {
		return Collections.unmodifiableMap((Map) tabs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SwingWorker<?, ?> newSwingWorker() {
		switch (getContext().getConnectionState()) {
			case CONNECTING:
			case DISCONNECTED:
				for (AbstractStatisticsContext context : contexts.values())
					context.setConnection(null);
				return null;

			default:
				MBeanServerConnection connection = getContext().getMBeanServerConnection();
				for (Map.Entry<String, AbstractStatisticsContext> entry : contexts.entrySet()) {
					AbstractStatisticsContext context = entry.getValue();
					context.setConnection(connection);
					tabs.get(entry.getKey()).setHibernateAvailable(context.isEnabled());
				}

				return contexts.isEmpty() ? null : new Updater();
		}
	}
}
