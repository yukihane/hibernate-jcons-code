/*
 * Copyright (c) 2010
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

package net.sf.hibernate.jconsole.ui;

/**
 * Implements the collections page.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public class CollectionsTab extends AbstractTableTab {

	public static final String NAME = "Collections";

	CollectionsTable collectionsTable = new CollectionsTable();
	CollectionsDetails details = new CollectionsDetails(collectionsTable);

	public CollectionsTab() {
		super();
		init(details);
	}
}
