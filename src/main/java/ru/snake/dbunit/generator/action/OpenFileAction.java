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
import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.worker.LoadFileWorker;
import ru.snake.dbunit.generator.worker.SaveFileWorker;

/**
 * Show open file dialog and start load content worker.
 *
 * @author snake
 *
 */
public final class OpenFileAction extends AbstractAction implements Action {

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
	public OpenFileAction(final JFrame frame, final MainModel model, final JFileChooser chooser) {
		this.frame = frame;
		this.model = model;
		this.chooser = chooser;

		Icon smallIcon = new ImageIcon(ClassLoader.getSystemResource("icons/open-x16.png"));
		Icon largeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/open-x24.png"));

		putValue(NAME, "Open...");
		putValue(SHORT_DESCRIPTION, "Open selected file in editor.");
		putValue(SMALL_ICON, smallIcon);
		putValue(LARGE_ICON_KEY, largeIcon);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		putValue(MNEMONIC_KEY, KeyEvent.VK_O);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		FileDialogs fileDialogs = new FileDialogs(frame, model);
		fileDialogs.showChangeDialog(chooser, this::loadContent, this::saveAndLoadContent);
	}

	/**
	 * Save editor content then clear editor.
	 *
	 * @param file
	 *            file
	 */
	private void saveAndLoadContent(final File file) {
		Document document = model.getQueryDocument();
		int length = document.getLength();

		try {
			String text = document.getText(0, length);
			SaveFileWorker worker = new SaveFileWorker(model, file, text, this::loadContent);
			worker.execute();
		} catch (BadLocationException e) {
			Message.showError(e);
		}
	}

	/**
	 * Load content from selected.
	 */
	private void loadContent() {
		int result = chooser.showOpenDialog(frame);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			LoadFileWorker worker = new LoadFileWorker(model, file);
			worker.execute();
		}
	}

}
