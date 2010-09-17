package net.sf.hibernate.jconsole.ui.widgets;

import net.sf.hibernate.jconsole.HibernateContext;
import net.sf.hibernate.jconsole.Refreshable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

/**
 * Is a styled JSplitPane that delegates any refresh calls to its children.
 *
 * @author Juergen_Kellerer, 2009-11-23
 * @version 1.0
 */
public class RefreshableJSplitPane extends JSplitPane implements Refreshable {
	/**
	 * Constructs a new refreshable split pane with empty JPanels and the given orientation.
	 *
	 * @param orientation The orientation of the divider.
	 */
	public RefreshableJSplitPane(int orientation) {
		this(orientation, new JPanel(), new JPanel());
	}

	/**
	 * Constructs a new refreshable split pane with empty JPanels and the given orientation.
	 *
	 * @param orientation		  The orientation of the divider.
	 * @param leftTopComponent	 The left or top component inside the panel.
	 * @param rightBottomComponent The right or bottom component inside the panel.
	 */
	public RefreshableJSplitPane(int orientation, Component leftTopComponent, Component rightBottomComponent) {
		super(orientation, true, leftTopComponent, rightBottomComponent);

		leftTopComponent.setMinimumSize(new Dimension(0, 0));
		rightBottomComponent.setMinimumSize(new Dimension(0, 0));

		setDividerSize(8);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		// Make sure the divider is transparent.
		setUI(new BasicSplitPaneUI() {
			@Override
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					@Override
					public Border getBorder() {
						return null;
					}
				};
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void refresh(HibernateContext context) {
		refreshComponents(getComponents(), context);
	}

	protected void refreshComponents(Component[] components, HibernateContext context) {
		for (Component component : components) {
			if (!(component instanceof Refreshable))
				continue;
			((Refreshable) component).refresh(context);
		}
	}
}
