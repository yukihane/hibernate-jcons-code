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

package net.sf.hibernate.jconsole.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Simple collection of UI related utility methods.
 *
 * @author Juergen_Kellerer, 2009-11-19
 * @version 1.0
 */
public class UIUtils {

	static String exceptionToString(Throwable t) {
		StringWriter w = new StringWriter(1024);
		t.printStackTrace(new PrintWriter(w, true));
		return w.toString();
	}

	/**
	 * Calculates the brightness of a color.
	 * <p/>
	 * Taken from: {@code http://www.nbdtech.com/blog/archive/2008/04/27/Calculating-the-Perceived-Brightness-of-a-Color.aspx}
	 *
	 * @param c The color to calculate the brightness of.
	 * @return A relative value of the brightness.
	 */
	public static int getBrightness(Color c) {
		double b = c.getRed() * c.getRed() * .241D +
				c.getGreen() * c.getGreen() * .691D +
				c.getBlue() * c.getBlue() * .068D;
		return (int) Math.sqrt(b);
	}

	/**
	 * Calculates the string bounds for the given text.
	 *
	 * @param g2d  The Graphics2D instance used to render the text.
	 * @param text The text to calculate the bounds for.
	 * @param x	The x coordinate to use for drawing the text.
	 * @param y	The y coordinate to use for drawing the text.
	 * @return A rectangle describing the outer bounds of the text when rendered.
	 */
	public static Rectangle2D getStringBounds(Graphics2D g2d, String text, int x, int y) {
		Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
		return setLocation(bounds, x, y);
	}

	/**
	 * Sets the coordinates on the given rectangle.
	 *
	 * @param rectangle The rectangle to change.
	 * @param x		 The new x coordinate to use.
	 * @param y		 The new y coordinate to use.
	 * @return The given rectangle instance.
	 */
	public static Rectangle2D setLocation(Rectangle2D rectangle, double x, double y) {
		rectangle.setRect(x, y, rectangle.getWidth(), rectangle.getHeight());
		return rectangle;
	}

	/**
	 * Displays a modal dialog containing the given error message.
	 *
	 * @param owner The owner of the modal dialog.
	 * @param cause The exception that caused the error.
	 */
	public static void displayErrorMessage(Frame owner, Throwable cause) {
		displayErrorMessage(owner, cause.getMessage(), cause);
	}

	/**
	 * Displays a modal dialog containing the given error message.
	 *
	 * @param owner   The owner of the modal dialog.
	 * @param message The message to show.
	 * @param cause   The exception that caused the error.
	 */
	public static void displayErrorMessage(Frame owner, String message, Throwable cause) {
		final JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("Error occured");
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLayout(new BorderLayout(8, 8));
		dialog.setPreferredSize(new Dimension(500, 400));

		// Error message
		JLabel messageLabel = new JLabel(message);
		messageLabel.setBorder(new EmptyBorder(4, 4, 4, 4));
		dialog.add(BorderLayout.NORTH, messageLabel);

		// Exception
		JTextArea textArea = new JTextArea(exceptionToString(cause));
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setEditable(false);
		textArea.setLineWrap(false);
		textArea.setFont(Font.getFont("monospaced"));
		textArea.setMinimumSize(new Dimension(0, 300));
		dialog.add(BorderLayout.CENTER, scrollPane);

		// Close button
		JButton close = new JButton("Close");
		close.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		JPanel closePanel = new JPanel();
		closePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		closePanel.add(close);
		closePanel.setMinimumSize(new Dimension(100, 30));
		dialog.add(BorderLayout.SOUTH, closePanel);

		// Show it.
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * Aligns the first <code>rows</code> * <code>cols</code>
	 * components of <code>parent</code> in
	 * a grid. Each component is as big as the maximum
	 * preferred width and height of the components.
	 * The parent is made just big enough to fit them all.
	 * <p/>
	 * <b>Note: This code is taken from the Swing Examples.</b>
	 *
	 * @param rows	 number of rows
	 * @param cols	 number of columns
	 * @param initialX x location to start the grid at
	 * @param initialY y location to start the grid at
	 * @param xPad	 x padding between cells
	 * @param yPad	 y padding between cells
	 */
	public static void makeGrid(Container parent,
								int rows, int cols,
								int initialX, int initialY,
								int xPad, int yPad) {
		SpringLayout layout = getOrSetSpringLayout(parent);

		Spring xPadSpring = Spring.constant(xPad);
		Spring yPadSpring = Spring.constant(yPad);
		Spring initialXSpring = Spring.constant(initialX);
		Spring initialYSpring = Spring.constant(initialY);
		int max = rows * cols;

		//Calculate Springs that are the max of the width/height so that all
		//cells have the same size.
		Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
		Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
		for (int i = 1; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

			maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
			maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
		}

		//Apply the new width/height Spring. This forces all the
		//components to have the same size.
		for (int i = 0; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(
					parent.getComponent(i));

			cons.setWidth(maxWidthSpring);
			cons.setHeight(maxHeightSpring);
		}

		//Then adjust the x/y constraints of all the cells so that they
		//are aligned in a grid.
		SpringLayout.Constraints lastCons = null;
		SpringLayout.Constraints lastRowCons = null;
		for (int i = 0; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
			if (i % cols == 0) { //start of new row
				lastRowCons = lastCons;
				cons.setX(initialXSpring);
			} else { //x position depends on previous component
				cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST), xPadSpring));
			}

			if (i / cols == 0) { //first row
				cons.setY(initialYSpring);
			} else { //y position depends on previous row
				cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH), yPadSpring));
			}
			lastCons = cons;
		}

		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH,
				Spring.sum(Spring.constant(yPad), lastCons.getConstraint(SpringLayout.SOUTH)));
		pCons.setConstraint(SpringLayout.EAST,
				Spring.sum(Spring.constant(xPad), lastCons.getConstraint(SpringLayout.EAST)));
	}

	/* Used by makeCompactGrid. */

	private static SpringLayout.Constraints getConstraintsForCell(
			int row, int col, Container parent, int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}

	private static SpringLayout getOrSetSpringLayout(Container parent) {
		LayoutManager lm = parent.getLayout();
		if (!(lm instanceof SpringLayout))
			parent.setLayout(lm = new SpringLayout());
		return (SpringLayout) lm;
	}

	/**
	 * Aligns the first <code>rows</code> * <code>cols</code>
	 * components of <code>parent</code> in
	 * a grid. Each component in a column is as wide as the maximum
	 * preferred width of the components in that column;
	 * height is similarly determined for each row.
	 * The parent is made just big enough to fit them all.
	 * <p/>
	 * <b>Note: This code is taken from the Swing Examples.</b>
	 *
	 * @param rows	 number of rows
	 * @param cols	 number of columns
	 * @param initialX x location to start the grid at
	 * @param initialY y location to start the grid at
	 * @param xPad	 x padding between cells
	 * @param yPad	 y padding between cells
	 */
	public static void makeCompactGrid(Container parent,
									   int rows, int cols,
									   int initialX, int initialY,
									   int xPad, int yPad) {
		SpringLayout layout = getOrSetSpringLayout(parent);

		//Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);

			for (int r = 0; r < rows; r++)
				width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());

			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}

			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		//Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);

			for (int c = 0; c < cols; c++)
				height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());

			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}

			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}

	private UIUtils() {
	}
}
