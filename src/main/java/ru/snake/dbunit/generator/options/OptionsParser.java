package ru.snake.dbunit.generator.options;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Parse command line arguments and environment variables to determine options.
 *
 * @author snake
 *
 */
public final class OptionsParser {

	/**
	 * Short command line options.
	 */

	private static final String SOPT_CONFIG = "c";

	/**
	 * Environment variables.
	 */

	private static final String ENV_CONFIG = "DBUNITGEN_CONFIG";

	/**
	 * Default option values.
	 */

	private static final String DEFAULT_CONFIG = "config.yaml";

	private final String[] arguments;

	private final Map<String, String> environment;

	private Options options;

	/**
	 * Create new parser from options from arguments and environment.
	 *
	 * @param arguments
	 *            command line arguments
	 * @param environment
	 *            environment variables
	 */
	public OptionsParser(final String[] arguments, final Map<String, String> environment) {
		this.arguments = arguments;
		this.environment = environment;

		this.options = createOptions();
	}

	/**
	 * Print usage info to STDOUT.
	 */
	public void printHelp() {
		HelpFormatter formatter = new HelpFormatter();

		formatter.printHelp("pgdiff", options);
	}

	/**
	 * Parse and returns application options. Command line options have priority
	 * over environment variables. Default value will be used of all other
	 * option not defined.
	 *
	 * @return application options
	 * @throws NoParameterException
	 *             if required parameters missed
	 * @throws CliOptionsParseException
	 *             if command line option are invalid
	 */
	public CliOptions getOptions() throws NoParameterException, CliOptionsParseException {
		CommandLine commandLine;

		try {
			commandLine = new DefaultParser().parse(this.options, this.arguments);
		} catch (ParseException e) {
			throw new CliOptionsParseException(e);
		}

		String config = getDefaultOption(commandLine, SOPT_CONFIG, ENV_CONFIG, DEFAULT_CONFIG);
		File configFile = new File(config);

		return new CliOptions.Builder().setConfigFile(configFile).build();
	}

	/**
	 * Return parameter value. If given command line option defined, return it's
	 * value. Next if given environment variable defined, return it's value.
	 * Otherwise returns default value.
	 *
	 * @param commandLine
	 *            command line options
	 * @param optName
	 *            option name
	 * @param envVar
	 *            variable name
	 * @param defaultValue
	 *            default value
	 * @return option value
	 */
	private String getDefaultOption(
		final CommandLine commandLine,
		final String optName,
		final String envVar,
		final String defaultValue
	) {
		String argument = commandLine.getOptionValue(optName);

		if (argument != null) {
			return argument;
		}

		argument = this.environment.get(envVar);

		if (argument != null) {
			return argument;
		}

		return defaultValue;
	}

	/**
	 * Create and returns new {@link Options}.
	 *
	 * @return options instance
	 */
	private static Options createOptions() {
		Option config = Option.builder(SOPT_CONFIG)
			.longOpt("config")
			.argName("PATH")
			.hasArg()
			.desc("Path to configuration file.")
			.build();

		Options options = new Options();
		options.addOption(config);

		return options;
	}

	@Override
	public String toString() {
		return "OptionsParser [arguments=" + Arrays.toString(arguments) + ", environment=" + environment + ", options="
				+ options + "]";
	}

}
