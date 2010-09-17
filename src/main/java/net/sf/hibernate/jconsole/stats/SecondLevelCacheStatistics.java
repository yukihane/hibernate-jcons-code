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
 * Second level cache statistics of a specific region.
 *
 * @author Gavin King
 * @author Alex Snaps
 * @author Juergen_Kellerer, 2010-09-16
 * @version 1.0
 */
public interface SecondLevelCacheStatistics extends Serializable {

	long getHitCount();

	long getMissCount();

	long getPutCount();

	long getElementCountInMemory();

	long getElementCountOnDisk();

	long getSizeInMemory();
}
