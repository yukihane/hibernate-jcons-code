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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Implements a JPanel that draws a line chart.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public abstract class AbstractChart2D extends RefreshableJPanel {

	static final String TOOLTIP_TEMPLATE = "x: %s - y: %s";
	static final Color BACKGROUND_GARDIENT_BRIGHT_TOP = Color.decode("0xFFFFFF");
	static final Color BACKGROUND_GARDIENT_BRIGHT_BOTTOM = Color.decode("0xFAFAFA");
	static final Color BACKGROUND_GARDIENT_DARK = Color.decode("0xF5F5F5");
	static final Color[] DEFAULT_GRAPH_COLORS = new Color[]{
			Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA,
			Color.ORANGE, Color.CYAN, Color.DARK_GRAY, Color.PINK};

	private Insets graphInsets = new Insets(10, 60, 20, 0);

	private DataTable dataTable;
	private ChartAxis verticalAxis;
	private ChartAxis horizontalAxis;

	private Graph2D[] graphs;
	private Color[] graphColors = DEFAULT_GRAPH_COLORS;

	private Paint backgroundPaint;
	private Rectangle verticalAxisBounds, horizontalAxisBounds, graphBounds;

	private WeakHashMap<DataTable.Column, Graph2D> columnMap = new WeakHashMap<DataTable.Column, Graph2D>();

	protected AbstractChart2D() {
		setOpaque(true);
		setToolTipText("non-empty");
	}

	/**
	 * Returns the data table to use for this line chart.
	 *
	 * @param context The hibernate context instance containing the data table.
	 * @return An instance of data table acting as the data source for this line chart.
	 */
	protected abstract DataTable getDataTable(AbstractStatisticsContext context);

	/**
	 * Returns the legend label text for the given column.
	 *
	 * @param column The column to get the label text for.
	 * @return The text to show inside the legend for the given column.
	 */
	protected abstract String getLegendForColumn(DataTable.Column column);

	/**
	 * Returns the color to use for drawing the graph for the given column.
	 *
	 * @param column The column to get the color for.
	 * @return The color to use for drawing the graph.
	 */
	protected Color getColorForColumn(DataTable.Column column) {
		int colorIdx = column.getIndex();
		while (colorIdx >= graphColors.length)
			colorIdx -= graphColors.length;
		return graphColors[colorIdx];
	}

	/**
	 * Creates the graph element to use for the given column.
	 *
	 * @param column   The column to get the graph for.
	 * @param values   The values of the column.
	 * @param maxValue The global max value contained in the data.
	 * @return A new instance of LineGraph used for painting.
	 */
	protected LineGraph2D createGraph(DataTable.Column column, double[] values, double maxValue) {
		return new FilledLineGraph2D(values, maxValue);
	}

	/**
	 * Creates the vertical axis for the chart.
	 *
	 * @param dataTable The data table to create the axis from.
	 * @return A new implementation of ChartAxis.
	 */
	protected ChartAxis createVerticalAxis(DataTable dataTable) {
		return new NumberAxis(dataTable);
	}

	/**
	 * Creates the horizontal axis for the chart.
	 *
	 * @param dataTable The data table to create the axis from.
	 * @return A new implementation of ChartAxis.
	 */
	protected ChartAxis createHorizontalAxis(DataTable dataTable) {
		return new TimeAxis(dataTable);
	}

	/**
	 * Returns the maximum value (=lagest data value) for the given data table.
	 * <p/>
	 * Note: The returned value contains the vertical spacing of the chart.
	 *
	 * @param dataTable The data table to extract the maximum value from.
	 * @return The maximum data value from the given table.
	 */
	protected double getMaxValue(DataTable dataTable) {
		return dataTable.getMaxValue();
	}

	/**
	 * Returns the background paint to draw behind the graphs.
	 *
	 * @return the background paint to draw behind the graphs.
	 */
	protected Paint getGraphBackground() {
		if (backgroundPaint == null) {
			Rectangle bounds = getGraphBounds();
			if (bounds.height < 3) {
				backgroundPaint = BACKGROUND_GARDIENT_BRIGHT_TOP;
			} else {
				int height = bounds.height;
				Color[] colors = {BACKGROUND_GARDIENT_BRIGHT_TOP,
						BACKGROUND_GARDIENT_DARK, BACKGROUND_GARDIENT_BRIGHT_BOTTOM};
				float[] fractions = {0.0f, 0.80f, 1f};
				backgroundPaint = new LinearGradientPaint(0, height / 3f, 0, height, fractions, colors);
			}
		}
		return backgroundPaint;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Returns the exact coordinates under the mouse pointer if any.
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		if (verticalAxis == null || horizontalAxis == null)
			return null;

		Point m = event.getPoint();
		Rectangle bounds = getGraphBounds();

		if (bounds.contains(m)) {
			double xPercentage = (double) (m.x - bounds.x) / (double) bounds.width;
			double yPercentage = Math.max(0, 1D - (double) (m.y - bounds.y) / (double) bounds.height);
			String xPosition = horizontalAxis.getAxisLabel(
					horizontalAxis.getMinValue() + (xPercentage * horizontalAxis.getAxisRange()));
			String yPosition = verticalAxis.getAxisLabel(
					verticalAxis.getMinValue() + (yPercentage * verticalAxis.getAxisRange()));

			return String.format(TOOLTIP_TEMPLATE, xPosition, yPosition);
		} else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point getToolTipLocation(MouseEvent event) {
		Point m = event.getPoint();
		String toolTip = getToolTipText(event);
		if (toolTip == null)
			return null;

		int tipWidth = getFontMetrics(getFont()).stringWidth(toolTip);
		m.translate(-(tipWidth / 2), -30);
		return m;
	}

	/**
	 * Returns true if the specified column is visible.
	 *
	 * @param column The column to check.
	 * @return true if the specified column is visible.
	 */
	public boolean isColumnVisible(DataTable.Column column) {
		Graph2D graph = columnMap.get(column);
		return graph != null && graph.isVisible();
	}

	/**
	 * Toggles the visibility of the specified column.
	 *
	 * @param column  The column to change the visibility for.
	 * @param visible True to set the column to visible, false to hide it.
	 */
	public void setColumnVisible(DataTable.Column column, boolean visible) {
		Graph2D graph = columnMap.get(column);
		if (graph != null) {
			graph.setVisible(visible);
			updateVisibility();
		}
	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		try {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			Rectangle bounds = getGraphBounds();

			if (graphs != null) {
				// Drawing the background gradient for the graphs.
				g2d.setPaint(getGraphBackground());
				g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

				// Drawing the graphs.
				List<DataTable.Column> columnList = dataTable.getColumns();
				Iterator<DataTable.Column> columns = columnList.iterator();
				for (Graph2D graph : graphs) {
					g2d.setPaint(getColorForColumn(columns.next()));
					if (!graph.isVisible())
						continue;
					graph.setBounds(bounds);
					graph.paint(g2d);
				}

				// Drawing the overlays.
				columns = columnList.iterator();
				for (Graph2D graph : graphs) {
					g2d.setPaint(getColorForColumn(columns.next()));
					if (!graph.isVisible())
						continue;
					graph.paintOverlay(g2d);
				}

				int x = bounds.x + bounds.width - 1;
				g2d.setPaint(Color.LIGHT_GRAY);
				g2d.drawLine(x, bounds.y, x, bounds.y + bounds.height);
			}

			if (verticalAxis != null) {
				verticalAxis.setBounds(getVerticalAxisBounds());
				verticalAxis.paint(g2d);
			}

			if (horizontalAxis != null) {
				horizontalAxis.setBounds(getHorizontalAxisBounds());
				horizontalAxis.paint(g2d);
			}
		} finally {
			g2d.dispose();
		}
	}

	public Insets getGraphInsets() {
		return graphInsets;
	}

	public ChartAxis getVerticalAxis() {
		return verticalAxis;
	}

	public ChartAxis getHorizontalAxis() {
		return horizontalAxis;
	}

	/**
	 * Returns the rectangle around the vertical axis.
	 *
	 * @return the rectangle around the vertical axis.
	 */
	protected Rectangle getVerticalAxisBounds() {
		if (verticalAxisBounds == null) {
			Rectangle bounds = getGraphBounds();
			verticalAxisBounds = new Rectangle(0, bounds.y, bounds.x, bounds.height);
		}
		return verticalAxisBounds;
	}

	/**
	 * Returns the rectangle around the horizontal axis.
	 *
	 * @return the rectangle around the horizontal axis.
	 */
	protected Rectangle getHorizontalAxisBounds() {
		if (horizontalAxisBounds == null) {
			Rectangle bounds = getGraphBounds();
			int y = bounds.y + bounds.height;
			horizontalAxisBounds = new Rectangle(bounds.x, y, bounds.width, getHeight() - y);
		}
		return horizontalAxisBounds;
	}

	/**
	 * Returns the rectangle around all graphs.
	 *
	 * @return the rectangle around all graphs.
	 */
	protected Rectangle getGraphBounds() {
		if (graphBounds == null) {
			graphBounds = new Rectangle(graphInsets.left, graphInsets.top,
					getWidth() - graphInsets.right - graphInsets.left,
					getHeight() - graphInsets.top - graphInsets.bottom);
		}
		return graphBounds;
	}

	private boolean[] getAllGraphVisibleFlags() {
		boolean[] visibleFlags = new boolean[graphs == null ? 0 : graphs.length];
		for (int i = 0; i < visibleFlags.length; i++)
			visibleFlags[i] = graphs[i].isVisible();
		return visibleFlags;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		backgroundPaint = null;
		graphBounds = horizontalAxisBounds = verticalAxisBounds = null;
	}

	/**
	 * Updates the visibility settings after a change in the graphs or graph visibility.
	 */
	public void updateVisibility() {
		double maxValue = 0;
		for (Graph2D graph : graphs)
			if (graph.isVisible())
				maxValue = Math.max(maxValue, graph.getMaxGraphValue());
		for (Graph2D graph : graphs)
			graph.setMaxValue(maxValue);

		repaint(25);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(AbstractStatisticsContext context) {
		super.refresh(context);

		Rectangle bounds = getGraphBounds();
		dataTable = getDataTable(context).shrinkToSize((int) bounds.getWidth());
		verticalAxis = createVerticalAxis(dataTable);
		horizontalAxis = createHorizontalAxis(dataTable);

		int i = 0;
		List<DataTable.Column> columns = dataTable.getColumns();
		boolean[] visibleFlags = getAllGraphVisibleFlags();

		columnMap.clear();
		if (!columns.isEmpty()) {
			graphs = new LineGraph2D[columns.size()];
			for (DataTable.Column column : columns) {
				graphs[i] = createGraph(column, dataTable.getColumnValues(column), 0D);
				graphs[i].setVisible(i >= visibleFlags.length || visibleFlags[i]);
				columnMap.put(column, graphs[i]);
				i++;
			}
		} else
			graphs = null;

		updateVisibility();
	}
}
