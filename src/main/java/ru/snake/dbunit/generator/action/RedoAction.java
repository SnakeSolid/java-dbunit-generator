package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

import ru.snake.dbunit.generator.listener.DocumentModifiedListener;

public final class RedoAction extends AbstractAction implements Action {

	private final JFrame frame;

	private final Document document;

	private final UndoManager undoManager;

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
	public RedoAction(final JFrame frame, final Document document, final UndoManager undoManager) {
		this.frame = frame;
		this.document = document;
		this.undoManager = undoManager;

		setEnabled(false);
		document.addDocumentListener(new DocumentModifiedListener(this::update));
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (undoManager.canRedo()) {
			undoManager.redo();
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
			setEnabled(undoManager.canRedo());
		}
	}

}
