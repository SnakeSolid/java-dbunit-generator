package ru.snake.dbunit.generator.config;

import java.awt.Font;

/**
 * Font style configuration.
 *
 * @author snake
 *
 */
public enum FontStyle {

	PLAIN, BOLD, ITALIC, BOLD_ITALIC;

	/**
	 * Convert font style to AWK compatible integer value.
	 *
	 * @return AWT font style
	 */
	public int asInt() {
		switch (this) {
		case PLAIN:
			return Font.PLAIN;

		case BOLD:
			return Font.BOLD;

		case ITALIC:
			return Font.ITALIC;

		case BOLD_ITALIC:
			return Font.BOLD | Font.ITALIC;

		default:
			throw new IllegalStateException("Incorrect font style");
		}
	}

}
