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

/**
 * Shows the modifications for entities and collections.
 *
 * @author Juergen_Kellerer, 21.11.2009
 */
public class ModificationsTableCell extends LineBarTableCell {
	public ModificationsTableCell(long maxModificationCount,
								  long inserts, long updates, long deletes) {
		super();
		setStyle(Style.GREEN);

		long modificationCount = inserts + updates + deletes;
		setValue(Math.max(0, (double) modificationCount / (double) maxModificationCount));
		setLabelValue(modificationCount);
		setLabelFormat("%.0f");

		setToolTipText(String.format("(inserts: %d - updates: %d - deletes: %d)", inserts, updates, deletes));
	}
}