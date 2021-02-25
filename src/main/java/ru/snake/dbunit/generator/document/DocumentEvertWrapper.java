package ru.snake.dbunit.generator.document;

import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Wrapper for {@link DefaultDocumentEvent}. Replaces
 * {@link AbstractUndoableEdit#isSignificant()} method, it will returns false if
 * event type is {@link EventType#CHANGE}. All other methods will be redirected
 * to source event.
 *
 * @author snake
 *
 */
public final class DocumentEvertWrapper extends AbstractUndoableEdit {

	private final DefaultDocumentEvent documentEvent;

	/**
	 * Creates wrapper for document event.
	 *
	 * @param documentEvent
	 */
	public DocumentEvertWrapper(final DefaultDocumentEvent documentEvent) {
		this.documentEvent = documentEvent;
	}

	@Override
	public void undo() throws CannotUndoException {
		documentEvent.undo();
	}

	@Override
	public boolean canUndo() {
		return documentEvent.canUndo();
	}

	@Override
	public void redo() throws CannotRedoException {
		documentEvent.redo();
	}

	@Override
	public boolean canRedo() {
		return documentEvent.canRedo();
	}

	@Override
	public void die() {
		documentEvent.die();
	}

	@Override
	public boolean addEdit(final UndoableEdit anEdit) {
		return documentEvent.addEdit(anEdit);
	}

	@Override
	public boolean replaceEdit(final UndoableEdit anEdit) {
		return documentEvent.replaceEdit(anEdit);
	}

	@Override
	public boolean isSignificant() {
		return documentEvent.getType() != EventType.CHANGE;
	}

	@Override
	public String toString() {
		return "DocumentEvertWrapper [documentEvent=" + documentEvent + "]";
	}

}
