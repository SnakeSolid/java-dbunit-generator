package ru.snake.dbunit.generator.listener;

import java.util.function.Consumer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Wrapper over document modification. To call the same function for all
 * modifications
 *
 * @author snake
 *
 */
public final class DocumentModifiedListener implements DocumentListener {

	private final Consumer<Document> callback;

	/**
	 * Create new modification listener.
	 *
	 * @param callback
	 *            callback
	 */
	public DocumentModifiedListener(final Consumer<Document> callback) {
		this.callback = callback;
	}

	@Override
	public void insertUpdate(final DocumentEvent e) {
		callback.accept(e.getDocument());
	}

	@Override
	public void removeUpdate(final DocumentEvent e) {
		callback.accept(e.getDocument());
	}

	@Override
	public void changedUpdate(final DocumentEvent e) {
		callback.accept(e.getDocument());
	}

}
