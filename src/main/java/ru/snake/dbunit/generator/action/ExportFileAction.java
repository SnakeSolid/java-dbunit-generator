package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ru.snake.dbunit.generator.FileDialogs;
import ru.snake.dbunit.generator.Message;
import ru.snake.dbunit.generator.listener.DocumentModifiedListener;
import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.worker.ExportFileWorker;

/**
 * Show save file dialog and start export worker.
 *
 * @author snake
 *
 */
public final class ExportFileAction extends AbstractAction implements Action {

	private final JFrame frame;

	private final MainModel model;

	private final JFileChooser chooser;

	/**
	 * Create new export file action.
	 *
	 * @param frame
	 *            main frame
	 * @param model
	 *            main model
	 * @param chooser
	 *            file chooser
	 */
	public ExportFileAction(final JFrame frame, final MainModel model, final JFileChooser chooser) {
		this.frame = frame;
		this.model = model;
		this.chooser = chooser;

		Icon smallIcon = new ImageIcon(ClassLoader.getSystemResource("icons/export-x16.png"));
		Icon largeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/export-x24.png"));

		putValue(NAME, "Export...");
		putValue(SHORT_DESCRIPTION, "Export current data set content to selected file.");
		putValue(SMALL_ICON, smallIcon);
		putValue(LARGE_ICON_KEY, largeIcon);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift E"));
		putValue(MNEMONIC_KEY, KeyEvent.VK_E);

		setEnabled(false);
		model.getDatasetDocument().addDocumentListener(new DocumentModifiedListener(this::checkDataset));
	}

	/**
	 * Check if data set not empty - this action will be enabled.
	 *
	 * @param document
	 *            changed document
	 */
	private void checkDataset(final Document document) {
		if (document == model.getDatasetDocument()) {
			int length = document.getLength();

			setEnabled(length > 0);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		FileDialogs fileDialogs = new FileDialogs(frame, model);
		fileDialogs.showSaveAsDialog(chooser, this::exportContent);
	}

	/**
	 * Export data set content to file.
	 *
	 * @param file
	 *            file
	 */
	private void exportContent(final File file) {
		Document document = model.getDatasetDocument();
		int length = document.getLength();

		try {
			String text = document.getText(0, length);
			ExportFileWorker worker = new ExportFileWorker(file, text);
			worker.execute();
		} catch (BadLocationException e) {
			Message.showError(e);
		}
	}

}
