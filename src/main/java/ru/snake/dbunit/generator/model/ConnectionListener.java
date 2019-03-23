package ru.snake.dbunit.generator.model;

public interface ConnectionListener {

	/**
	 * Called when connection settings changed.
	 *
	 * @param model
	 *            changed model
	 * @param settings
	 *            new settings
	 */
	public void connectionChanged(MainModel model, ConnectionSettings settings);

}
