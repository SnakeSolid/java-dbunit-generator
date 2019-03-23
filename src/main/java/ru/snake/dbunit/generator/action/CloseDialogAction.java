package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 * Dispose dialog action. Disposes given dialog when performed.
 *
 * @author snake
 *
 */
public final class CloseDialogAction extends AbstractAction implements Action {

	private final JDialog dialog;

	/**
	 * Create new close dialog action.
	 *
	 * @param dialog
	 *            dialog to dispose
	 */
	public CloseDialogAction(final JDialog dialog) {
		this.dialog = dialog;

		putValue(NAME, "Cancel");
		putValue(SHORT_DESCRIPTION, "Close this dialog without saving any changes");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ESC"));
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.dialog.setVisible(false);
	}

}
