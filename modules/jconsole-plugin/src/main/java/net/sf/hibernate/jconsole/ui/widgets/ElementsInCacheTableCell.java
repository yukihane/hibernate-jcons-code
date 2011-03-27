/*
 * Copyright (c) 2011
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

import static net.sf.hibernate.jconsole.stats.StatisticsUtil.toRatio;
import static net.sf.hibernate.jconsole.ui.widgets.AbstractJTable.round;

/**
 * Displays the element count and memory used by the cache.
 *
 * @author juergen, 27.03.11
 */
public class ElementsInCacheTableCell extends LineBarTableCell {

	long totalElementsInCache;

	public ElementsInCacheTableCell(long elementsInMemory, long elementsOnDisk, long sizeInMemory) {
		super();
		setStyle(Style.CYAN_ORANGE);

		totalElementsInCache = elementsInMemory + elementsOnDisk;
		double ratio = toRatio(elementsInMemory, elementsOnDisk);

		setValue(ratio);
		setLabelValue(round(ratio * 100D));
		setLabelFormat("%2.2f%%");

		setToolTipText(String.format("cached elements %d (memory: %d, disk: %d, ratio %2.2f%%)\n" +
				"memory usage: %.3fkb",
				totalElementsInCache, elementsInMemory, elementsOnDisk, ratio,
				sizeInMemory / 1024D));
	}

	@Override
	public int compareTo(LineBarTableCell o) {
		final long l = totalElementsInCache - (o instanceof ElementsInCacheTableCell ?
				((ElementsInCacheTableCell) o).totalElementsInCache : 0);
		return l < 0 ? -1 : (l > 0 ? 1 : 0);
	}
}
