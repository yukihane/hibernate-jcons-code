package net.sf.hibernate.jconsole.ui.widgets.charts;

import java.awt.*;

/**
 * Extends the standard line graph with drawing a filled shape below it.
 *
 * @author Juergen_Kellerer, 2009-11-23
 * @version 1.0
 */
public class FilledLineGraph2D extends LineGraph2D {

	private static final long serialVersionUID = -6693655764785437078L;

	private int alpha = 64;
	private Color brightenerColor = new Color(255, 255, 240, 192);

	/**
	 * Creates a new filled line graph using the defined graph values and max value.
	 *
	 * @param values   the Y values along the X axis.
	 * @param maxValue the maximum value that can be shown on the Y axis.
	 */
	public FilledLineGraph2D(double[] values, double maxValue) {
		super(values, maxValue);
	}

	/**
	 * Creates the paint to use for filling the graph.
	 *
	 * @param targetColor The target color of the graph.
	 * @param graphHeight The height of the single graph.
	 * @return the paint to use for filling the graph.
	 */
	protected Paint getFillPaint(Color targetColor, int graphHeight) {
		Color brighter = targetColor.brighter();
		Color topPaint = new Color(brighter.getRed(), brighter.getGreen(), brighter.getBlue(), alpha);
		Color bottomPaint = new Color(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(), alpha);
		return new GradientPaint(0, height - graphHeight, topPaint,
				0, height - (int) (graphHeight / 1.9), bottomPaint);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintCoordinates(Graphics2D g2d) {
		Color c = g2d.getColor();
		int maxHeight = getMaxGraphHeight(), len = xCoordinates.length;
		int[] xCoords = xCoordinates, yCoords = yCoordinates;

		if (yCoords[0] != maxHeight || yCoords[len - 1] != maxHeight) {
			xCoords = new int[len + 2];
			System.arraycopy(xCoordinates, 0, xCoords, 1, len);
			yCoords = new int[len + 2];
			System.arraycopy(yCoordinates, 0, yCoords, 1, len);
			xCoords[0] = xCoords[1];
			xCoords[len + 1] = xCoords[len];
			yCoords[0] = yCoords[len + 1] = maxHeight;
			len = xCoords.length;
		}

		int maxGraphHeight = 0;
		for (int yCoord : yCoords)
			maxGraphHeight = Math.max(maxGraphHeight, maxHeight - yCoord);

		// Brighten background.
		g2d.setPaint(brightenerColor);
		g2d.fillPolygon(xCoords, yCoords, len);

		g2d.setPaint(getFillPaint(c, maxGraphHeight));
		g2d.fillPolygon(xCoords, yCoords, len);
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Paints the lines on top of the
	 */
	@Override
	public void paintOverlay(Graphics2D g2d) {
		Graphics2D elementGraphics = createElementGraphics(g2d);
		try {
			super.paintCoordinates(elementGraphics);
		} finally {
			elementGraphics.dispose();
		}
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
}
