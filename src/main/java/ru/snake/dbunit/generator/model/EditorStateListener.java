package ru.snake.dbunit.generator.model;

import java.io.File;

/**
 * Listener for query modification or file change.
 *
 * @author snake
 *
 */
@FunctionalInterface
public interface EditorStateListener {

	/**
	 * Called when query text or file changed.
	 *
	 * @param model
	 *            changed model
	 * @param modified
	 *            is modified
	 * @param file
	 *            file
	 */
	void editorStateChanged(MainModel model, boolean modified, File file);

}
