package ru.snake.dbunit.generator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import ru.snake.dbunit.generator.config.Configuration;

/**
 * Wrapped over default combo box model for connection name list.
 *
 * @author snake
 *
 */
public final class DriverListModel extends DefaultComboBoxModel<String> {

	private static final long serialVersionUID = 2451726644570331613L;

	/**
	 * Create model from configuration.
	 *
	 * @param config
	 *            configuration settings
	 */
	public DriverListModel(final Configuration config) {
		super(getSortedConnections(config));
	}

	/**
	 * Create sorted list of connection names from configuration.
	 *
	 * @param config
	 *            configuration settings
	 * @return vector of names
	 */
	private static Vector<String> getSortedConnections(final Configuration config) {
		List<String> elements = new ArrayList<String>(config.getDrivers().keySet());
		Collections.sort(elements);

		return new Vector<>(elements);
	}

	@Override
	public String toString() {
		return "DriverListModel []";
	}

}
