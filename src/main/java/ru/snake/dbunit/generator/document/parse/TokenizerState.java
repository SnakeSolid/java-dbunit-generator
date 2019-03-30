package ru.snake.dbunit.generator.document.parse;

/**
 * SQL tokenizer internal state.
 *
 * @author snake
 *
 */
public enum TokenizerState {

	/**
	 * Current block is not defined.
	 */
	NONE,

	/**
	 * Current block is identifier or keyword.
	 */
	IDENTIFIER,

	/**
	 * Current block id code unrelated to identifier: whitespace, braces,
	 * numbers etc.
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

}
