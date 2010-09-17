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

package net.sf.hibernate.jconsole.formatters;

import java.util.EnumSet;

/**
 * Is a specialized version for tooltips that adds a newline in from of some HQL commands.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public class ToolTipQueryHighlighter extends QueryHighlighter implements AbstractHighlighter.TokenHighlighter {

	static final EnumSet<Markup> INDENT_BEFORE = EnumSet.of(Markup.IN);

	static final EnumSet<Markup> LINE_BREAK_BEFORE = EnumSet.of(
			Markup.IN, Markup.WHERE, Markup.ORDER, Markup.HAVING, Markup.FROM);

	boolean indent;
	TokenHighlighter tokenHighlighter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TokenHighlighter getHighlighterForToken(Token token, Token nextToken) {
		tokenHighlighter = super.getHighlighterForToken(token, nextToken);
		Markup markup = Markup.find(token);
		indent = INDENT_BEFORE.contains(markup);
		return LINE_BREAK_BEFORE.contains(markup) ? this : tokenHighlighter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void highlight(Token token, StringBuilder out) {
		out.append("<br/>");
		if (indent)
			out.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		tokenHighlighter.highlight(token, out);
	}
}
