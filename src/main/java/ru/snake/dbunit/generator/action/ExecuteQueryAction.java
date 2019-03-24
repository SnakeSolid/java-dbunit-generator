package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ru.snake.dbunit.generator.MainFrame;
import ru.snake.dbunit.generator.model.ConnectionListener;
import ru.snake.dbunit.generator.model.ConnectionSettings;
import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.worker.BuildDatasetWorker;

/**
 * Execute query action.
 *
 * @author snake
 *
 */
public final class ExecuteQueryAction extends AbstractAction implements Action, ConnectionListener {

	private final MainFrame mainFrame;

	/**
	 * Create new select connection action.
	 *
	 * @param mainFrame
	 *            main frame
	 */
	public ExecuteQueryAction(final MainFrame mainFrame) {
		this.mainFrame = mainFrame;

		Icon smallIcon = new ImageIcon(ClassLoader.getSystemResource("icons/play-x16.png"));
		Icon largeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/play-x24.png"));

		putValue(NAME, "Execute");
		putValue(SHORT_DESCRIPTION, "Execute all queries to couunt connection and build dataset");
		putValue(SMALL_ICON, smallIcon);
		putValue(LARGE_ICON_KEY, largeIcon);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F5"));
		putValue(MNEMONIC_KEY, KeyEvent.VK_E);

		setEnabled(false);
		mainFrame.getModel().addConnectionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		MainModel model = this.mainFrame.getModel();
		Document queryDocument = model.getQueryDocument();
		Document datesetText = model.getDatasetDocument();
		ConnectionSettings settings = model.getCurrentConnection();

		int queryLength = queryDocument.getLength();

		try {
			String queryText = queryDocument.getText(0, queryLength);
			BuildDatasetWorker worker = new BuildDatasetWorker(queryText, settings, datesetText);

			worker.execute();
		} catch (BadLocationException exception) {
			showError(exception);
		}
	}

	/**
	 * Show exception message dialog.
	 *
	 * @param e
	 *            exception
	 */
	private void showError(final Exception e) {
		JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), null, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void connectionChanged(final MainModel model, final ConnectionSettings settings) {
		if (model == mainFrame.getModel()) {
			setEnabled(settings != null);
		}
	}

}
