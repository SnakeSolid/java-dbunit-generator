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
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import ru.snake.dbunit.generator.Message;
import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.worker.SaveFileWorker;

/**
 * Show open file dialog and start load content worker.
 *
 * @author snake
 *
 */
public final class NewFileAction extends AbstractAction implements Action {

	private final JFrame frame;

	private final MainModel model;

	private final JFileChooser chooser;

	/**
	 * Create new open file action.
	 *
	 * @param frame
	 *            main frame
	 * @param model
	 *            main model
	 * @param chooser
	 *            file chooser
	 */
	public NewFileAction(final JFrame frame, final MainModel model, final JFileChooser chooser) {
		this.frame = frame;
		this.model = model;
		this.chooser = chooser;

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
		if (model.isModified()) {
			int result = JOptionPane.showConfirmDialog(
				frame,
				"Query modified. Do you want to save it?",
				"Content changed",
				JOptionPane.YES_NO_CANCEL_OPTION
			);

			if (result == JOptionPane.YES_OPTION) {
				if (model.hasFile()) {
					saveAndClearContent(model.getFile());
				} else {
					result = chooser.showSaveDialog(frame);

					if (result == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();

						if (file.exists()) {
							result = JOptionPane.showConfirmDialog(
								frame,
								"This file exists. Do you want to overwrite it?",
								"File exists",
								JOptionPane.YES_NO_OPTION
							);

							if (result == JOptionPane.YES_OPTION) {
								saveAndClearContent(file);
							}
						} else {
							saveAndClearContent(file);
						}
					}
				}
			} else if (result == JOptionPane.NO_OPTION) {
				clearContent();
			}
		} else {
			clearContent();
		}
	}

	/**
	 * Save editor content then clear editor.
	 *
	 * @param file
	 *            file
	 */
	private void saveAndClearContent(final File file) {
		SaveFileWorker worker = new SaveFileWorker(model, file, this::clearContent);
		worker.execute();
	}

	/**
	 * Clear query editor content.
	 */
	private void clearContent() {
		try {
			model.setQueryStateNew();
		} catch (BadLocationException exception) {
			Message.showError(exception);
		}
	}

}
