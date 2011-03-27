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

import net.sf.hibernate.jconsole.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Is a simple component that renders a line bar to visualize progress or relative usage.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public class LineBarTableCell extends JComponent implements Comparable<LineBarTableCell> {

	static final Font DEFAULT = new Font(Font.DIALOG, Font.BOLD, 10);
	static final Paint BAR_BACKGROUND = new GradientPaint(
			0, 1, Color.decode("0xF0F0F0"), 0, 10, Color.decode("0xFDFDFD"));

	enum Style {

		DEFAULT(new GradientPaint(0, 1, Color.decode("0xC1EAFF"), 0, 20, Color.decode("0x56AFD8")),
				BAR_BACKGROUND, Color.BLACK),

		GREEN(new GradientPaint(0, 1, Color.decode("0xEFFFC6"), 0, 10, Color.decode("0xDAFF7F")),
				BAR_BACKGROUND, Color.BLACK),

		ORANGE(new GradientPaint(0, 1, Color.decode("0xFFFFC6"), 0, 10, Color.decode("0xFAFF7F")),
				BAR_BACKGROUND, Color.BLACK),

		CYAN_ORANGE(new GradientPaint(0, 1, Color.decode("0xc6FFFF"), 0, 10, Color.decode("0x7FCCFA")),
				new GradientPaint(0, 1, Color.decode("0xFFFFC6"), 0, 10, Color.decode("0xFAFF7F")),
				Color.BLACK);

		transient Paint bar;
		transient Paint background;
		Color fontColor;

		Style(Paint bar, Paint background, Color fontColor) {
			this.bar = bar;
			this.background = background;
			this.fontColor = fontColor;
		}
	}

	private Style style = Style.DEFAULT;
	private String labelFormat = "%3.2f";

	private Double labelValue;
	private double value;
	private int overlayBrightnessOffset = UIUtils.getBrightness(Color.lightGray);

	public LineBarTableCell() {
		super();
		setFont(DEFAULT);
	}

	public LineBarTableCell(double value) {
		this();
		this.value = value;
	}

	public LineBarTableCell(String labelFormat, double value, Double labelValue) {
		this(value);
		this.labelFormat = labelFormat;
		this.labelValue = labelValue;
	}

	public LineBarTableCell(String labelFormat, double value) {
		this(labelFormat, value, null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		paintBar((Graphics2D) g);
	}

	private void paintBar(Graphics2D g2d) {
		Dimension size = getSize();
		int width = (int) size.getWidth();
		int height = (int) size.getHeight();
		int i = (int) (width * value); // what area should be painted red?

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.setPaint(style.background);
		g2d.fillRect(0, 1, width, height - 1);
		g2d.setPaint(style.bar);
		g2d.fillRect(0, 1, i, height - 1);

		// Painting an overlay if the background is set (used to show a selected bar).
		if (isBackgroundSet()) {
			Color overlayColor = getBackground();
			int brightness = UIUtils.getBrightness(overlayColor);
			if (brightness < overlayBrightnessOffset) {
				g2d.setPaint(new Color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), 48));
				g2d.fillRect(0, 0, width, height);
			}
		}

		// Draw a white border.
		g2d.setPaint(Color.WHITE);
		g2d.drawRect(0, 0, width, height);

		// Draw the label.
		g2d.setColor(style.fontColor);
		String label = getLabelText();
		Rectangle2D labelBounds = g2d.getFontMetrics().getStringBounds(label, g2d);
		g2d.drawString(label, Math.max(0, (width / 2) - (int) (labelBounds.getWidth() / 2)),
				height - (int) (labelBounds.getHeight() / 2) + 1);
	}

	protected String getLabelText() {
		return String.format(labelFormat, getLabelValue());
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public String getLabelFormat() {
		return labelFormat;
	}

	public void setLabelFormat(String labelFormat) {
		this.labelFormat = labelFormat;
	}

	public double getLabelValue() {
		return labelValue == null ? value : labelValue;
	}

	public void setLabelValue(double labelValue) {
		this.labelValue = labelValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String toolTipText = getToolTipText();
		if (toolTipText == null)
			return getLabelText();
		else
			return getLabelText() + " (" + toolTipText + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(LineBarTableCell o) {
		return Double.compare(value, o.value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LineBarTableCell)) return false;

		LineBarTableCell that = (LineBarTableCell) o;

		if (Double.compare(that.value, value) != 0) return false;
		if (!labelFormat.equals(that.labelFormat)) return false;
		if (labelValue != null ? !labelValue.equals(that.labelValue) : that.labelValue != null) return false;
		if (getToolTipText() != null ? !getToolTipText().equals(that.getToolTipText()) : that.getToolTipText() != null)
			return false;
		if (style != that.style) return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result;
		long temp;
		result = style.hashCode();
		result = 31 * result + labelFormat.hashCode();
		result = 31 * result + (labelValue != null ? labelValue.hashCode() : 0);
		temp = value != +0.0d ? Double.doubleToLongBits(value) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
