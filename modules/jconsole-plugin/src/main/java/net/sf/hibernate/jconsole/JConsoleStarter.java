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

package net.sf.hibernate.jconsole;

import net.sf.hibernate.jconsole.util.JMXUtil;

import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Searches for "jconsole" in the path and starts it with the required arguments.
 *
 * @author Juergen_Kellerer, 2011-03-20
 * @version 1.0
 */
public class JConsoleStarter {

	private static final String PLUGIN_JAR = "hibernate-jconsole";
	private static final MBeanServer MBS = ManagementFactory.getPlatformMBeanServer();

	public static void main(String[] args) throws Exception {
		ProcessBuilder builder = new ProcessBuilder();

		// TODO: Try multiple paths.
		List<String> command = buildJConsoleCL("jconsole", args);

		builder.command(command).redirectErrorStream(true).start();
	}

	static List<String> buildJConsoleCL(String executable, String[] args) throws Exception {
		List<String> jconsoleCommand = new ArrayList<String>();

		jconsoleCommand.add(executable);

		final String[] classpath = System.getProperty("java.class.path").split(File.pathSeparator);
		for (String cp : classpath)
			if (cp.contains(PLUGIN_JAR) || classpath.length == 1) {
				jconsoleCommand.add("-pluginpath");
				jconsoleCommand.add(cp);
			}

		for (Object arg : JMXUtil.getInputArguments(MBS))
			addInputArgument(jconsoleCommand, String.valueOf(arg));

		jconsoleCommand.addAll(Arrays.asList(args));
		return jconsoleCommand;
	}

	static void addInputArgument(List<String> jconsoleCommand, String arg) {
		StringBuilder jconsoleArg = new StringBuilder();
		if (arg != null && (arg.startsWith("-X") || arg.startsWith("-D"))) {
			jconsoleArg.setLength(0);
			jconsoleArg.append("-J");
			if (arg.startsWith("-D")) {
				String key = arg.substring(2, arg.indexOf('='));
				jconsoleArg.append("-D").append(key).append('=').append(System.getProperty(key));
			} else
				jconsoleArg.append(arg);

			jconsoleCommand.add(jconsoleArg.toString());
		}
	}
}
