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

package net.sf.hibernate.jconsole.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.management.MBeanServer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Extends the default binding implementation with the capability to
 * auto configure itself in an JPA environment with hibernate as the JPA provider.
 *
 * @author Juergen_Kellerer, 22.11.2009 14:36:42
 */
public class JpaHibernateJmxBinding extends HibernateJmxBinding {

	static SessionFactory toSessionFactory(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			return ((Session) em.getDelegate()).getSessionFactory();
		} finally {
			em.close();
		}
	}

	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;

	/**
	 * Creates a new instance of the HibernateJmxBinding using the given EntityManagerFactory
	 * as source to publish the statistics via JMX.
	 *
	 * @param entityManagerFactory The JPA EntityManagerFactory to monitor.
	 */
	public JpaHibernateJmxBinding(EntityManagerFactory entityManagerFactory) {
		super(toSessionFactory(entityManagerFactory));
	}

	/**
	 * Creates a new instance of the HibernateJmxBinding using the given EntityManagerFactory
	 * as source to publish the statistics via JMX.
	 *
	 * @param mBeanServer		  The MBeanServer to publish the JMX bean to.
	 * @param entityManagerFactory The JPA EntityManagerFactory to monitor.
	 */
	public JpaHibernateJmxBinding(MBeanServer mBeanServer, EntityManagerFactory entityManagerFactory) {
		super(mBeanServer, toSessionFactory(entityManagerFactory));
	}
}
