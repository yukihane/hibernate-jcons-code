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
 * Defines a paintable chart-element used inside charts.
 *
 * @author Juergen_Kellerer, 22.11.2009
 */
public abstract class AbstractChartElement extends Rectangle {

	private static final long serialVersionUID = 6119511389913501262L;

	/**
	 * Paint the chart element.
	 *
	 * @param g2d The graphics context to paint the element in.
	 */
	public abstract void paintElement(Graphics2D g2d);

	/**
	 * Returns the graphics context to use for painting this element.
	 *
	 * @param g2d The graphics context to paint the element in.
	 * @return A new instance of Graphics2D to use for painting.
	 */
	protected Graphics2D createElementGraphics(Graphics2D g2d) {
		return (Graphics2D) g2d.create(x, y, width, height);
	}

	/**
	 * Paint the chart element.
	 *
	 * @param g2d The graphics context to paint the element in.
	 */
	public final void paint(Graphics2D g2d) {
		Graphics2D elementGraphics = createElementGraphics(g2d);
		try {
			paintElement(elementGraphics);
		} finally {
			elementGraphics.dispose();
		}
	}

	/**
	 * Is used to paint an overlay after all other chart elements have been painted.
	 *
	 * @param g2d The graphics context to paint the element in.
	 */
	public void paintOverlay(Graphics2D g2d) {
		// By default no overlay is painted.
	}
}
