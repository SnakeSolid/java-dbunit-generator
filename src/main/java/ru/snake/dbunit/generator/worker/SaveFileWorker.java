package ru.snake.dbunit.generator.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.text.Document;

import ru.snake.dbunit.generator.Message;
import ru.snake.dbunit.generator.model.MainModel;

/**
 * Background worker to save current query document contents to file.
 *
 * @author snake
 *
 */
public final class SaveFileWorker extends SwingWorker<Void, Void> {

	private final MainModel model;

	private final File file;

	/**
	 * Create new save worker.
	 *
	 * @param model
	 *            model
	 * @param file
	 *            file
	 */
	public SaveFileWorker(final MainModel model, final File file) {
		this.model = model;
		this.file = file;
	}

	@Override
	protected Void doInBackground() throws Exception {
		Document document = model.getQueryDocument();
		int length = document.getLength();
		String text = document.getText(0, length);

		try (OutputStream os = new FileOutputStream(file, false)) {
			os.write(text.getBytes());
		}

		return null;
	}

	@Override
	protected void done() {
		try {
			get();

			model.setQueryStateSaved(file);
		} catch (InterruptedException | ExecutionException e) {
			Message.showError(e);
		}
	}

}
