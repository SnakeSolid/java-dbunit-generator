package ru.snake.dbunit.generator.model;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Wrapper over document modification. To call the same function for all
 * modifications
 *
 * @author snake
 *
 */
public final class DocumentModifiedListener implements DocumentListener {

	private final Runnable callback;

	/**
	 * Create new modification listener.
	 *
	 * @param callback
	 *            callback
	 */
	public DocumentModifiedListener(final Runnable callback) {
		this.callback = callback;
	}

	@Override
	public void insertUpdate(final DocumentEvent e) {
		callback.run();
	}

	@Override
	public void removeUpdate(final DocumentEvent e) {
		callback.run();
	}

	@Override
	public void changedUpdate(final DocumentEvent e) {
		callback.run();
	}

}
