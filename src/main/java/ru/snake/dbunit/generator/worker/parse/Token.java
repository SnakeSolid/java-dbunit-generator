package ru.snake.dbunit.generator.worker.parse;

/**
 * Single query token. Contains type and string representation.
 *
 * @author snake
 *
 */
public final class Token {

	private final TokenType type;

	private final String value;

	/**
	 * Creates new token with given tyle and value.
	 *
	 * @param type
	 *            token type
	 * @param value
	 *            string value
	 */
	private Token(final TokenType type, final String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Returns token type.
	 *
	 * @return type
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * Return string value or null.
	 *
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	public static Token code(final String value) {
		return new Token(TokenType.CODE, value);
	}

	public static Token comment(final String value) {
		return new Token(TokenType.COMMENT, value);
	}

	public static Token semicolon() {
		return new Token(TokenType.SEMICOLON, null);
	}

	@Override
	public String toString() {
		return "Token [type=" + type + ", value=" + value + "]";
	}

}
