package ru.snake.dbunit.generator.config;

/**
 * Data mapping type.
 *
 * @author snake
 *
 */
public enum TypeMapping {

	/**
	 * Value will be encoded as ASCII string.
	 */
	ASCII,

	/**
	 * Value will be encoded as UTF-8 string.
	 */
	UTF8,

	/**
	 * Value will be encoded as HEX string.
	 */
	HEX,

	/**
	 * Value will be encoded as BASE64 string with '[BASE64]' prefix.
	 */
	BASE64,

}
