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
import java.util.HashMap;
import java.util.Map;

/**
 * Implements highlighting of HQL queries.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public class QueryHighlighter extends AbstractHighlighter {

	static final String BRACES = "()";
	static final String QUOTES = "\"'";
	static final String NUMERIC = "-+.1234567890~^";
	static final String DELIMITERS = "\t\r\n ,".concat(BRACES + QUOTES);
	static final EnumSet<Markup> FROM_SECTION_END = EnumSet.of(
			Markup.RIGHT_BRACE, Markup.WHERE, Markup.ORDER);

	enum Markup {

		SELECT, DISTINCT, FROM, WHERE,
		BETWEEN, AND, OR, IN, HAVING, NOT,
		ORDER, BY, ASC, DESC,
		UPDATE, DELETE, SET, LIKE,

		LEFT_BRACE(Style.OPERATOR, "("),
		RIGHT_BRACE(Style.OPERATOR, ")"),

		OPERATOR_EQUALS(Style.OPERATOR, "="),
		OPERATOR_UNEQUALS1(Style.OPERATOR, "!="),
		OPERATOR_UNEQUALS2(Style.OPERATOR, "<>"),
		OPERATOR_GREATER(Style.OPERATOR, ">"),
		OPERATOR_GREATER_EQUALS(Style.OPERATOR, ">="),
		OPERATOR_LOWER(Style.OPERATOR, "<"),
		OPERATOR_LOWER_EQUALS(Style.OPERATOR, "=<"),;

		static Map<String, Markup> lookupMap = new HashMap<String, Markup>();

		static {
			for (Markup markup : values())
				lookupMap.put(markup.token, markup);
		}

		Style style = Style.KEYWORD;
		String token = name().toLowerCase();

		Markup() {
		}

		Markup(Style style, String token) {
			this.style = style;
			this.token = token.toLowerCase();
		}

		static Markup find(Token token) {
			return lookupMap.get(token.getValue().toLowerCase());
		}
	}


	{
		delimiters = DELIMITERS;
	}

	boolean quoted, betweenFromAndWhere;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		betweenFromAndWhere = quoted = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TokenHighlighter getHighlighterForToken(Token token, Token nextToken) {
		boolean isQuotation = token.isDelimiter() && token.firstCharIn(QUOTES);

		if (quoted || isQuotation) {
			if (isQuotation)
				quoted = !quoted;
			return Style.LITERAL;
		}

		if (token.firstCharIn(":?"))
			return Style.VARIABLE;

		if (nextToken != null && nextToken.firstCharIn("("))
			return Style.FUNCTION;

		Markup m = Markup.find(token);
		if (m != null) {
			if (FROM_SECTION_END.contains(m))
				betweenFromAndWhere = false;
			else if (m == Markup.FROM)
				betweenFromAndWhere = true;

			return m.style;
		}

		return token.containsOnly(NUMERIC) ? Style.NUMBER : (betweenFromAndWhere ? Style.NAME : null);
	}
}
