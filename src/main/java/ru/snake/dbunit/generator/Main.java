package ru.snake.dbunit.generator;

import java.util.Map;
import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ru.snake.dbunit.generator.config.ConfigNotFoundException;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.config.ConfigurationReader;
import ru.snake.dbunit.generator.config.ReadConfigException;
import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.options.CliOptions;
import ru.snake.dbunit.generator.options.CliOptionsParseException;
import ru.snake.dbunit.generator.options.OptionsParser;

/**
 * Main class.
 *
 * @author snake
 *
 */
public final class Main {

	private static final int EXIT_CONFIGURATIN_ERROR = 1;

	/**
	 * DBUtit data set generator entry point.
	 *
	 * @param args
	 *            command-line arguments
	 */
	public static void main(final String[] args) {
		new Main().run(args);
	}

	/**
	 * Read configuration and shows main frame.
	 *
	 * @param args
	 *            comment line arguments
	 */
	private void run(final String[] args) {
		Map<String, String> env = System.getenv();
		OptionsParser parser = new OptionsParser(args, env);

		try {
			CliOptions options = parser.getOptions();

			try {
				Configuration config = ConfigurationReader.read(options.getConfigFile());
				String title = buildTitle();

				SwingUtilities.invokeLater(() -> showMainFrame(title, config));
			} catch (ConfigNotFoundException | ReadConfigException e) {
				Message.showError(e);

				System.exit(EXIT_CONFIGURATIN_ERROR);
			}
		} catch (CliOptionsParseException e) {
			parser.printHelp();

			System.exit(EXIT_CONFIGURATIN_ERROR);
		}
	}

	/**
	 * Build application title string from system properties. If properties not
	 * available returns default title.
	 *
	 * @return application title string
	 */
	private String buildTitle() {
		Properties properties = ApplicationProperties.readApplicationProperties();
		String name = properties.getProperty(ApplicationProperties.PROPEPTY_NAME, "DBUnit dataset generator");
		String version = properties.getProperty(ApplicationProperties.PROPEPTY_VERSION);

		if (version == null) {
			return name;
		} else {
			return name + " (v" + version + ")";
		}
	}

	/**
	 * Create and show main frame.
	 *
	 * @param title
	 *            application title
	 * @param config
	 *            configuration
	 */
	private void showMainFrame(final String title, final Configuration config) {
		MainModel model = new MainModel();
		MainFrame mainFrame = new MainFrame(title, config, model);
		mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainFrame.setVisible(true);
	}

}
