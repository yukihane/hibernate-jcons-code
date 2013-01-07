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

import net.sf.hibernate.jconsole.util.HibernateJmxBinding;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

/**
 * Simple Hibernate initializer.
 *
 * @author Juergen_Kellerer, 2010-09-18
 * @version 1.0
 */
public final class HibernateSessions {

	static SessionFactory sessionFactory;
	static MBeanServer mBeanServer;

	public static MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	/**
	 * Returns a new hibernate session.
	 *
	 * @return a new hibernate session.
	 */
	public static Session getSession() {
		initSessionFactory();
		try {
			final Method openSessionMethod = sessionFactory.getClass().getMethod("openSession");
			return (Session) openSessionMethod.invoke(sessionFactory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initializes the session factory (once for the JVM).
	 */
	public static void initSessionFactory() {
		if (sessionFactory == null)
			try {
				doInitSessionFactory();
			} catch (Exception e) {
				sessionFactory = null;
				throw new RuntimeException(e);
			}
	}

	private static void doInitSessionFactory() throws Exception {
		Configuration cfg = new Configuration();
		cfg.addResource("net/sf/hibernate/jconsole/tester/test-message-entity.hbm.xml");
		cfg.setProperties(System.getProperties());

		cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:mymemdb");
		cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbc.JDBCDriver");
		cfg.setProperty("hibernate.connection.username", "SA");
		cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");

		sessionFactory = cfg.buildSessionFactory();
		mBeanServer = ManagementFactory.getPlatformMBeanServer();
		HibernateJmxBinding binding = new HibernateJmxBinding(mBeanServer, sessionFactory);
		binding.registerJmxBinding();
	}

	private HibernateSessions() {
	}
}
