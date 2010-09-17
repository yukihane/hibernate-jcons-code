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

package net.sf.hibernate.jconsole.stats;

import java.io.Serializable;

/**
 * Collection related statistics.
 *
 * @author Gavin King
 * @author Alex Snaps
 * @author Juergen_Kellerer, 2010-09-16
 * @version 1.0
 */
public interface CollectionStatistics extends Serializable {

	/**
	 * Returns the number of DB loads for the given collection.
	 *
	 * @return the number of DB loads for the given collection.
	 */
	long getLoadCount();

	/**
	 * Returns the number of hits on a collection.
	 *
	 * @return the number of hits on a collection.
	 */
	long getFetchCount();

	/**
	 * Returns the number of re-creations on the collection.
	 *
	 * @return the number of re-creations on the collection.
	 */
	long getRecreateCount();

	/**
	 * Returns the number of removes on the collection.
	 *
	 * @return the number of removes on the collection.
	 */
	long getRemoveCount();

	/**
	 * Returns the number of updates on the collection.
	 *
	 * @return the number of updates on the collection.
	 */
	long getUpdateCount();
}
