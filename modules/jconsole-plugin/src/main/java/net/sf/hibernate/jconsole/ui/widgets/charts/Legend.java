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

package net.sf.hibernate.jconsole.ui.widgets.charts;

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.ui.widgets.RefreshableJPanel;
import net.sf.hibernate.jconsole.util.DataTable;
import net.sf.hibernate.jconsole.util.UIUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Creates a legend for the given line chart panel.
 *
 * @author Juergen_Kellerer, 2009-11-23
 * @version 1.0
 */
public class Legend extends RefreshableJPanel {

	static final int SPACING = 4;

	private AbstractChart2D parent;
	private List<DataTable.Column> columns;

	/**
	 * Contructs a new legend for the given chart.
	 *
	 * @param chart The chart to construct the legend for.
	 */
	public Legend(AbstractChart2D chart) {
		this.parent = chart;
		setLayout(new SpringLayout());
		setPreferredSize(new Dimension(100, 100));
		Border innerBorder = new CompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1),
				new EmptyBorder(SPACING, SPACING, SPACING, SPACING));
		setBorder(new CompoundBorder(new EmptyBorder(8, 0, 19, 0), innerBorder));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(AbstractStatisticsContext context) {
		super.refresh(context);

		List<DataTable.Column> columns = parent.getDataTable(context).getColumns();
		if (this.columns == null || !this.columns.equals(columns)) {
			this.columns = columns;

			removeAll();
			Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			for (DataTable.Column column : columns) {
				LegendJLabel l = new LegendJLabel(parent.getColorForColumn(column), parent.getLegendForColumn(column));
				l.setCursor(hand);
				l.setDisabled(!parent.isColumnVisible(column));
				l.addMouseListener(new ColumnVisibilityToggle(column));
				add(l);
			}

			UIUtils.makeCompactGrid(this, columns.size(), 1, 0, 0, SPACING, SPACING);
		}
	}

	private class ColumnVisibilityToggle implements MouseListener {

		private DataTable.Column column;

		private ColumnVisibilityToggle(DataTable.Column column) {
			this.column = column;
		}

		public void mouseClicked(MouseEvent e) {
			LegendJLabel label = (LegendJLabel) e.getSource();
			parent.setColumnVisible(column, !parent.isColumnVisible(column));
			label.setDisabled(!label.isDisabled());
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
}
