package ru.snake.dbunit.generator;

import javax.swing.JOptionPane;

/**
 * Wrapper over
 * {@link JOptionPane#showMessageDialog(java.awt.Component, Object, String, int)}
 * to show error messages.
 *
 * @author snake
 *
 */
public final class Message {

	/**
	 * Show exception message dialog.
	 *
	 * @param e
	 *            exception
	 */
	public static void showError(final Exception e) {
		JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), null, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Hide public constructor for utility cless.
	 */
	private Message() {
	}

}
