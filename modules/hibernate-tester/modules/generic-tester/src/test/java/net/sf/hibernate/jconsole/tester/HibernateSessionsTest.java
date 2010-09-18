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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Smoke test the test env.
 *
 * @author Juergen_Kellerer, 2010-09-18
 * @version 1.0
 */
public class HibernateSessionsTest {

	DummyAction action = new DummyAction();

	@Before
	public void beginTx() {
		action.beginTx();
	}

	@After
	public void rollbackTx() {
		action.rollbackTx();
	}

	@Test
	public void testHasTransaction() throws Exception {
		assertNotNull(action.session);
	}

	@Test
	public void testCanRunDefaultAction() throws Exception {
		action.insertAndSelect();
	}

	@Test
	public void testCanInsertEntity() throws Exception {
		action.doInsertAndSelect();
		assertEquals(action.entityMessage, action.entity.getText());
	}
}
