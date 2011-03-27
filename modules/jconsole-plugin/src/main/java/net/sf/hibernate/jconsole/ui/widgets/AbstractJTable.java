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

package net.sf.hibernate.jconsole.ui.widgets;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Contains the abstract table logic.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public abstract class AbstractJTable<E> extends JTable {

	/**
	 * Defines a single column inside the table.
	 */
	protected static class Column {

		private String identifier;
		private String tooltip;
		private Class<?> type = Object.class;

		/**
		 * Creates a new Column instance.
		 *
		 * @param identifier An identifier used to address the column.
		 * @param tooltip	the tool-tip text to show on mouse over.
		 * @param type	   the type of the column, e.g. Comparable.class.
		 */
		public Column(String identifier, String tooltip, Class<?> type) {
			this.identifier = identifier;
			this.tooltip = tooltip;
			if (type != null)
				this.type = type;
		}

		public String getIdentifier() {
			return identifier;
		}

		public String getTooltip() {
			return tooltip;
		}

		public Class<?> getType() {
			return type;
		}

		@Override
		public String toString() {
			return identifier;
		}
	}

	/**
	 * Converts ms to seconds.
	 *
	 * @param ms the ms to convert.
	 * @return the seconds.
	 */
	public static double msToSeconds(long ms) {
		return ((double) ms) / 1000D;
	}

	/**
	 * Rounds the given input with 3 remaining digits.
	 *
	 * @param input hte input to round.
	 * @return the input rounded to a human readable number.
	 */
	public static double round(double input) {
		return Math.round(input * 1000D) / 1000D;
	}

	private class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = -3098276628365570375L;

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return getColumns()[columnIndex].getType();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	/**
	 * Constructs a new table.
	 */
	protected AbstractJTable() {
		super();
		MyTableModel model = new MyTableModel();
		model.setColumnIdentifiers(getColumns());
		setModel(model);

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
		sorter.setSortsOnUpdates(true);
		setRowSorter(sorter);

		setAutoResizeMode(AUTO_RESIZE_OFF);

		setFillsViewportHeight(true);
		setGridColor(Color.LIGHT_GRAY);
	}

	/**
	 * Overrides the default method to add tooltips in the table header.
	 */
	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				int realIndex = columnModel.getColumn(index).getModelIndex();
				return getColumns()[realIndex].getTooltip();
			}
		};
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	/**
	 * Returns the columns of this table.
	 *
	 * @return the columns of this table.
	 */
	protected abstract Column[] getColumns();

	/**
	 * Converts the given table data to a table row.
	 *
	 * @param key		 The key identifying the table data.
	 * @param dataElement The data element to convert.
	 * @return A vector of objects to show inside the table.
	 */
	protected abstract Vector toTableRow(String key, E dataElement);

	/**
	 * Is called to perform an incremental table update.
	 *
	 * @param data The keyed table data.
	 */
	@SuppressWarnings("unchecked")
	protected void refresh(Map<String, E> data) {
		Map<String, E> updateData = new HashMap<String, E>(data);

		int i = 0;
		boolean changed = false;
		final MyTableModel model = (MyTableModel) dataModel;
		for (Iterator dataIterator = model.getDataVector().iterator(); dataIterator.hasNext();) {
			Vector rowVector = (Vector) dataIterator.next();
			ListIterator rowIterator = rowVector.listIterator();
			String key = rowIterator.hasNext() ? String.valueOf(rowIterator.next()) : null;
			E statistics = updateData.remove(key);

			if (statistics == null) {
				dataIterator.remove();
				model.fireTableRowsDeleted(i, i);
				continue;
			}

			Iterator updateIterator = toTableRow(key, statistics).iterator();
			for (updateIterator.next(); updateIterator.hasNext() && rowIterator.hasNext();) {
				Object value = rowIterator.next();
				Object newValue = updateIterator.next();
				if (!value.equals(newValue)) {
					rowIterator.set(newValue);
					changed = true;
				}
			}

			i++;
		}

		for (Map.Entry<String, E> entry : updateData.entrySet())
			model.addRow(toTableRow(entry.getKey(), entry.getValue()));

		// We need to redraw all rows because the sort order may have changed...
		if (changed || !updateData.isEmpty())
			model.fireTableRowsUpdated(0, model.getRowCount() - 1);
	}
}
