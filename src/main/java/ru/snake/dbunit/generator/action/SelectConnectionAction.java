package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import ru.snake.dbunit.generator.ConnectionDialog;
import ru.snake.dbunit.generator.MainFrame;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.model.ConnectionSettings;

/**
 * Show connection settings dialog action.
 *
 * @author snake
 *
 */
public final class SelectConnectionAction extends AbstractAction implements Action {

	private final MainFrame mainFrame;

	private final Configuration config;

	/**
	 * Create new select connection action.
	 *
	 * @param mainFrame
	 *            main frame
	 * @param config
	 *            configuration settings
	 */
	public SelectConnectionAction(final MainFrame mainFrame, final Configuration config) {
		this.mainFrame = mainFrame;
		this.config = config;

		Icon smallIcon = new ImageIcon(ClassLoader.getSystemResource("icons/plug-x16.png"));
		Icon largeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/plug-x24.png"));

		putValue(NAME, "Connection...");
		putValue(SHORT_DESCRIPTION, "Select connection to use for build dataset");
		putValue(SMALL_ICON, smallIcon);
		putValue(LARGE_ICON_KEY, largeIcon);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F4"));
		putValue(MNEMONIC_KEY, KeyEvent.VK_C);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		ConnectionDialog dialog = this.mainFrame.getConnectionDialog();
		dialog.setVisible(true);

		if (dialog.hasSelectedConnection()) {
			ConnectionSettings connection = dialog.getSelectedConnection();

			this.mainFrame.getModel().setCurrentConnection(connection);
		}
	}

}
