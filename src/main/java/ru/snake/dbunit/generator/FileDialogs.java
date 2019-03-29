package ru.snake.dbunit.generator;

import java.io.File;
import java.util.function.Consumer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ru.snake.dbunit.generator.model.MainModel;

/**
 * Implement basic file operation dialogs.
 *
 * @author snake
 *
 */
public class FileDialogs {

	private final JFrame parent;

	private final MainModel model;

	/**
	 * Create new dialogs with given parent.
	 *
	 * @param model
	 *            model
	 * @param parent
	 *            parent
	 */
	public FileDialogs(final JFrame parent, final MainModel model) {
		this.parent = parent;
		this.model = model;
	}

	/**
	 * Show change content dialog. Calls given {@code saveAndChangeCallback} if
	 * model changed, otherwise calls {@code changeCallback}.
	 *
	 * @param chooser
	 *            file chooser
	 * @param changeCallback
	 *            open document callback
	 * @param saveAndChangeCallback
	 *            save document and open callback
	 */
	public void showChangeDialog(
		final JFileChooser chooser,
		final Runnable changeCallback,
		final Consumer<File> saveAndChangeCallback
	) {
		if (model.isModified()) {
			int result = JOptionPane.showConfirmDialog(
				parent,
				"Query modified. Do you want to save it?",
				"Content changed",
				JOptionPane.YES_NO_CANCEL_OPTION
			);

			if (result == JOptionPane.YES_OPTION) {
				if (model.hasFile()) {
					saveAndChangeCallback.accept(model.getFile());
				} else {
					result = chooser.showSaveDialog(parent);

					if (result == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();

						if (file.exists()) {
							result = JOptionPane.showConfirmDialog(
								parent,
								"This file exists. Do you want to overwrite it?",
								"File exists",
								JOptionPane.YES_NO_OPTION
							);

							if (result == JOptionPane.YES_OPTION) {
								saveAndChangeCallback.accept(file);
							}
						} else {
							saveAndChangeCallback.accept(file);
						}
					}
				}
			} else if (result == JOptionPane.NO_OPTION) {
				changeCallback.run();
			}
		} else {
			changeCallback.run();
		}
	}

	/**
	 * Show save dialog. Call given {@code saveCallback} if file selected.
	 *
	 * @param chooser
	 *            file chooser
	 * @param saveCallback
	 *            save document callback
	 */
	public void showSaveDialog(final JFileChooser chooser, final Consumer<File> saveCallback) {
		if (model.hasFile()) {
			saveCallback.accept(model.getFile());
		} else {
			int result = chooser.showSaveDialog(parent);

			if (result == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();

				if (file.exists()) {
					result = JOptionPane.showConfirmDialog(
						parent,
						"This file exists. Do you want to overwrite it?",
						"File exists",
						JOptionPane.YES_NO_OPTION
					);

					if (result == JOptionPane.YES_OPTION) {
						saveCallback.accept(file);
					}
				} else {
					saveCallback.accept(file);
				}
			}
		}
	}

	/**
	 * Show save as dialog. Call given {@code saveCallback} if file selected.
	 *
	 * @param chooser
	 *            file chooser
	 * @param saveCallback
	 *            save document callback
	 */
	public void showSaveAsDialog(final JFileChooser chooser, final Consumer<File> saveCallback) {
		int result = chooser.showSaveDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			if (file.exists()) {
				result = JOptionPane.showConfirmDialog(
					parent,
					"This file exists. Do you want to overwrite it?",
					"File exists",
					JOptionPane.YES_NO_OPTION
				);

				if (result == JOptionPane.YES_OPTION) {
					saveCallback.accept(file);
				}
			} else {
				saveCallback.accept(file);
			}
		}
	}

}
