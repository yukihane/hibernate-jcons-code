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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

/**
 * Is a styled JSplitPane that delegates any refresh calls to its children.
 *
 * @author Juergen_Kellerer, 2009-11-23
 * @version 1.0
 */
public class RefreshableJSplitPane extends JSplitPane implements Refreshable {
	/**
	 * Constructs a new refreshable split pane with empty JPanels and the given orientation.
	 *
	 * @param orientation The orientation of the divider.
	 */
	public RefreshableJSplitPane(int orientation) {
		this(orientation, new JPanel(), new JPanel());
	}

	/**
	 * Constructs a new refreshable split pane with empty JPanels and the given orientation.
	 *
	 * @param orientation		  The orientation of the divider.
	 * @param leftTopComponent	 The left or top component inside the panel.
	 * @param rightBottomComponent The right or bottom component inside the panel.
	 */
	public RefreshableJSplitPane(int orientation, Component leftTopComponent, Component rightBottomComponent) {
		super(orientation, true, leftTopComponent, rightBottomComponent);

		leftTopComponent.setMinimumSize(new Dimension(0, 0));
		rightBottomComponent.setMinimumSize(new Dimension(0, 0));

		setOpaque(false);
		setDividerSize(8);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		// Make sure the divider is transparent.
		setUI(new BasicSplitPaneUI() {
			@Override
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					@Override
					public Border getBorder() {
						return null;
					}
				};
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void refresh(AbstractStatisticsContext context) {
		RefreshableJPanel.refreshComponents(getComponents(), context);
	}
}
