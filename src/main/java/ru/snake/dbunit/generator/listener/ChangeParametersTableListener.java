package ru.snake.dbunit.generator.listener;

import java.util.Collections;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.config.DriverConfig;
import ru.snake.dbunit.generator.model.ConnectionParametersTableModel;
import ru.snake.dbunit.generator.model.DriverListModel;

public final class ChangeParametersTableListener implements ListDataListener {

	private final Configuration config;

	private final DriverListModel driverListModel;

	private final ConnectionParametersTableModel parametersModel;

	/**
	 * Creates new update parameters model listener.
	 *
	 * @param config
	 *            configuration
	 * @param driverListModel
	 *            driver list model
	 * @param parametersModel
	 *            parameters model
	 */
	public ChangeParametersTableListener(
		final Configuration config,
		final DriverListModel driverListModel,
		final ConnectionParametersTableModel parametersModel
	) {
		this.config = config;
		this.driverListModel = driverListModel;
		this.parametersModel = parametersModel;

		updateParameters();
	}

	@Override
	public void intervalAdded(final ListDataEvent e) {
	}

	@Override
	public void intervalRemoved(final ListDataEvent e) {
	}

	@Override
	public void contentsChanged(final ListDataEvent e) {
		if (e.getType() == ListDataEvent.CONTENTS_CHANGED) {
			updateParameters();
		}
	}

	/**
	 * Update parameters using selected connection configuration.
	 */
	private void updateParameters() {
		String selectedDriver = String.valueOf(driverListModel.getSelectedItem());
		DriverConfig driverConfig = this.config.getDrivers().get(selectedDriver);

		if (driverConfig != null) {
			this.parametersModel.setParameters(selectedDriver, driverConfig.getParameters());
		} else {
			this.parametersModel.setParameters(selectedDriver, Collections.emptyList());
		}
	}

}
