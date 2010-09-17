/*
 * Copyright (c) 2009
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

package net.sf.hibernate.jconsole.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Displays a text panel explaining how to configure JConsole or VisualVm.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public class HibernateNotFoundContent extends JPanel {

	public static final String RESOURCE = "HibernateNotFound.html";

	JTextPane textPane = new JTextPane();

	public HibernateNotFoundContent() {
		super(new BorderLayout());

		setBorder(new EmptyBorder(10, 10, 10, 10));
		add(BorderLayout.CENTER, new JScrollPane(textPane));
		textPane.setEditable(false);

		try {
			URL resourceUrl = getClass().getClassLoader().getResource(RESOURCE);
			if (resourceUrl != null)
				textPane.setPage(resourceUrl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
