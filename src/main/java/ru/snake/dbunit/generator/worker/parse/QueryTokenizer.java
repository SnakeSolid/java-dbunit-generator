package ru.snake.dbunit.generator.worker.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class QueryTokenizer {

	private static final char ZERO = '\0';

	private final char[] text;

	private final List<Token> tokens;

	private TokenizerState state;

	private StringBuilder currentToken;

	private int position;

	private QueryTokenizer(final String text) {
		this.text = text.toCharArray();

		this.tokens = new ArrayList<Token>();
		this.state = TokenizerState.NONE;
		this.currentToken = new StringBuilder();
		this.position = 0;
	}

	/**
	 * Tokenize internal text and returns token list.
	 *
	 * @return token list
	 */
	private List<Token> tokenize() {
		while (!isEos()) {
			switch (state) {
			case NONE:
				visitNone();
				break;

			case CODE:
				visitCode();
				break;

			case COMMENT:
				visitComment();
				break;

			case STRING_BODY:
				visitStringBody();
				break;

			case STRING_ESCAPE:
				visitStringEscape();
				break;

			case STRING_END:
				visitStringEnd();
				break;

			case SEMICOLON:
				visitSemicolon();
				break;

			default:
				throw new IllegalStateException("Unexpected internal state: " + state);
			}

			currentToken.append(getCurrentChar());
			position += 1;
		}

		if (currentToken.length() > 0) {
			switch (state) {
			case CODE:
			case STRING_BODY:
			case STRING_ESCAPE:
			case STRING_END:
				pushCode();
				break;

			case COMMENT:
				pushComment();
				break;

			case SEMICOLON:
				pushSemicolon();
				break;

			default:
				throw new IllegalStateException("Unexpected internal state at end of stream: " + state);
			}
		}

		return tokens;
	}

	/**
	 * Process next token in {@link TokenizerState#SEMICOLON} state. Change
	 * state and push next token if it's necessarily.
	 */
	private void visitSemicolon() {
		pushSemicolon();

		if (getCurrentChar() == '-' && getNextChar() == '-') {
			state = TokenizerState.COMMENT;
		} else if (getCurrentChar() == '\'') {
			state = TokenizerState.STRING_BODY;
		} else if (getCurrentChar() == ';') {
			state = TokenizerState.SEMICOLON;
		} else {
			state = TokenizerState.CODE;
		}
	}

	/**
	 * Push semicolon token to internal token list and clear current token.
	 */
	private void pushSemicolon() {
		tokens.add(Token.semicolon());
		currentToken.setLength(0);
	}

	/**
	 * Process next token in {@link TokenizerState#STRING_BODY} state. Change
	 * state and push next token if it's necessarily.
	 */
	private void visitStringEnd() {
		pushCode();

		if (getCurrentChar() == '-' && getNextChar() == '-') {
			state = TokenizerState.COMMENT;
		} else if (getCurrentChar() == ';') {
			state = TokenizerState.SEMICOLON;
		} else {
			state = TokenizerState.CODE;
		}
	}

	/**
	 * Process next token in {@link TokenizerState#STRING_ESCAPE} state. Change
	 * state and push next token if it's necessarily.
	 */
	private void visitStringEscape() {
		state = TokenizerState.STRING_BODY;
	}

	/**
	 * Process next token in {@link TokenizerState#STRING_BODY} state. Change
	 * state and push next token if it's necessarily.
	 */
	private void visitStringBody() {
		if (getCurrentChar() == '\'' && getNextChar() == '\'') {
			state = TokenizerState.STRING_ESCAPE;
		} else if (getCurrentChar() == '\'') {
			state = TokenizerState.STRING_END;
		}
	}

	/**
	 * Process next token in {@link TokenizerState#COMMENT} state. Change state
	 * and push next token if it's necessarily.
	 */
	private void visitComment() {
		if (getCurrentChar() == '\r' || getCurrentChar() == '\n') {
			pushComment();

			state = TokenizerState.CODE;
		}
	}

	/**
	 * Push comment token to internal token list and clear current token.
	 */
	private void pushComment() {
		tokens.add(Token.comment(currentToken.toString()));
		currentToken.setLength(0);
	}

	/**
	 * Process next token in {@link TokenizerState#CODE} state. Change state and
	 * push next token if it's necessarily.
	 */
	private void visitCode() {
		if (getCurrentChar() == '-' && getNextChar() == '-') {
			pushCode();

			state = TokenizerState.COMMENT;
		} else if (getCurrentChar() == '\'') {
			pushCode();

			state = TokenizerState.STRING_BODY;
		} else if (getCurrentChar() == ';') {
			pushCode();

			state = TokenizerState.SEMICOLON;
		}
	}

	/**
	 * Push code token to internal token list and clear current token.
	 */
	private void pushCode() {
		tokens.add(Token.code(currentToken.toString()));
		currentToken.setLength(0);
	}

	/**
	 * Initialize state at start of stream.
	 */
	private void visitNone() {
		if (getCurrentChar() == '-' && getNextChar() == '-') {
			state = TokenizerState.COMMENT;
		} else if (getCurrentChar() == '\'') {
			state = TokenizerState.STRING_BODY;
		} else if (getCurrentChar() == ';') {
			state = TokenizerState.SEMICOLON;
		} else {
			state = TokenizerState.CODE;
		}
	}

	/**
	 * Returns true if end of stream reached, otherwise false.
	 *
	 * @return true if end of stream reached
	 */
	private boolean isEos() {
		return position >= text.length;
	}

	private char getCurrentChar() {
		if (position < text.length) {
			return text[position];
		}

		return ZERO;
	}

	private char getNextChar() {
		if (position + 1 < text.length) {
			return text[position + 1];
		}

		return ZERO;
	}

	@Override
	public String toString() {
		return "QueryTokenizer [text=" + Arrays.toString(text) + ", tokens=" + tokens + ", state=" + state
				+ ", currentToken=" + currentToken + ", position=" + position + "]";
	}

	/**
	 * Tokenize test to token list.
	 *
	 * @param text
	 *            text
	 * @return token list
	 */
	public static List<Token> tokenize(final String text) {
		return new QueryTokenizer(text).tokenize();
	}

}
