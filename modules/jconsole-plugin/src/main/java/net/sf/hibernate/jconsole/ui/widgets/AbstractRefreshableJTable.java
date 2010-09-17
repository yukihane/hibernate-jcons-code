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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Map;

/**
 * Implements the refresh logic and extends the rendering logic.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public abstract class AbstractRefreshableJTable<E> extends AbstractJTable<E> implements Refreshable {

	static final int INITIAL_COLUMN_WIDTH = 100;
	static final int INITIAL_NUMERIC_COLUMN_WIDTH = 60;

	/**
	 * Overrides the default table renderer to add tooltips and custom components in the table cells.
	 */
	protected DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

		NumberFormat numberFormat = NumberFormat.getInstance();

		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			boolean valueRendersItself = value instanceof JComponent;
			Object valueToRender = (value instanceof Number ? numberFormat.format(value) :
					(valueRendersItself ? "" : value));

			JComponent cellRenderer = (JComponent) super.getTableCellRendererComponent(table, valueToRender,
					isSelected, hasFocus, row, column);

			if (valueRendersItself) {
				JComponent component = (JComponent) value;
				component.setForeground(cellRenderer.isForegroundSet() ? cellRenderer.getForeground() : null);
				component.setBackground(cellRenderer.isBackgroundSet() ? cellRenderer.getBackground() : null);
				component.setBorder(cellRenderer.getBorder());
				cellRenderer = component;
			} else {
				setToolTipText(value instanceof String ? getToolTip((String) value, column) : null);
				setHorizontalAlignment(value instanceof Number ? SwingConstants.TRAILING : SwingConstants.LEADING);
			}

			return cellRenderer;
		}
	};

	protected boolean initial = true;

	/**
	 * Returns the tool tip for the specified row or null if no tooltip is available.
	 *
	 * @param row	The row to return the tooltip for.
	 * @param column The current column index.
	 * @return The tooltip for the row or null if no tooltip should be shown.
	 */
	protected String getToolTip(String row, int column) {
		return null;
	}

	/**
	 * Returns a map of row data.
	 *
	 * @param context The data source.
	 * @return A map of rows to show inside the table.
	 */
	protected abstract Map<String, E> toTableData(AbstractStatisticsContext context);

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void refresh(AbstractStatisticsContext context) {
		refresh(toTableData(context));

		if (initial) {
			TableColumn firstColumn = null;
			TableColumnModel columnModel = getColumnModel();

			Enumeration<TableColumn> columns = columnModel.getColumns();
			while (columns.hasMoreElements()) {
				TableColumn c = columns.nextElement();
				if (firstColumn == null)
					firstColumn = c;

				c.setCellRenderer(renderer);

				int width = Number.class.isAssignableFrom(getColumns()[c.getModelIndex()].getType()) ?
						INITIAL_NUMERIC_COLUMN_WIDTH : INITIAL_COLUMN_WIDTH;
				c.setWidth(width);
				c.setPreferredWidth(width);
			}

			int tableWidth = columnModel.getTotalColumnWidth();
			int totalWidth = (int) getParent().getSize().getWidth();

			int firstColumnWidth = totalWidth - tableWidth + firstColumn.getWidth();
			firstColumn.setPreferredWidth(firstColumnWidth);

			initial = false;
		}
	}
}
