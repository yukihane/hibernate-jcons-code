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

import java.util.*;

/**
 * Is a specialized version for tooltips that adds a newline in from of some HQL commands.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public class ToolTipQueryHighlighter extends QueryHighlighter implements AbstractHighlighter.TokenHighlighter {

	static final EnumSet<Markup> INDENT_BEFORE = EnumSet.of(
			Markup.IN, Markup.INNER, Markup.OUTER, Markup.LEFT, Markup.JOIN, Markup.AND, Markup.OR);

	static final Map<Markup, Set<Markup>> LINE_BREAK_BEFORE = new HashMap<Markup, Set<Markup>>();

	static {
		for (Markup markup : EnumSet.of(
				Markup.IN, Markup.WHERE, Markup.ORDER, Markup.HAVING,
				Markup.FROM, Markup.INNER, Markup.OUTER, Markup.LEFT, Markup.JOIN,
				Markup.AND, Markup.OR)) {
			LINE_BREAK_BEFORE.put(markup, Collections.<Markup>emptySet());
		}
		// Define exceptions (when not to apply a line break)
		LINE_BREAK_BEFORE.put(Markup.AND, EnumSet.of(Markup.BETWEEN));
		LINE_BREAK_BEFORE.put(Markup.JOIN, EnumSet.of(Markup.INNER, Markup.OUTER, Markup.LEFT));
	}

	boolean indent;
	TokenHighlighter tokenHighlighter;
	Markup lastKeyWord;

	@Override
	protected void reset() {
		super.reset();
		lastKeyWord = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TokenHighlighter getHighlighterForToken(Token token, Token nextToken) {
		tokenHighlighter = super.getHighlighterForToken(token, nextToken);
		Markup markup = Markup.find(token);
		indent = INDENT_BEFORE.contains(markup);
		Set<Markup> exceptions = LINE_BREAK_BEFORE.get(markup);
		try {
			return exceptions != null && !exceptions.contains(lastKeyWord) ? this : tokenHighlighter;
		} finally {
			if (markup != null && markup.style == Style.KEYWORD)
				lastKeyWord = markup;
		}
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
