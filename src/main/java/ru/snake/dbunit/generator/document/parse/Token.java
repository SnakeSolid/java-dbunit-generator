package ru.snake.dbunit.generator.document.parse;

/**
 * Represents SQL token with type and stream position.
 *
 * @author snake
 *
 */
public final class Token {

	private final TokenType type;

	private final String value;

	private final int offset;

	private final int length;

	/**
	 * Creates new token with given type.
	 *
	 * @param value
	 *            value
	 * @param type
	 *            token type
	 * @param offset
	 *            offset
	 * @param length
	 *            length
	 */
	private Token(final TokenType type, final String value, final int offset, final int length) {
		this.type = type;
		this.value = value;
		this.offset = offset;
		this.length = length;
	}

	/**
	 * Returns given token type.
	 *
	 * @return token type
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * Returns given value.
	 *
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns given offset.
	 *
	 * @return offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns given length.
	 *
	 * @return length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Create new token with type {@link TokenType#STRING} and given value.
	 *
	 * @param value
	 *            value
	 * @param offset
	 *            offset
	 * @param length
	 *            length
	 * @return new token
	 */
	public static Token string(final String value, final int offset, final int length) {
		return new Token(TokenType.STRING, value, offset, length);
	}

	/**
	 * Create new token with type {@link TokenType#COMMENT} and given value.
	 *
	 * @param value
	 *            value
	 * @param offset
	 *            offset
	 * @param length
	 *            length
	 * @return new token
	 */
	public static Token comment(final String value, final int offset, final int length) {
		return new Token(TokenType.COMMENT, value, offset, length);
	}

	/**
	 * Create new token with type {@link TokenType#OTHER} and given value.
	 *
	 * @param value
	 *            value
	 * @param offset
	 *            offset
	 * @param length
	 *            length
	 * @return new token
	 */
	public static Token other(final String value, final int offset, final int length) {
		return new Token(TokenType.OTHER, value, offset, length);
	}

	/**
	 * Create new token with type {@link TokenType#KEYWORD} and given value.
	 *
	 * @param value
	 *            value
	 * @param offset
	 *            offset
	 * @param length
	 *            length
	 * @return new token
	 */
	public static Token keyword(final String value, final int offset, final int length) {
		return new Token(TokenType.KEYWORD, value, offset, length);
	}

	@Override
	public String toString() {
		return "Token [type=" + type + ", value=" + value + ", offset=" + offset + ", length=" + length + "]";
	}

}
