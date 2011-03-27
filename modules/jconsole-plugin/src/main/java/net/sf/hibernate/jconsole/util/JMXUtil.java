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

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;
import java.util.Properties;

/**
 * Implements utilities around JMX.
 *
 * @author Juergen_Kellerer, 2011-03-20
 * @version 1.0
 */
public final class JMXUtil {

	private static ObjectName operatingSystem;
	private static ObjectName runtimeName;

	/**
	 * Returns an attribute value from the type 'java.lang:type=Runtime'.
	 *
	 * @param mbs		   The server hosting the types.
	 * @param attributeName the name of the attribute to return.
	 * @return the value of the attribute.
	 * @throws Exception if the operation failed.
	 */
	public static Object getRuntimeAttribute(MBeanServerConnection mbs, String attributeName) throws Exception {
		if (runtimeName == null) //NOSONAR - MT is not an issue.
			runtimeName = new ObjectName("java.lang:type=Runtime");
		return mbs.getAttribute(runtimeName, attributeName);
	}

	/**
	 * Returns an attribute value from the type 'java.lang:type=OperatingSystem'.
	 *
	 * @param mbs		   The server hosting the types.
	 * @param attributeName the name of the attribute to return.
	 * @return the value of the attribute.
	 * @throws Exception if the operation failed.
	 */
	public static Object getOperatingSystemAttribute(MBeanServerConnection mbs,
													 String attributeName) throws Exception {
		if (operatingSystem == null) //NOSONAR - MT is not an issue.
			operatingSystem = new ObjectName("java.lang:type=OperatingSystem");
		return mbs.getAttribute(operatingSystem, attributeName);
	}

	/**
	 * Returns the JVMs input (commandline) arguments.
	 *
	 * @param mbs The server hosting the types.
	 * @return the JVMs input (commandline) arguments.
	 * @throws Exception if the operation failed.
	 */
	public static Object[] getInputArguments(MBeanServerConnection mbs) throws Exception {
		return (Object[]) getRuntimeAttribute(mbs, "InputArguments");
	}

	/**
	 * Returns the JVMs classpath.
	 *
	 * @param mbs The server hosting the types.
	 * @return the JVMs classpath.
	 * @throws Exception if the operation failed.
	 */
	public static String[] getClassPath(MBeanServerConnection mbs) throws Exception {
		String pathSeparator = String.valueOf(getOperatingSystemAttribute(mbs, "Name")).contains("Windows") ?
				";" : ":";
		return String.valueOf(getRuntimeAttribute(mbs, "ClassPath")).split(pathSeparator);
	}

	/**
	 * Returns the JVMs system properties.
	 *
	 * @param mbs The server hosting the types.
	 * @return the JVMs system properties.
	 * @throws Exception if the operation failed.
	 */
	public static Properties getSystemProperties(MBeanServerConnection mbs) throws Exception {
		Properties properties = new Properties();

		TabularDataSupport tds = (TabularDataSupport) getRuntimeAttribute(mbs, "SystemProperties");
		for (Object o : tds.values()) {
			CompositeData cd = (CompositeData) o;
			properties.setProperty((String) cd.get("key"), (String) cd.get("value"));
		}

		return properties;
	}

	private JMXUtil() {
	}
}
