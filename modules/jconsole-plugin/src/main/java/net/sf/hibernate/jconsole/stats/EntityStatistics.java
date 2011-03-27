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
 * Entity related statistics.
 *
 * @author Gavin King
 * @author Alex Snaps
 * @author Juergen_Kellerer, 2010-09-16
 * @version 1.0
 */
public interface EntityStatistics extends Serializable {
	/**
	 * Returns the amount of deletes on the entity.
	 *
	 * @return the amount of deletes on the entity.
	 */
	long getDeleteCount();

	/**
	 * Returns the amount of entity creations.
	 *
	 * @return the amount of entity creations.
	 */
	long getInsertCount();

	/**
	 * Returns the amount of db hits caused by loading the entity.
	 *
	 * @return the amount of db hits caused by loading the entity.
	 */
	long getLoadCount();

	/**
	 * Returns the amount entity changes.
	 *
	 * @return the amount entity changes.
	 */
	long getUpdateCount();

	/**
	 * Returns the amount of hits on the entity
	 *
	 * @return the amount of hits on the entity
	 */
	long getFetchCount();

	/**
	 * Returns the amount of optimistic lock failures.
	 *
	 * @return the amount of optimistic lock failures.
	 */
	long getOptimisticFailureCount();
}
