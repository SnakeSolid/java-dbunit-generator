package ru.snake.dbunit.generator.action;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import ru.snake.dbunit.generator.ConnectionDialog;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.config.DriverConfig;
import ru.snake.dbunit.generator.model.ConnectionParametersTableModel;
import ru.snake.dbunit.generator.model.ConnectionSettings;
import ru.snake.dbunit.generator.model.DriverListModel;

/**
 * Prepare connection settings to use in worker.
 *
 * @author snake
 *
 */
public final class PrepareConnectionAction extends AbstractAction implements Action {

	private final JDialog dialog;

	private final Configuration config;

	private final DriverListModel driverListModel;

	private final ConnectionParametersTableModel parametersModel;

	private final Consumer<ConnectionSettings> consumer;

	/**
	 * Create new prepare connection action.
	 *
	 * @param dialog
	 *            dialog
	 * @param config
	 *            configuration
	 * @param driverListModel
	 *            driver list model
	 * @param parametersModel
	 *            driver parameters model
	 * @param consumer
	 *            driver settings setter
	 */
	public PrepareConnectionAction(
		final ConnectionDialog dialog,
		final Configuration config,
		final DriverListModel driverListModel,
		final ConnectionParametersTableModel parametersModel,
		final Consumer<ConnectionSettings> consumer
	) {
		this.dialog = dialog;
		this.config = config;
		this.driverListModel = driverListModel;
		this.parametersModel = parametersModel;
		this.consumer = consumer;

		putValue(NAME, "Save settings");
		putValue(SHORT_DESCRIPTION, "Save current connection settings and close dialog.");
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		String selectedDriver = String.valueOf(driverListModel.getSelectedItem());
		DriverConfig driverConfig = this.config.getDrivers().get(selectedDriver);

		if (driverConfig != null) {
			Map<String, String> parameterMap = this.parametersModel.getParameterMap();
			String url = driverConfig.getUrl();

			for (Entry<String, String> entry : parameterMap.entrySet()) {
				String parameter = "{" + entry.getKey() + "}";
				String value = entry.getValue();

				url = url.replace(parameter, value);
			}

			ConnectionSettings settings = new ConnectionSettings(
				driverConfig.getDriverPath(),
				driverConfig.getDriverClass(),
				driverConfig.getTypeMappings(),
				url
			);

			this.consumer.accept(settings);
		}

		this.dialog.setVisible(false);
	}

}
