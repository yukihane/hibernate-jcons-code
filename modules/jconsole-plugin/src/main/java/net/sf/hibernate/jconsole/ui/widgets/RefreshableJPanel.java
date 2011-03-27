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

package net.sf.hibernate.jconsole.ui.widgets;

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.Refreshable;

import javax.swing.*;
import java.awt.*;

/**
 * Is a panel that delegates any refresh calls to its children.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public class RefreshableJPanel extends JPanel implements Refreshable {

	public RefreshableJPanel(LayoutManager layout) {
		super(layout);
	}

	public RefreshableJPanel() {
		super(new BorderLayout());
	}

	/**
	 * {@inheritDoc}
	 */
	public void refresh(AbstractStatisticsContext context) {
		refreshComponents(getComponents(), context);
	}

	/**
	 * Refreshes the components with the given context.
	 *
	 * @param components the components to refresh.
	 * @param context	the context to refresh the components with.
	 */
	protected static void refreshComponents(Component[] components, AbstractStatisticsContext context) {
		for (Component component : components) {
			try {
				if (component instanceof Refreshable)
					((Refreshable) component).refresh(context);
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
}
