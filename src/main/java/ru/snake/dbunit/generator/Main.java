package ru.snake.dbunit.generator;

import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ru.snake.dbunit.generator.config.ConfigNotFoundException;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.config.ConfigurationReader;
import ru.snake.dbunit.generator.config.ReadConfigException;
import ru.snake.dbunit.generator.model.MainModel;
import ru.snake.dbunit.generator.options.CliOptions;
import ru.snake.dbunit.generator.options.CliOptionsParseException;
import ru.snake.dbunit.generator.options.NoParameterException;
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

				SwingUtilities.invokeLater(() -> showMainFrame(config));
			} catch (ConfigNotFoundException | ReadConfigException e) {
				Message.showError(e);

				System.exit(EXIT_CONFIGURATIN_ERROR);
			}
		} catch (CliOptionsParseException e) {
			parser.printHelp();

			System.exit(EXIT_CONFIGURATIN_ERROR);
		} catch (NoParameterException e) {
			System.err.println(e.getMessage());

			System.exit(EXIT_CONFIGURATIN_ERROR);
		}
	}

	/**
	 * Create and show main frame.
	 *
	 * @param config
	 *            configuration
	 */
	private void showMainFrame(final Configuration config) {
		MainModel model = new MainModel(config);
		MainFrame mainFrame = new MainFrame(config, model);
		mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainFrame.setVisible(true);
	}

}
