package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * Dispose frame action. Disposes given dialog when performed.
 *
 * @author snake
 *
 */
public final class CloseFrameAction extends AbstractAction implements Action {

	private final JFrame frame;

	/**
	 * Create new close frame action.
	 *
	 * @param frame
	 *            frame to dispose
	 */
	public CloseFrameAction(final JFrame frame) {
		this.frame = frame;

		Icon smallIcon = new ImageIcon(ClassLoader.getSystemResource("icons/exit-x16.png"));
		Icon largeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/exit-x24.png"));

		putValue(NAME, "Exit");
		putValue(SHORT_DESCRIPTION, "Exit from application without saving any changes");
		putValue(SMALL_ICON, smallIcon);
		putValue(LARGE_ICON_KEY, largeIcon);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q"));
		putValue(MNEMONIC_KEY, KeyEvent.VK_X);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.frame.dispose();
	}

}
