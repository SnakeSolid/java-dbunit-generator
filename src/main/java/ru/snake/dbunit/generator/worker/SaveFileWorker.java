package ru.snake.dbunit.generator.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

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

	private final String text;

	private final Optional<Runnable> callback;

	/**
	 * Create new save worker.
	 *
	 * @param model
	 *            model
	 * @param file
	 *            file
	 * @param text
	 *            text
	 */
	public SaveFileWorker(final MainModel model, final File file, final String text) {
		this.model = model;
		this.file = file;
		this.text = text;
		this.callback = Optional.empty();
	}

	/**
	 * Create new save worker.
	 *
	 * @param model
	 *            model
	 * @param file
	 *            file
	 * @param text
	 *            text
	 * @param callback
	 *            callback
	 */
	public SaveFileWorker(final MainModel model, final File file, final String text, final Runnable callback) {
		this.model = model;
		this.file = file;
		this.text = text;
		this.callback = Optional.of(callback);
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

			model.setQueryStateSaved(file);
			callback.ifPresent(Runnable::run);
		} catch (InterruptedException | ExecutionException e) {
			Message.showError(e);
		}
	}

}
