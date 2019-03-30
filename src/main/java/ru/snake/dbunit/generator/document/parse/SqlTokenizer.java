package ru.snake.dbunit.generator.document.parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simplified SQL tokenizer. Split query text to four different token type -
 * keywords, comments, strings and other code.
 *
 * @author snake
 *
 */
public class SqlTokenizer {

	private static final char ZERO = '\0';

	private static final Set<String> KEYWORDS;

	static {
		KEYWORDS = new HashSet<String>();
		KEYWORDS.add("with");
		KEYWORDS.add("as");
		KEYWORDS.add("select");
		KEYWORDS.add("values");
		KEYWORDS.add("from");
		KEYWORDS.add("inner");
		KEYWORDS.add("left");
		KEYWORDS.add("right");
		KEYWORDS.add("cross");
		KEYWORDS.add("natural");
		KEYWORDS.add("join");
		KEYWORDS.add("using");
		KEYWORDS.add("on");
		KEYWORDS.add("where");
		KEYWORDS.add("having");
		KEYWORDS.add("group");
		KEYWORDS.add("order");
		KEYWORDS.add("limit");
		KEYWORDS.add("for");
		KEYWORDS.add("in");
		KEYWORDS.add("not");
		KEYWORDS.add("and");
		KEYWORDS.add("or");
	}

	private final char[] text;

	private final List<Token> tokens;

	private final StringBuilder currentToken;

	private TokenizerState state;

	private int startPosition;

	private int currentPosition;

	/**
	 * Create new SQL tokenizer for given text.
	 *
	 * @param text
	 *            text
	 */
	public SqlTokenizer(final char[] text) {
		this.text = text;

		this.tokens = new ArrayList<Token>();
		this.state = TokenizerState.NONE;
		this.currentToken = new StringBuilder();
		this.startPosition = 0;
		this.currentPosition = 0;
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

			case IDENTIFIER:
				visitIdenhtifier();
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

			default:
				throw new IllegalStateException("Unexpected internal state: " + state);
			}

			currentToken.append(getCurrentChar());
			currentPosition += 1;
		}

		if (currentToken.length() > 0) {
			switch (state) {
			case IDENTIFIER:
				pushIdentifier();
				break;

			case CODE:
				pushOther();
				break;

			case STRING_BODY:
			case STRING_ESCAPE:
			case STRING_END:
				pushString();
				break;
			//
			case COMMENT:
				pushComment();
				break;

			default:
				throw new IllegalStateException("Unexpected internal state at end of stream: " + state);
			}
		}

		return tokens;
	}

	/**
	 * Process next token in {@link TokenizerState#STRING_BODY} state. Change
	 * state and push next token if it's necessarily.
	 */
	private void visitStringEnd() {
		pushString();

		if (getCurrentChar() == '-' && getNextChar() == '-') {
			state = TokenizerState.COMMENT;
		} else if (isIdentifierPart(getCurrentChar())) {
			state = TokenizerState.IDENTIFIER;
		} else {
			state = TokenizerState.CODE;
		}
	}

	/**
	 * Push comment token to internal token list and clear current token.
	 */
	private void pushString() {
		String value = currentToken.toString();

		tokens.add(Token.string(value, startPosition, currentPosition - startPosition));

		startPosition = currentPosition;
		currentToken.setLength(0);
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
		String value = currentToken.toString();

		tokens.add(Token.comment(value, startPosition, currentPosition - startPosition));

		startPosition = currentPosition;
		currentToken.setLength(0);
	}

	private void visitCode() {
		if (getCurrentChar() == '-' && getNextChar() == '-') {
			pushOther();

			state = TokenizerState.COMMENT;
		} else if (getCurrentChar() == '\'') {
			pushOther();

			state = TokenizerState.STRING_BODY;
		} else if (isIdentifierPart(getCurrentChar())) {
			pushOther();

			state = TokenizerState.IDENTIFIER;
		}
	}

	/**
	 * Push comment token to internal token list and clear current token.
	 */
	private void pushOther() {
		String value = currentToken.toString();

		tokens.add(Token.other(value, startPosition, currentPosition - startPosition));

		startPosition = currentPosition;
		currentToken.setLength(0);
	}

	private void visitIdenhtifier() {
		if (getCurrentChar() == '-' && getNextChar() == '-') {
			pushIdentifier();

			state = TokenizerState.COMMENT;
		} else if (getCurrentChar() == '\'') {
			pushIdentifier();

			state = TokenizerState.STRING_BODY;
		} else if (!isIdentifierPart(getCurrentChar())) {
			pushIdentifier();

			state = TokenizerState.CODE;
		}
	}

	/**
	 * Returns {@code true} if given character is part of identifier.
	 *
	 * @param ch
	 *            char
	 * @return {@code true} if char is part of identifier
	 */
	private boolean isIdentifierPart(final char ch) {
		return Character.isAlphabetic(ch) || ch == '_';
	}

	/**
	 * Push comment token to internal token list and clear current token.
	 */
	private void pushIdentifier() {
		String value = currentToken.toString();

		if (isKeyword(value)) {
			tokens.add(Token.keyword(value, startPosition, currentPosition - startPosition));
		} else {
			tokens.add(Token.other(value, startPosition, currentPosition - startPosition));
		}

		startPosition = currentPosition;
		currentToken.setLength(0);
	}

	/**
	 * Returns {@code true} if given value is keyword.
	 *
	 * @param value
	 *            value
	 * @return {@code true} if value is keyword
	 */
	private boolean isKeyword(final String value) {
		String lower = value.toLowerCase();

		return KEYWORDS.contains(lower);
	}

	/**
	 * Initialize state at start of stream.
	 */
	private void visitNone() {
		if (getCurrentChar() == '-' && getNextChar() == '-') {
			state = TokenizerState.COMMENT;
		} else if (getCurrentChar() == '\'') {
			state = TokenizerState.STRING_BODY;
		} else if (Character.isAlphabetic(getCurrentChar())) {
			state = TokenizerState.IDENTIFIER;
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
		return currentPosition >= text.length;
	}

	/**
	 * Returns current char or zero.
	 *
	 * @return current char
	 */
	private char getCurrentChar() {
		if (currentPosition < text.length) {
			return text[currentPosition];
		}

		return ZERO;
	}

	/**
	 * Returns next char or zero.
	 *
	 * @return next char
	 */
	private char getNextChar() {
		if (currentPosition + 1 < text.length) {
			return text[currentPosition + 1];
		}

		return ZERO;
	}

	public static List<Token> tokenize(String sqlText) {
		char[] text = sqlText.toCharArray();

		return new SqlTokenizer(text).tokenize();
	}

}
