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

/**
 * Test entity taken from public example.
 *
 * @author Juergen_Kellerer, 2010-09-18
 * @version 1.0
 */
public class TestMessageEntity {

	private Long id;
	private String text;
	private TestMessageEntity nextMessage;

	private TestMessageEntity() {
	}

	public TestMessageEntity(String text) {
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public TestMessageEntity getNextMessage() {
		return nextMessage;
	}

	public void setNextMessage(TestMessageEntity nextMessage) {
		this.nextMessage = nextMessage;
	}
}
