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

import ru.snake.dbunit.generator.model.EditorStateListener;
import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.worker.SaveFileWorker;

/**
 * Show open file dialog and start load content worker.
 *
 * @author snake
 *
 */
public final class SaveFileAction extends AbstractAction implements Action, EditorStateListener {

	private final MainModel model;

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
	public SaveFileAction(final JFrame frame, final MainModel model, final JFileChooser chooser) {
		this.model = model;

		Icon smallIcon = new ImageIcon(ClassLoader.getSystemResource("icons/save-x16.png"));
		Icon largeIcon = new ImageIcon(ClassLoader.getSystemResource("icons/save-x24.png"));

		putValue(NAME, "Save");
		putValue(SHORT_DESCRIPTION, "Save current editor content.");
		putValue(SMALL_ICON, smallIcon);
		putValue(LARGE_ICON_KEY, largeIcon);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);

		setEnabled(false);
		model.addEditorStateListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (model.hasFile()) {
			File file = model.getFile();
			SaveFileWorker worker = new SaveFileWorker(model, file);
			worker.execute();
		}
	}

	@Override
	public void editorStateChanged(final MainModel otherModel, final boolean modified, final File file) {
		if (model == otherModel) {
			setEnabled(modified);
		}
	}

}
