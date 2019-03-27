package ru.snake.dbunit.generator.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;

import ru.snake.dbunit.generator.Message;
import ru.snake.dbunit.generator.model.MainModel;

/**
 * Background worker to load file content. When content is loaded update query
 * editor with new value.
 *
 * @author snake
 *
 */
public final class LoadFileWorker extends SwingWorker<String, Void> {

	private static final int BUFFER_SIZE = 8192;

	private final MainModel model;

	private final File file;

	/**
	 * Create new load worker.
	 *
	 * @param model
	 *            model
	 * @param file
	 *            file
	 */
	public LoadFileWorker(final MainModel model, final File file) {
		this.model = model;
		this.file = file;
	}

	@Override
	protected String doInBackground() throws Exception {
		StringBuilder builder = new StringBuilder();
		byte[] buffer = new byte[BUFFER_SIZE];

		try (InputStream is = new FileInputStream(file)) {
			while (true) {
				int len = is.read(buffer);

				if (len == -1) {
					break;
				} else if (len == 0) {
					continue;
				} else {
					String text = new String(buffer, 0, len);

					builder.append(text);
				}
			}
		}

		return builder.toString();
	}

	@Override
	protected void done() {
		try {
			model.setQueryStateLoaded(file, get());
		} catch (InterruptedException | ExecutionException | BadLocationException e) {
			Message.showError(e);
		}
	}

}
