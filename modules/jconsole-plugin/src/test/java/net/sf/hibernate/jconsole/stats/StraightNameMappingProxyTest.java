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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Simple test for StraightNameMappingProxy.
 *
 * @author Juergen_Kellerer, 2010-09-16
 * @version 1.0
 */
public class StraightNameMappingProxyTest {

	class TestDelegate {

		public long getLoadCount() {
			return 1;
		}

		public long getFetchCount() {
			return 1;
		}

		public void getRecreateCount() {
		}
	}

	@Test
	public void testCanMapByMethodName() {
		CollectionStatistics statistics = StraightNameMappingProxy.
				newInstance(CollectionStatistics.class, new TestDelegate(), 2L);

		assertEquals(1, statistics.getLoadCount());
		assertEquals(1, statistics.getFetchCount());
		assertEquals(2, statistics.getRecreateCount());
		assertEquals(2, statistics.getRemoveCount());
		assertEquals(2, statistics.getUpdateCount());
	}
}
