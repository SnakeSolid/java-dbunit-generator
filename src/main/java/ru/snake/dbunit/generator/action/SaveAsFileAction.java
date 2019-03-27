package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.worker.SaveFileWorker;

/**
 * Show open file dialog and start load content worker.
 *
 * @author snake
 *
 */
public final class SaveAsFileAction extends AbstractAction implements Action {

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
	public SaveAsFileAction(final JFrame frame, final MainModel model, final JFileChooser chooser) {
		this.frame = frame;
		this.model = model;
		this.chooser = chooser;

		putValue(NAME, "Save as...");
		putValue(SHORT_DESCRIPTION, "Save current editor content to selected file.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
		putValue(MNEMONIC_KEY, KeyEvent.VK_A);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		int result = chooser.showSaveDialog(frame);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			SaveFileWorker worker = new SaveFileWorker(model, file);
			worker.execute();
		}
	}

}
