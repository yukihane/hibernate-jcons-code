package net.sf.hibernate.jconsole.ui.widgets.charts;

import net.sf.hibernate.jconsole.ui.widgets.RefreshableJSplitPane;

/**
 * Combines chart and legend.
 *
 * @author Juergen_Kellerer, 2009-11-23
 * @version 1.0
 */
public class Chart2DPanel extends RefreshableJSplitPane {
	/**
	 * Creates a new Chart2DPanel out of the given chart implementation.
	 *
	 * @param chart2d The implementation of the chart.
	 */
	public Chart2DPanel(AbstractChart2D chart2d) {
		super(HORIZONTAL_SPLIT, chart2d, new Legend(chart2d));
		setDividerSize(6);
		setResizeWeight(1);
	}

	/**
	 * Returns the legend inside this Chart2DPanel.
	 *
	 * @return the legend inside this Chart2DPanel.
	 */
	public Legend getLegend() {
		return (Legend) getRightComponent();
	}

	/**
	 * Returns the chart inside this Chart2DPanel.
	 *
	 * @return the chart inside this Chart2DPanel.
	 */
	public AbstractChart2D getChart2D() {
		return (AbstractChart2D) getLeftComponent();
	}
}
