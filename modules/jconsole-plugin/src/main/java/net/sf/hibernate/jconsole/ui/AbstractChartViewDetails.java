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

package net.sf.hibernate.jconsole.ui;

import net.sf.hibernate.jconsole.AbstractStatisticsContext;
import net.sf.hibernate.jconsole.ui.widgets.AbstractRefreshableJTable;
import net.sf.hibernate.jconsole.ui.widgets.AbstractTableDetails;
import net.sf.hibernate.jconsole.ui.widgets.RefreshableJSplitPane;
import net.sf.hibernate.jconsole.ui.widgets.charts.AbstractChart2D;
import net.sf.hibernate.jconsole.ui.widgets.charts.Chart2DPanel;
import net.sf.hibernate.jconsole.ui.widgets.charts.FilledLineGraph2D;
import net.sf.hibernate.jconsole.ui.widgets.charts.LineGraph2D;
import net.sf.hibernate.jconsole.util.DataTable;

import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

/**
 * Defines an abstract container that is used to display details on a selected table entry.
 *
 * @author Juergen_Kellerer, 2010-09-20
 * @version 1.0
 */
public abstract class AbstractChartViewDetails<E> extends AbstractTableDetails<E> {

	private static final DataTable emptyDataTable = new DataTable();

	public AbstractChartViewDetails(AbstractRefreshableJTable<E> table) {
		super(table);
	}

	private Object lastSelection;
	private int detailsHeightWhenSelected = 110;
	private AbstractStatisticsContext lastFreshContext;

	/**
	 * {@inheritDoc}
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;

		final int rowIdx = e.getFirstIndex();
		final Object selection = table.getValueAt(rowIdx, 0);
		final RefreshableJSplitPane splitPane = getParent() instanceof RefreshableJSplitPane ?
				(RefreshableJSplitPane) getParent() : null;

		if (lastSelection == null || !lastSelection.equals(selection)) {
			if (lastSelection != null && splitPane != null)
				detailsHeightWhenSelected = (int) (splitPane.getSize().getHeight() - splitPane.getDividerLocation());

			removeAll();

			if (selection != null) {
				Chart2DPanel chart2DPanel = new Chart2DPanel(new AbstractChart2D() {
					@Override
					protected DataTable getDataTable(AbstractStatisticsContext context) {
						DataTable dt = getDataTableFor(context, selection);
						return dt == null ? emptyDataTable : dt;
					}

					@Override
					protected String getLegendForColumn(DataTable.Column column) {
						return column.getName();
					}

					@Override
					protected LineGraph2D createGraph(DataTable.Column column, double[] values, double maxValue) {
						LineGraph2D lg = super.createGraph(column, values, maxValue);
						lg.setDotSize(2);
						if (lg instanceof FilledLineGraph2D)
							((FilledLineGraph2D) lg).setAlpha(32);
						return lg;
					}
				});

				chart2DPanel.setBorder(new EmptyBorder(0, 0, 0, 4));
				chart2DPanel.refresh(lastFreshContext);

				add(chart2DPanel, BorderLayout.CENTER);
				setPreferredSize(new Dimension(400, detailsHeightWhenSelected));
			}

			// Adjust divider.
			if (splitPane != null) {
				double height = splitPane.getSize().getHeight();
				int dividerLocation = (int) (height - (selection == null ? 0 : detailsHeightWhenSelected));
				splitPane.setDividerLocation(dividerLocation);
			}

			lastSelection = selection;
		}

		repaint(25);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(AbstractStatisticsContext context) {
		this.lastFreshContext = context;
		super.refresh(context);
	}

	/**
	 * Returns the DataTable for the selected item or 'null' if not available.
	 *
	 * @param context   the context containing the statistics.
	 * @param selection the selected first row of the table grid.
	 * @return the DataTable for the selected item or 'null' if not available.
	 */
	protected abstract DataTable getDataTableFor(AbstractStatisticsContext context, Object selection);
}
