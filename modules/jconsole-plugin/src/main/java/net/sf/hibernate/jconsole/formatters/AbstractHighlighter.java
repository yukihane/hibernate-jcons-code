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

import java.util.StringTokenizer;

/**
 * Is a simple abstract base for string highlighting.
 *
 * @author Juergen_Kellerer, 2009-11-20
 * @version 1.0
 */
public abstract class AbstractHighlighter {
	/**
	 * An enumeration of pre-defined styles to be used by sub-classes.
	 */
	public enum Style implements TokenHighlighter {

		COMMENT("color: 808080;"),
		MARKUP("color: black;"),
		LITERAL("font-weight:bold; color: 267F00;"),
		KEYWORD("font-weight:bold; color: 004A7F;"),
		FUNCTION("font-style: italic; color: 0094FF;"),
		NUMBER("color: FF6A00;"),
		VARIABLE("color: FF006E;"),
		OPERATOR("font-weight:bold; color: black;"),
		NAME("font-weight:bold;"),;

		static final String HTML_SPECIAL_CHARS = "<>&";
		static final String[] HTML_SPECIAL_CHARS_ENTITIES = {"&lt;", "&gt;", "&amp;"};

		private String css;

		private Style(String css) {
			this.css = css;
		}

		public void highlight(Token token, StringBuilder out) {
			out.append("<span style=\"").append(css).append("\">");

			StringTokenizer tokenizer = new StringTokenizer(token.getValue(), HTML_SPECIAL_CHARS, true);
			while (tokenizer.hasMoreTokens()) {
				String t = tokenizer.nextToken();
				int entityIndex = HTML_SPECIAL_CHARS.indexOf(t.charAt(0));
				if (entityIndex == -1)
					out.append(t);
				else
					out.append(HTML_SPECIAL_CHARS_ENTITIES[entityIndex]);
			}

			out.append("</span>");
		}
	}

	/**
	 * Represents a parsed token.
	 */
	protected interface Token {

		/**
		 * Returns true if this token is a delimiter.
		 *
		 * @return true if this token is a delimiter.
		 */
		boolean isDelimiter();

		/**
		 * Returns the token value.
		 *
		 * @return the token value.
		 */
		String getValue();

		/**
		 * Returns true if the token is made only of the given chars.
		 *
		 * @param chars the characters that may be contained in the token.
		 * @return true if the token is made only of the given chars.
		 */
		boolean containsOnly(String chars);

		/**
		 * Returns true if the first char is in the given chars.
		 *
		 * @param chars the chars to check the first token char against.
		 * @return true if the first char is in the given chars.
		 */
		boolean firstCharIn(String chars);

		/**
		 * Returns true if the last char is in the given chars.
		 *
		 * @param chars the chars to check the last token char against.
		 * @return true if the last char is in the given chars.
		 */
		boolean lastCharIn(String chars);
	}

	/**
	 * Describes a token highlighter
	 */
	protected interface TokenHighlighter {
		void highlight(Token token, StringBuilder out);
	}

	private final class TokenImplementation implements Token {

		String value;

		private TokenImplementation(String value) {
			this.value = value;
		}

		public boolean isDelimiter() {
			return delimiters.indexOf(value.charAt(0)) != -1;
		}

		public String getValue() {
			return value;
		}

		public boolean containsOnly(String chars) {
			for (char c : value.toCharArray())
				if (chars.indexOf(c) == -1)
					return false;
			return true;
		}

		public boolean firstCharIn(String chars) {
			return chars.indexOf(value.charAt(0)) != -1;
		}

		public boolean lastCharIn(String chars) {
			return chars.indexOf(value.charAt(value.length() - 1)) != -1;
		}
	}

	/**
	 * The delimiters used for parsing.
	 */
	protected String delimiters = "\t\n\r ";

	/**
	 * Highlight the given input string.
	 *
	 * @param input The input to highlight.
	 * @return The highlighted string.
	 */
	public String highlight(String input) {
		reset();

		StringBuilder output = new StringBuilder(input.length() * 2);
		output.append("<html>");

		Token token, nextToken = null;
		StringTokenizer tokenizer = new StringTokenizer(input, delimiters, true);

		while (tokenizer.hasMoreTokens() || nextToken != null) {
			if (nextToken == null)
				nextToken = new TokenImplementation(tokenizer.nextToken());

			token = nextToken;
			if (tokenizer.hasMoreTokens())
				nextToken = new TokenImplementation(tokenizer.nextToken());
			else
				nextToken = null;

			TokenHighlighter hl = getHighlighterForToken(token, nextToken);
			if (hl != null)
				hl.highlight(token, output);
			else
				output.append(token.getValue());
		}

		return output.append("</html>").toString();
	}

	/**
	 * Resets the highlighter to its initial state.
	 */
	protected abstract void reset();

	/**
	 * Is implemented by sub classes to highlight the given token.
	 *
	 * @param token	 The token to highlight-
	 * @param nextToken The following token or 'null' token is the last one.
	 * @return A highlighter for the token or 'null' if no highlighting should be performed.
	 */
	protected abstract TokenHighlighter getHighlighterForToken(Token token, Token nextToken);
}
