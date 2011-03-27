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

import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * Is the base to details panel on top of tables.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public abstract class AbstractTableDetails<E> extends RefreshableJPanel implements ListSelectionListener {

	protected static final Dimension PREFERRED_SIZE = new Dimension(400, 130);

	protected AbstractRefreshableJTable<E> table;

	protected AbstractTableDetails(AbstractRefreshableJTable<E> table) {
		super(new BorderLayout(4, 4));

		setBorder(new EmptyBorder(2, 0, 6, 0));
		setPreferredSize(PREFERRED_SIZE);

		this.table = table;

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				try {
					AbstractTableDetails.this.valueChanged(e);
				} catch (RuntimeException re) {
					re.printStackTrace();
				}
			}
		});
	}

	public AbstractRefreshableJTable<E> getTable() {
		return table;
	}
}
