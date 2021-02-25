package ru.snake.dbunit.generator.document;

import java.awt.Color;
import java.util.List;

import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.undo.UndoableEdit;

import ru.snake.dbunit.generator.document.parse.SqlTokenizer;
import ru.snake.dbunit.generator.document.parse.Token;

public final class SqlDocument extends DefaultStyledDocument implements Document {

	private static final StyleContext STYLES = new StyleContext();

	private static final Style DEFAULT_STYLE;

	private static final Style KEYWORD_STYLE;

	private static final Style STRING_STYLE;

	private static final Style COMMENT_STYLE;

	static {
		DEFAULT_STYLE = STYLES.addStyle("default", null);
		KEYWORD_STYLE = STYLES.addStyle("keyword", null);
		STRING_STYLE = STYLES.addStyle("string", null);
		COMMENT_STYLE = STYLES.addStyle("comment", null);

		StyleConstants.setForeground(KEYWORD_STYLE, Color.decode("#7f0055"));
		StyleConstants.setForeground(STRING_STYLE, Color.decode("#2a00ff"));
		StyleConstants.setForeground(COMMENT_STYLE, Color.decode("#3f7f5f"));
		StyleConstants.setBold(KEYWORD_STYLE, true);
	}

	/**
	 * Creates new SQL document.
	 */
	public SqlDocument() {
	}

	@Override
	public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
		super.insertString(offs, str, a);

		processChanges(offs, str.length());
	}

	@Override
	public void remove(final int offs, final int len) throws BadLocationException {
		super.remove(offs, len);

		processChanges(offs, 0);
	}

	@Override
	protected void fireUndoableEditUpdate(final UndoableEditEvent e) {
		UndoableEdit edit = e.getEdit();

		if (edit instanceof DefaultDocumentEvent) {
			DefaultDocumentEvent documentEvent = (DefaultDocumentEvent) edit;
			UndoableEdit undoableEdit = new DocumentEvertWrapper(documentEvent);
			UndoableEditEvent newEvent = new UndoableEditEvent(e.getSource(), undoableEdit);

			super.fireUndoableEditUpdate(newEvent);
		} else {
			super.fireUndoableEditUpdate(e);
		}
	}

	/**
	 * Highlight changed text.
	 *
	 * @param offset
	 *            offset
	 * @param length
	 *            length
	 * @throws BadLocationException
	 *             if error occurred
	 */
	private void processChanges(final int offset, final int length) throws BadLocationException {
		int documentLength = getLength();
		String text = getText(0, documentLength);
		int startOffset = findLineStart(text, offset);
		int endOffset = findLineEnd(text, offset + length);

		setCharacterAttributes(startOffset, endOffset - startOffset, DEFAULT_STYLE, true);

		String changedText = text.substring(startOffset, endOffset);
		List<Token> tokens = SqlTokenizer.tokenize(changedText);

		for (Token token : tokens) {
			switch (token.getType()) {
			case KEYWORD:
				setCharacterAttributes(startOffset + token.getOffset(), token.getLength(), KEYWORD_STYLE, false);
				break;

			case STRING:
				setCharacterAttributes(startOffset + token.getOffset(), token.getLength(), STRING_STYLE, false);
				break;

			case COMMENT:
				setCharacterAttributes(startOffset + token.getOffset(), token.getLength(), COMMENT_STYLE, false);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Returns character index after end of line.
	 *
	 * @param text
	 *            text
	 * @param offset
	 *            start offset
	 * @return end of line
	 */
	private int findLineEnd(final String text, final int offset) {
		int index = text.indexOf('\n', offset);

		if (index == -1) {
			return text.length();
		}

		return index + 1;
	}

	/**
	 * Returns index of first line character.
	 *
	 * @param text
	 *            text
	 * @param offset
	 *            start offset
	 * @return start of line
	 */
	private int findLineStart(final String text, final int offset) {
		int index = text.lastIndexOf('\n', offset);

		if (index == -1) {
			return 0;
		}

		return index + 1;
	}

}
