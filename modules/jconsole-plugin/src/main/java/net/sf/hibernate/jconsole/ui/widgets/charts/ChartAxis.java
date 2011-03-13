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

package net.sf.hibernate.jconsole.ui.widgets.charts;

import net.sf.hibernate.jconsole.util.UIUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static net.sf.hibernate.jconsole.util.UIUtils.getStringBounds;

/**
 * Implements a basic chart axis.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public class ChartAxis extends AbstractChartElement {

	private static final long serialVersionUID = -4378685110063388423L;

	private static final int TICK_LENGTH = 2;
	private static final Color LINE_COLOR = Color.LIGHT_GRAY;
	private static final Color TEXT_COLOR = Color.BLACK;

	public enum Orientation {
		/**
		 * Defines that the axis is drawn horizontally.
		 */
		horizontal,
		/**
		 * Defines that the axis is drawn vertically.
		 */
		vertical
	}

	private int tickSize;
	protected double[] values;
	private Orientation orientation;

	/**
	 * Constructs a new ChartAxis.
	 *
	 * @param tickSize	the length of the lines leaving the axis border.
	 * @param orientation the orientation of the axis.
	 * @param values	  the max values of the axis.
	 */
	public ChartAxis(int tickSize, Orientation orientation, double... values) {
		this.tickSize = tickSize;
		this.orientation = orientation;
		this.values = values;
	}

	public int getTickSize() {
		return tickSize;
	}

	public void setTickSize(int tickSize) {
		this.tickSize = tickSize;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	/**
	 * Returns the total range of the axis.
	 *
	 * @return the total range of the axis.
	 */
	protected double getAxisRange() {
		return values[values.length - 1] - values[0];
	}

	/**
	 * Returns the min value of the axis.
	 *
	 * @return the min value of the axis.
	 */
	protected double getMinValue() {
		return values[0];
	}

	/**
	 * Converts the given value to a axis label.
	 *
	 * @param value The value to convert.
	 * @return The string to show inside the axis.
	 */
	protected String getAxisLabel(double value) {
		return String.valueOf(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintElement(Graphics2D g2d) {
		g2d.setPaint(LINE_COLOR);
		if (orientation == Orientation.horizontal)
			paintHorizontalAxis(g2d);
		else
			paintVerticalAxis(g2d);
	}

	/**
	 * Paints a horizontal axis.
	 *
	 * @param g2d The graphics context to paint to.
	 */
	protected void paintHorizontalAxis(Graphics2D g2d) {
		g2d.drawLine(0, 0, width, 0);
		for (int i = 0; i < width; i += tickSize)
			g2d.drawLine(i, 0, i, TICK_LENGTH);

		String leftLabel = getAxisLabel(values[0]);
		Rectangle2D leftBounds = getStringBounds(g2d, leftLabel, 0, TICK_LENGTH);

		String rightLabel = getAxisLabel(values[values.length - 1]);
		Rectangle2D rightBounds = getStringBounds(g2d, rightLabel, 0, 0);
		UIUtils.setLocation(rightBounds, width - rightBounds.getWidth(), TICK_LENGTH);

		paintText(g2d, leftBounds, leftLabel, rightBounds, rightLabel);
	}

	/**
	 * Paints a vertical axis.
	 *
	 * @param g2d The graphics context to paint to.
	 */
	protected void paintVerticalAxis(Graphics2D g2d) {
		g2d.drawLine(width - 1, 0, width - 1, height);
		for (int i = 0; i < height; i += tickSize)
			g2d.drawLine(width - TICK_LENGTH - 1, i, width - 1, i);

		String leftLabel = getAxisLabel(values[0]);
		Rectangle2D leftBounds = getStringBounds(g2d, leftLabel, 0, 0);
		UIUtils.setLocation(leftBounds, width - leftBounds.getWidth() - TICK_LENGTH - 4,
				height - leftBounds.getHeight());

		String rightLabel = getAxisLabel(values[values.length - 1]);
		Rectangle2D rightBounds = getStringBounds(g2d, rightLabel, 0, 0);
		UIUtils.setLocation(rightBounds, width - rightBounds.getWidth() - TICK_LENGTH - 4, -4);

		paintText(g2d, leftBounds, leftLabel, rightBounds, rightLabel);
	}

	/**
	 * Paints the axis lables.
	 *
	 * @param g2d		 The graphics context to paint to.
	 * @param leftBounds  The left/lower string bounds of the label.
	 * @param leftText	The left/lower text to paint.
	 * @param rightBounds The right/top string bounds of the label.
	 * @param rightText   The right/top text to paint.
	 */
	protected void paintText(Graphics2D g2d, Rectangle2D leftBounds, String leftText,
							 Rectangle2D rightBounds, String rightText) {
		g2d.setPaint(TEXT_COLOR);
		g2d.drawString(leftText, (int) leftBounds.getX(), (int) (leftBounds.getY() + leftBounds.getHeight()));
		if (!leftBounds.intersects(rightBounds))
			g2d.drawString(rightText, (int) rightBounds.getX(), (int) (rightBounds.getY() + rightBounds.getHeight()));
	}
}
