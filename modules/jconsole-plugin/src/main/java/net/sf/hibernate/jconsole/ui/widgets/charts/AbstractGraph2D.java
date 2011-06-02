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

import java.awt.*;

/**
 * Is the base to all graphs contained inside a AbstractChart2D.
 *
 * @author Juergen_Kellerer, 23.11.2009
 */
public abstract class AbstractGraph2D extends AbstractChartElement {

	private static final long serialVersionUID = 3791460415201358526L;

	protected double maxValue;
	protected double[] values;
	protected int[] xCoordinates, yCoordinates;
	protected boolean visible = true;

	public AbstractGraph2D(double[] values, double maxValue) {
		super();
		this.values = values == null ? null : values.clone();
		this.maxValue = maxValue;
	}

	double toYCoordinate(double value) {
		double height = getMaxGraphHeight();
		if (maxValue == 0)
			return height;
		return height - (height * (value / maxValue));
	}

	double toXCoordinate(int index) {
		return width * ((double) index / (double) values.length);
	}

	/**
	 * Creates the coordinates for this graph using the values array
	 * and the elements dimension as source.
	 *
	 * @return True if the coordinates were calculated, false if the source was missing.
	 */
	protected boolean createCoordinates() {
		if (values == null || values.length == 0 || width <= 0)
			return false;

		int maxCoordinates = Math.min(values.length, width);
		xCoordinates = new int[maxCoordinates];
		yCoordinates = new int[maxCoordinates];

		for (int i = 0; i < maxCoordinates; i++) {
			xCoordinates[i] = (int) Math.round(toXCoordinate(i));
			yCoordinates[i] = (int) Math.round(toYCoordinate(values[i]));
		}

		return true;
	}

	/**
	 * Invalidates the caluculated cooridinates.
	 */
	protected void invalidate() {
		xCoordinates = yCoordinates = null;
	}

	/**
	 * Returns the maximum height of the graph.
	 *
	 * @return the maximum height of the graph.
	 */
	public int getMaxGraphHeight() {
		return height - 1;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
		invalidate();
	}

	/**
	 * Returns the maximum value of the graphs source values.
	 *
	 * @return the maximum value of the graphs source values.
	 */
	public double getMaxGraphValue() {
		double v = Long.MIN_VALUE;
		if (values != null) {
			for (double value : values)
				if (v < value)
					v = value;
		}
		return v;
	}

	/**
	 * Returns the average value of the graphs source values.
	 *
	 * @return the average value of the graphs source values.
	 */
	public double getAverageGraphValue() {
		double v = 0;
		if (values != null) {
			for (double value : values)
				v += value;
			v /= values.length;
		}
		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintElement(Graphics2D g2d) {
		if (xCoordinates == null || yCoordinates == null) {
			if (!createCoordinates())
				return;
		}
		paintCoordinates(g2d);
	}

	/**
	 * Paint the coordinates.
	 *
	 * @param g2d The graphics context to paint the element in.
	 */
	protected abstract void paintCoordinates(Graphics2D g2d);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBounds(int x, int y, int width, int height) {
		if (this.width != width || this.height != height)
			invalidate();
		super.setBounds(x, y, width, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRect(double x, double y, double width, double height) {
		if (this.width != width || this.height != height) //NOSONAR - Precession is not an issue here
			invalidate();
		super.setRect(x, y, width, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSize(int width, int height) {
		if (this.width != width || this.height != height)
			invalidate();
		super.setSize(width, height);
	}
}
