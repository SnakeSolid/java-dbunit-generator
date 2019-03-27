package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import ru.snake.dbunit.generator.Message;
import ru.snake.dbunit.generator.model.MainModel;

/**
 * Show open file dialog and start load content worker.
 *
 * @author snake
 *
 */
public final class NewFileAction extends AbstractAction implements Action {

	private final JFrame frame;

	private final MainModel model;

	/**
	 * Create new open file action.
	 *
	 * @param frame
	 *            main frame
	 * @param model
	 *            main model
	 */
	public NewFileAction(final JFrame frame, final MainModel model) {
		this.frame = frame;
		this.model = model;

		Icon smallIcon = new ImageIcon(ClassLoader.getSystemResource("icons/new-x16.png"));
		Icon largeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/new-x24.png"));

		putValue(NAME, "New");
		putValue(SHORT_DESCRIPTION, "Create new empty query in editor.");
		putValue(SMALL_ICON, smallIcon);
		putValue(LARGE_ICON_KEY, largeIcon);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		putValue(MNEMONIC_KEY, KeyEvent.VK_N);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		try {
			model.setQueryStateNew();
		} catch (BadLocationException exception) {
			Message.showError(exception);
		}
	}

}
