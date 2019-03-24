package ru.snake.dbunit.generator.worker.parse;

/**
 * {@link QueryTokenizer} internal state.
 *
 * @author snake
 *
 */
public enum TokenizerState {

	/**
	 * Initial state.
	 */
	NONE,

	/**
	 * Current block is code.
	 */
	CODE,

	/**
	 * Current block is string.
	 */
	STRING_BODY,

	/**
	 * Current block is escape sequence of string literal.
	 */
	STRING_ESCAPE,

	/**
	 * Current block is end of string literal.
	 */
	STRING_END,

	/**
	 * Current block is single line comment.
	 */
	COMMENT,

	/**
	 * Current block is semicolon.
	 */
	SEMICOLON,

}
