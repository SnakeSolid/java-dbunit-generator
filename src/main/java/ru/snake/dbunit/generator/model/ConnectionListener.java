package ru.snake.dbunit.generator.model;

/**
 * Listener will be called when connection settings modified.
 *
 * @author snake
 *
 */
@FunctionalInterface
public interface ConnectionListener {

	/**
	 * Called when connection settings changed.
	 *
	 * @param model
	 *            changed model
	 * @param settings
	 *            new settings
	 */
	void connectionChanged(MainModel model, ConnectionSettings settings);

}
