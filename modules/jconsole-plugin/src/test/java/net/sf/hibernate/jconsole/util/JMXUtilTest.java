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

package net.sf.hibernate.jconsole.util;

import org.junit.Test;

import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Smoke tests the jmx util.
 *
 * @author Juergen_Kellerer, 2011-03-20
 * @version 1.0
 */
public class JMXUtilTest {

	MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

	@Test
	public void testGetOSName() throws Exception {
		assertEquals(System.getProperty("os.name"), JMXUtil.getOperatingSystemAttribute(mbs, "Name"));
	}

	@Test
	public void testGetInputArguments() throws Exception {
		Object[] inputArguments = JMXUtil.getInputArguments(mbs);
		assertNotNull(inputArguments);
	}

	@Test
	public void testGetClassPath() throws Exception {
		String[] expected = System.getProperty("java.class.path").split(File.pathSeparator);
		String[] actual = JMXUtil.getClassPath(mbs);
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testGetSystemProperties() throws Exception {
		Properties systemProperties = JMXUtil.getSystemProperties(mbs);
		assertEquals(System.getProperties(), systemProperties);
	}
}
