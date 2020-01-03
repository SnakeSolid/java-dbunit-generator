package ru.snake.dbunit.generator.worker;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import ru.snake.dbunit.generator.Message;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.config.TableNameCase;
import ru.snake.dbunit.generator.model.ConnectionSettings;
import ru.snake.dbunit.generator.worker.dataset.DatasetBuilder;
import ru.snake.dbunit.generator.worker.dataset.TableRow;
import ru.snake.dbunit.generator.worker.dataset.TableRowBuilder;
import ru.snake.dbunit.generator.worker.mapper.ColumnMapper;
import ru.snake.dbunit.generator.worker.mapper.MapperBuilder;
import ru.snake.dbunit.generator.worker.parse.QueryParser;
import ru.snake.dbunit.generator.worker.query.Query;

/**
 * Background worker. Worker read queries from text, executes every query using
 * given connection setting. All retrieved data-sets will be converted to DBUnit
 * XML representation.
 *
 * @author snake
 *
 */
public final class BuildDatasetWorker extends SwingWorker<Result<String, String>, Void> {

	private final Configuration config;

	private final String queryText;

	private final ConnectionSettings connectionSettings;

	private final Document outputDocument;

	/**
	 * Create new worker to perform building data-set from given query list.
	 *
	 * @param config
	 *            configuration settings
	 * @param queryText
	 *            string with queries
	 * @param connectionSettings
	 *            connection settings
	 * @param outputDocument
	 *            output document
	 */
	public BuildDatasetWorker(
		final Configuration config,
		final String queryText,
		final ConnectionSettings connectionSettings,
		final Document outputDocument
	) {
		this.config = config;
		this.queryText = queryText;
		this.connectionSettings = connectionSettings;
		this.outputDocument = outputDocument;
	}

	@Override
	protected Result<String, String> doInBackground() throws Exception {
		List<Query> allQueries = QueryParser.parse(this.queryText);

		if (allQueries.isEmpty()) {
			return Result.error("Dataset must have at least one query.");
		}

		QueryFilter queryFilter = new QueryFilter(
			config.getNoTableMode(),
			config.getTemplateTableName(),
			config.getSkipTablePrefix()
		);
		Result<List<Query>, String> filterResult = queryFilter.filter(allQueries);

		if (filterResult.isError()) {
			return Result.error(filterResult.getError());
		}

		List<Query> queries = filterResult.getValue();

		if (queries.isEmpty()) {
			return Result.error("Dataset must have at least one executable query.");
		}

		String driverClassName = connectionSettings.getDriverClass();
		String connectionUrl = connectionSettings.getUrl();
		URL url = new URL(connectionSettings.getDriverPath());
		URL[] urls = new URL[] { url };

		try (URLClassLoader classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader())) {
			Class<?> driverClass = classLoader.loadClass(driverClassName);

			if (Driver.class.isAssignableFrom(driverClass)) {
				registerWrappedDriver(driverClass);
			} else {
				return Result.error("Driver class " + driverClassName + " does not implement java.sql.Driver.");
			}

			DatasetBuilder datasetBuilder = new DatasetBuilder();

			try (Connection connection = DriverManager.getConnection(connectionUrl);
					Statement statement = connection.createStatement()) {
				for (Query query : queries) {
					fillQueryDataset(datasetBuilder, statement, query);
				}
			}

			return Result.ok(datasetBuilder.build());
		}
	}

	/**
	 * Executes given query and put all collected row to data set.
	 *
	 * @param datasetBuilder
	 *            data set builder
	 * @param statement
	 *            JDBC statement
	 * @param query
	 *            query
	 * @throws SQLException
	 *             if error occurred
	 */
	private void fillQueryDataset(final DatasetBuilder datasetBuilder, final Statement statement, final Query query)
			throws SQLException {
		MapperBuilder builder = new MapperBuilder(connectionSettings);
		String queryString = query.getQueryText();
		String tableName = getQueryTableName(query);

		datasetBuilder.ensureTable(tableName);

		try (ResultSet resultSet = statement.executeQuery(queryString)) {
			List<ColumnMapper> mappers = builder.buildMappers(resultSet);

			while (resultSet.next()) {
				TableRow tableRow = getTableRow(resultSet, tableName, mappers);

				if (!tableRow.isEmpty()) {
					datasetBuilder.pushRow(tableRow);
				}
			}
		}
	}

	/**
	 * Returns query table name with expected in configuration case. If case not
	 * defined - table name will not be changed.
	 *
	 * @param query
	 *            query
	 * @return table name
	 */
	private String getQueryTableName(final Query query) {
		String tableName = query.getTableName();
		TableNameCase nameCase = connectionSettings.getTableNameCase();

		if (nameCase == null) {
			return tableName;
		}

		switch (nameCase) {
		case UPPER:
			return tableName.toUpperCase();

		case LOWER:
			return tableName.toLowerCase();

		default:
			throw new IllegalArgumentException("Unexpected table case: " + nameCase);
		}
	}

	/**
	 * Generate string representation of single table row.
	 *
	 * @param resultSet
	 *            result set
	 * @param tableName
	 *            table name
	 * @param mappers
	 *            column mappers
	 * @return table row
	 * @throws SQLException
	 *             if error occurred
	 */
	private TableRow getTableRow(final ResultSet resultSet, final String tableName, final List<ColumnMapper> mappers)
			throws SQLException {
		TableRowBuilder builder = new TableRowBuilder(tableName);

		for (ColumnMapper mapper : mappers) {
			String value = mapper.map(resultSet);

			if (value != null) {
				builder.push(mapper.getColumnName(), value);
			}
		}

		return builder.build();
	}

	/**
	 * Unregister all available drivers from {@link DriverManager}. Wraps driver
	 * to {@link DriverWrapper} and register it in {@link DriverManager}.
	 *
	 * @param driverClass
	 *            driver class
	 * @throws InstantiationException
	 *             if instance not created
	 * @throws IllegalAccessException
	 *             if constructor private
	 * @throws SQLException
	 *             if error occurred
	 */
	private void registerWrappedDriver(final Class<?> driverClass)
			throws InstantiationException, IllegalAccessException, SQLException {
		Driver driver = (Driver) driverClass.newInstance();
		DriverWrapper wrapper = new DriverWrapper(driver);

		// Deregister created driver if it was registered in static initializer.
		DriverDeregistrator.deregisterAll();
		// Register wrapped driver to allow access to it from this class loader.
		DriverManager.registerDriver(wrapper);
	}

	@Override
	protected void done() {
		Result<String, String> result;

		try {
			result = get();
		} catch (InterruptedException | ExecutionException e) {
			result = Result.error(unrollMessages(e));
		}

		try {
			AttributeSet attributes = SimpleAttributeSet.EMPTY;
			int length = this.outputDocument.getLength();
			this.outputDocument.remove(0, length);

			if (result.isError()) {
				this.outputDocument.insertString(0, result.getError(), attributes);
			} else {
				this.outputDocument.insertString(0, result.getValue(), attributes);
			}
		} catch (BadLocationException e) {
			Message.showError(e);
		}
	}

	/**
	 * Unroll exception messages to multi-line string.
	 *
	 * @param exception
	 *            exception
	 * @return formatted message
	 */
	private String unrollMessages(final Exception exception) {
		StringBuilder builder = new StringBuilder();
		Throwable cause = exception.getCause();

		builder.append("Error > ");
		builder.append(exception.getLocalizedMessage());

		if (cause != null) {
			builder.append("\n  Caused by > ");
			builder.append(cause.getLocalizedMessage());
		}

		for (Throwable suppressed : exception.getSuppressed()) {
			builder.append("\n  Suppressed > ");
			builder.append(suppressed.getLocalizedMessage());
		}

		return builder.toString();
	}

}
