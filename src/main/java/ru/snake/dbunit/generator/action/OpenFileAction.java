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

import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.worker.LoadFileWorker;

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
		int result = chooser.showOpenDialog(frame);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			LoadFileWorker worker = new LoadFileWorker(model, file);
			worker.execute();
		}
	}

}
