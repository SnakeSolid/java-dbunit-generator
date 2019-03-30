package ru.snake.dbunit.generator.document.parse;

/**
 * SQL token types.
 *
 * @author snake
 *
 */
public enum TokenType {

	/**
	 * Any text except for keywords, string, comments and whitespace.
	 */
	OTHER,

	/**
	 * SQL keywords.
	 */
	KEYWORD,

	/**
	 * String literals.
	 */
	STRING,

	/**
	 * Single line comments.
	 */
	COMMENT,

}
