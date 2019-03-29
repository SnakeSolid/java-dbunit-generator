package ru.snake.dbunit.generator.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import ru.snake.dbunit.generator.Message;

/**
 * Background worker to export given document contents to file.
 *
 * @author snake
 *
 */
public final class ExportFileWorker extends SwingWorker<Void, Void> {

	private final File file;

	private final String text;

	/**
	 * Create new export worker.
	 *
	 * @param file
	 *            file
	 * @param text
	 *            text
	 */
	public ExportFileWorker(final File file, final String text) {
		this.file = file;
		this.text = text;
	}

	@Override
	protected Void doInBackground() throws Exception {
		try (OutputStream os = new FileOutputStream(file, false)) {
			os.write(text.getBytes());
		}

		return null;
	}

	@Override
	protected void done() {
		try {
			get();
		} catch (InterruptedException | ExecutionException e) {
			Message.showError(e);
		}
	}

}
