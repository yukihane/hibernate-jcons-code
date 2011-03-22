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
import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;

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
		final Set<String> searchPaths = buildSearchPaths();

		for (Iterator<String> i = searchPaths.iterator(); i.hasNext();) {
			String path = i.next();

			final String jconsoleExecutable = path + (path.isEmpty() ? "" : File.separator) +
					"jconsole" + (File.separatorChar == '\\' ? ".exe" : "");

			if (path.isEmpty() || new File(jconsoleExecutable).isFile()) {
				List<String> command = buildJConsoleCL(jconsoleExecutable, args);
				ProcessBuilder builder = new ProcessBuilder();
				try {
					builder.command(command).redirectErrorStream(true).start();
					return;
				} catch (IOException e) {
					// next path is tried.
				}
			} else if (!new File(path).exists())
				i.remove();
		}

		JOptionPane.showMessageDialog(null, "Couldn't find 'jconsole' within the paths:\n" +
				String.valueOf(searchPaths).replace(',', '\n') + "\n\n" +
				"Please set 'JDK_HOME', configure 'jconsole' to be available through 'PATHS'\n" +
				"or install a JDK next to the JRE used to start this JAR.",
				"Failed to start 'jconsole'",
				JOptionPane.ERROR_MESSAGE);
	}

	static Set<String> buildSearchPaths() {
		Set<String> searchPaths = new LinkedHashSet<String>();
		searchPaths.add(System.getProperty("jdk.path", "") + File.separator + "bin");
		searchPaths.add(System.getenv("JDK_PATH") + File.separator + "bin");
		searchPaths.add(""); // use PATHS

		searchPaths.add(System.getProperty("java.home") + File.separator + "bin");

		File javaHome = new File(System.getProperty("java.home"));
		File parentDir = javaHome.getParentFile();
		if (parentDir != null && "jre".equalsIgnoreCase(javaHome.getName()))
			parentDir = parentDir.getParentFile();

		addDirsToSearchPath(searchPaths, parentDir);

		handleWindowsSpecificPaths(searchPaths);

		return searchPaths;
	}

	static void handleWindowsSpecificPaths(Set<String> searchPaths) {
		String programFiles = System.getenv("ProgramFiles");
		if (programFiles != null) {
			addDirsToSearchPath(searchPaths, new File(programFiles + File.separator + "Java"));

			String altPath = programFiles.contains("(x86)") ?
					programFiles.replace(" (x86)", "") :
					System.getenv("ProgramFiles(x86)");
			if (altPath != null && !altPath.equals(searchPaths))
				addDirsToSearchPath(searchPaths, new File(altPath + File.separator + "Java"));
		}
	}

	static void addDirsToSearchPath(Set<String> searchPaths, File parentDir) {
		File[] searchDirs = parentDir == null ? null : parentDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});

		if (searchDirs != null)
			for (File searchDir : searchDirs)
				searchPaths.add(searchDir + File.separator + "bin");
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
