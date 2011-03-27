package net.sf.hibernate.jconsole.ui.widgets.charts;

import javax.swing.*;
import java.awt.*;

/**
 * Implements the label that is shown in a chart legend.
 *
 * @author Juergen_Kellerer, 2009-11-23
 * @version 1.0
 */
public class LegendJLabel extends JLabel {
	/**
	 * Implements the color icon used to show the graph relationship.
	 */
	private static final class ColorIcon implements Icon {

		static final int ICON_SIZE = 8;

		private Color borderColor = new Color(0, 0, 0, 96);
		private Color color;

		private ColorIcon(Color color) {
			this.color = color;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (g instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setPaint(new GradientPaint(x, y, Color.WHITE, x, y + ICON_SIZE + 1, color));
				g2d.fillRect(x, y, ICON_SIZE, ICON_SIZE);
				g2d.setPaint(borderColor);
				g2d.drawRect(x, y, ICON_SIZE, ICON_SIZE);
			} else
				throw new IllegalStateException("The icon cannot be used without Graphics2D.");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getIconWidth() {
			return ICON_SIZE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getIconHeight() {
			return ICON_SIZE;
		}
	}

	private boolean disabled;

	/**
	 * Constructs a new label to be used from within a legend container.
	 *
	 * @param color The color of the graph this label belongs to.
	 * @param text  The text to show with the label.
	 */
	public LegendJLabel(Color color, String text) {
		super(text);
		setIcon(new ColorIcon(color));
		setDisabledIcon(new ColorIcon(Color.lightGray));
	}

	/**
	 * Returns true if this label is painted disabled.
	 * <p/>
	 * Note: If this returns true, the label is only painted as disabled.
	 * It remains enabled and can receive user input.
	 *
	 * @return true if this label is painted disabled.
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Sets the disabled paint state of the label.
	 *
	 * @param disabled True if the label shall be painted isabled.
	 */
	public void setDisabled(boolean disabled) {
		if (this.disabled == disabled)
			return;

		this.disabled = disabled;
		repaint();
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Returns true only if this label is enabled (receives user input) and is not disabled.
	 */
	@Override
	public boolean isEnabled() {
		return !disabled && super.isEnabled();
	}
}
