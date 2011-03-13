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

package net.sf.hibernate.jconsole.tester;

import net.sf.hibernate.jconsole.hibernate.HibernateContext;
import net.sf.hibernate.jconsole.hibernate.Methods;
import net.sf.hibernate.jconsole.stats.QueryStatistics;

/**
 * Tests the Hibernate context.
 *
 * @author Juergen_Kellerer, 2010-09-18
 * @version 1.0
 */
public class HibernateContextTester {

	private HibernateContextTester() {
	}

	/**
	 * Runs a test whether statistics can be collected.
	 *
	 * @throws Exception in case of the test was not successful.
	 */
	public static void testContext() throws Exception {
		HibernateSessions.initSessionFactory();
		Methods.clear.invoke(HibernateSessions.getMBeanServer());

		HibernateContext context = new HibernateContext();
		context.setConnection(HibernateSessions.getMBeanServer());

		context.refresh();
		measureInitialState(context);
		new DummyAction().insertAndSelect();

		context.refresh();
		measureStateAfterDummyAction(context);
	}

	static void measureStateAfterDummyAction(HibernateContext context) {
		assertEquals(1, context.getQueries().length);
		QueryStatistics qs = context.getQueryStatistics().values().iterator().next();
		assertEquals(1L, qs.getExecutionCount());
		assertEquals(1L, qs.getExecutionRowCount());
		assertEquals(0L, qs.getCacheHitCount());
		assertEquals(0L, qs.getCachePutCount());
	}

	static void measureInitialState(HibernateContext context) {
		assertEquals(1, context.getEntityNames().length);
		assertEquals("net.sf.hibernate.jconsole.tester.TestMessageEntity", context.getEntityNames()[0]);

		for (String s : context.getEntityNames())
			assertNotNull(context.getEntityStatistics().get(s));

		assertEquals(0, context.getQueries().length);
	}

	private static void assertNotNull(Object actual) {
		if (actual == null)
			throw new IllegalArgumentException("Did not expect 'null'");
	}

	private static void assertEquals(Object expected, Object actual) {
		if (!expected.equals(actual))
			throw new IllegalArgumentException("Expected <" + expected + "> but was <" + actual + ">");
	}
}
