package ru.snake.dbunit.generator.config;

/**
 * Behavior when query has no comment. Possible two different modes.
 *
 * @author snake
 *
 */
public enum NoTableMode {

	/**
	 * Strict mode. Show error if query has no comments.
	 */
	ERROR,

	/**
	 * Common mode. Use query as template if query has no comments.
	 */
	TEMPLATE,

}
