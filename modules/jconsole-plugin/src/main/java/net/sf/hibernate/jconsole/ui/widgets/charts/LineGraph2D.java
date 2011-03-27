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
 * Implements a line graph element.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public class LineGraph2D extends AbstractGraph2D {

	private static final long serialVersionUID = 3791460415201358526L;

	private int dotSize = 1;
	private float strokeSize = 1.6f;

	/**
	 * Creates a new line graph using the defined graph values and max value.
	 *
	 * @param values   the Y values along the X axis.
	 * @param maxValue the maximum value that can be shown on the Y axis.
	 */
	public LineGraph2D(double[] values, double maxValue) {
		super(values, maxValue);
	}

	/**
	 * Paint the coordinates.
	 *
	 * @param g2d The graphics context to paint the element in.
	 */
	protected void paintCoordinates(Graphics2D g2d) {
		if (xCoordinates == null)
			return;

		final Stroke stroke = g2d.getStroke();
		try {
			g2d.setStroke(new BasicStroke(strokeSize));

			int previousX = 0, previousY = 0;
			for (int i = 0; i < xCoordinates.length && i < width; i++) {
				int x = xCoordinates[i], y = yCoordinates[i];
				if (i > 0)
					g2d.drawLine(previousX, previousY, x, y);

				//if (dotSize > 1)
				//	g2d.fillOval(x - dotSize, y - dotSize, x + dotSize, y + dotSize);

				previousX = x;
				previousY = y;
			}
		} finally {
			g2d.setStroke(stroke);
		}
	}

	public int getDotSize() {
		return dotSize;
	}

	public void setDotSize(int dotSize) {
		this.dotSize = dotSize;
	}

	public float getStrokeSize() {
		return strokeSize;
	}

	public void setStrokeSize(float strokeSize) {
		this.strokeSize = strokeSize;
	}
}
