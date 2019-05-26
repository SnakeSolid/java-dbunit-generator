package ru.snake.dbunit.generator.worker.mapper;

/**
 * XML escape utilities. Contains methods to check and escape XML special
 * characters.
 *
 * @author snake
 *
 */
public final class XmlEscape {

	/**
	 * Returns {@code true} if given character must be escaped for XML.
	 *
	 * @param ch
	 *            character to check
	 * @return true if char must be escaped
	 */
	public boolean isEscapeableChar(final char ch) {
		switch (ch) {
		case '"':
		case '&':
		case '\'':
		case '<':
		case '>':
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns escape string. If given character not escaped returns
	 * {@code null}.
	 *
	 * @param ch
	 *            character to escape
	 * @return escaped string or null
	 */
	public String escapeChar(final char ch) {
		switch (ch) {
		case '"':
			return ("&quot;");
		case '&':
			return ("&amp;");
		case '\'':
			return ("&apos;");
		case '<':
			return ("&lt;");
		case '>':
			return ("&gt;");
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return "XmlEscape []";
	}

}
