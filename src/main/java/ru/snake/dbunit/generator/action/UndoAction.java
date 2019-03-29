package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.text.Document;

import ru.snake.dbunit.generator.TextUndoManager;
import ru.snake.dbunit.generator.listener.DocumentModifiedListener;

public final class UndoAction extends AbstractAction implements Action {

	private final JFrame frame;

	private final Document document;

	private final TextUndoManager undoManager;

	/**
	 * Creates new undo action for given undo manager.
	 *
	 * @param frame
	 *            parent frame
	 * @param document
	 *            document
	 * @param undoManager
	 *            undo manager
	 */
	public UndoAction(final JFrame frame, final Document document, final TextUndoManager undoManager) {
		this.frame = frame;
		this.document = document;
		this.undoManager = undoManager;

		setEnabled(false);
		document.addDocumentListener(new DocumentModifiedListener(this::update));
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (undoManager.canUndo()) {
			undoManager.undo();
		}
	}

	/**
	 * Set enabled state when document changed.
	 *
	 * @param modifiedDocument
	 *            document
	 */
	private void update(final Document modifiedDocument) {
		if (this.document == modifiedDocument) {
			setEnabled(undoManager.canUndo());
		}
	}

}
