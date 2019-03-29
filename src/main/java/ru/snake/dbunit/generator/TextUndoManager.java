package ru.snake.dbunit.generator;

import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * Wrapper over classic {@link UndoManager} to ignore attribute changes. This
 * manager will ignore all attribute change events.
 *
 * @author snake
 *
 */
public final class TextUndoManager implements UndoableEditListener {

	private final UndoManager undoManager;

	/**
	 * Creates new text undo manger.
	 *
	 * @param undoManager
	 *            undo manager
	 */
	public TextUndoManager(final UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	@Override
	public void undoableEditHappened(final UndoableEditEvent e) {
		UndoableEdit edit = e.getEdit();

		if (edit instanceof DefaultDocumentEvent) {
			DefaultDocumentEvent documentEvent = (DefaultDocumentEvent) edit;
			EventType type = documentEvent.getType();

			if (type == EventType.CHANGE) {
				return;
			}

			undoManager.undoableEditHappened(e);
		}
	}

	/**
	 * Call {@link UndoManager#discardAllEdits()} method for wrapped manager.
	 */
	public void discardAllEdits() {
		undoManager.discardAllEdits();
	}

	/**
	 * Call {@link UndoManager#redo()} method for wrapped manager.
	 *
	 * @throws CannotUndoException
	 *             if error occurred
	 */
	public void redo() throws CannotUndoException {
		undoManager.redo();
	}

	/**
	 * Call {@link UndoManager#canRedo()} method for wrapped manager.
	 *
	 * @return true if redo is possible
	 */
	public boolean canRedo() {
		return undoManager.canRedo();
	}

	/**
	 * Call {@link UndoManager#undo()} method for wrapped manager.
	 *
	 * @throws CannotUndoException
	 *             if error occurred
	 */
	public void undo() throws CannotUndoException {
		undoManager.undo();
	}

	/**
	 * Call {@link UndoManager#canUndo()} method for wrapped manager.
	 *
	 * @return true if undo is possible
	 */
	public boolean canUndo() {
		return undoManager.canUndo();
	}

}
