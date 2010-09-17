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

import net.sf.hibernate.jconsole.formatters.AbstractHighlighter;

import javax.swing.*;

/**
 * Is a specialized version of JLabel which can be used instead of plain text inside a table.
 * <p/>
 * (This is primarily a speedup option to avoid reparsing of highlighted content when rendering the cells.)
 *
 * @author Juergen_Kellerer, 21.11.2009
 */
public class TableCellJLabel extends JLabel implements Comparable<TableCellJLabel> {

	String originalText;

	public TableCellJLabel(String text, String toolTipText,
						   AbstractHighlighter textHighlighter) {
		super(textHighlighter == null ? text : textHighlighter.highlight(text));
		setVerticalAlignment(TOP);
		setToolTipText(toolTipText);
		setOpaque(true);
		originalText = text;
	}

	public TableCellJLabel(String text, String toolTipText) {
		this(text, toolTipText, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return originalText;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(TableCellJLabel o) {
		return originalText.compareTo(o.originalText);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TableCellJLabel)) return false;

		TableCellJLabel that = (TableCellJLabel) o;
		return originalText.equals(that.originalText);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return originalText.hashCode();
	}
}