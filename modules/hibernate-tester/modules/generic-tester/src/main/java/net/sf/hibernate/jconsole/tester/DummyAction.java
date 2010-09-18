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

import org.hibernate.Session;

/**
 * Simulates some traffic on the db.
 *
 * @author Juergen_Kellerer, 2010-09-18
 * @version 1.0
 */
public class DummyAction {

	Session session;
	String entityMessage = "Test Message";
	TestMessageEntity entity = new TestMessageEntity(entityMessage);

	public void insertAndSelect() throws Exception {
		try {
			beginTx();
			doInsertAndSelect();
		} finally {
			rollbackTx();
		}
	}

	void beginTx() {
		session = HibernateSessions.getSession();
		session.beginTransaction();
	}

	void rollbackTx() {
		if (session.isOpen()) {
			session.getTransaction().rollback();
			session.close();
		}
	}

	void doInsertAndSelect() throws Exception {
		session.persist(entity);
		entity = null;
		entity = (TestMessageEntity)
				session.createQuery("SELECT m FROM TestMessageEntity m WHERE m.text = :text").
						setParameter("text", entityMessage).uniqueResult();
	}
}
